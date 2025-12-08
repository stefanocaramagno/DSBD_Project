package flights.datacollector.service;

import flights.datacollector.api.dto.UserInterestRequest;
import flights.datacollector.api.dto.UserInterestResponse;
import flights.datacollector.client.UserValidationGrpcClient;
import flights.datacollector.domain.Airport;
import flights.datacollector.domain.UserAirportInterest;
import flights.datacollector.exception.AirportNotFoundException;
import flights.datacollector.exception.InterestNotFoundException;
import flights.datacollector.exception.UserNotFoundException;
import flights.datacollector.repository.AirportRepository;
import flights.datacollector.repository.UserAirportInterestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserInterestService {

    private final AirportRepository airportRepository;
    private final UserAirportInterestRepository interestRepository;
    private final UserValidationGrpcClient userValidationGrpcClient;

    public UserInterestService(AirportRepository airportRepository,
                               UserAirportInterestRepository interestRepository,
                               UserValidationGrpcClient userValidationGrpcClient) {
        this.airportRepository = airportRepository;
        this.interestRepository = interestRepository;
        this.userValidationGrpcClient = userValidationGrpcClient;
    }

    /**
     * Registra l'interesse di un utente per un aeroporto,
     * con politica "at-most-once".
     *
     * Passi:
     * 1) Verifica via gRPC se l'utente esiste nello User Manager.
     * 2) Verifica che l'aeroporto esista.
     * 3) Se esiste già un interesse (user_email, airport): restituisce quello esistente (nessuna modifica).
     * 4) Altrimenti crea un nuovo record.
     *
     * Soglie:
     * - La creazione dell'interesse è ammessa anche senza soglie
     *   (highValue == null e lowValue == null).
     * - Se una o entrambe le soglie sono presenti:
     *      - se entrambe presenti, deve valere highValue > lowValue;
     *      - i vincoli di non negatività sono gestiti dal DTO tramite @PositiveOrZero.
     */
    @Transactional
    public UserInterestResponse registerInterest(UserInterestRequest request) {
        String email = request.getUserEmail();
        String airportCode = request.getAirportCode();
        Integer highValue = request.getHighValue();
        Integer lowValue = request.getLowValue();

        // 1) Verifica utente via gRPC
        boolean userExists = userValidationGrpcClient.userExists(email);
        if (!userExists) {
            throw new UserNotFoundException(email);
        }

        // 2) Verifica aeroporto
        Airport airport = airportRepository.findByCode(airportCode)
                .orElseThrow(() -> new AirportNotFoundException(airportCode));

        // 3) Politica at-most-once sugli interessi
        UserAirportInterest existing = interestRepository
                .findByUserEmailAndAirport(email, airport)
                .orElse(null);

        if (existing != null) {
            // Manteniamo la semantica at-most-once:
            // la POST non modifica un interesse già esistente, ma lo restituisce così com'è.
            return toResponse(existing);
        }

        // 4) Validazione delle (eventuali) soglie in fase di creazione.
        //    È consentito anche il caso senza soglie (highValue == null && lowValue == null).
        validateThresholdsForCreate(highValue, lowValue);

        // 5) Crea un nuovo interesse con (eventuali) soglie
        UserAirportInterest interest = new UserAirportInterest(
                email,
                airport,
                LocalDateTime.now(),
                highValue,
                lowValue
        );

        UserAirportInterest saved = interestRepository.save(interest);
        return toResponse(saved);
    }

    /**
     * Aggiorna le soglie (highValue / lowValue) per un interesse esistente.
     *
     * La coppia (userEmail, airportCode) individua l'interesse.
     *
     * Soglie:
     * - È consentito:
     *      - impostare solo highValue,
     *      - impostare solo lowValue,
     *      - impostare entrambe con highValue > lowValue,
     *      - azzerare completamente le soglie (highValue == null e lowValue == null).
     * - Se entrambe presenti, deve valere highValue > lowValue.
     */
    @Transactional
    public UserInterestResponse updateInterestThresholds(UserInterestRequest request) {
        String email = request.getUserEmail();
        String airportCode = request.getAirportCode();
        Integer highValue = request.getHighValue();
        Integer lowValue = request.getLowValue();

        // Validazione delle soglie in fase di update (consente anche il caso "nessuna soglia")
        validateThresholdsForUpdate(highValue, lowValue);

        // 1. Controllo aeroporto: se non esiste → 404
        Airport airport = airportRepository.findByCode(airportCode)
                .orElseThrow(() -> new AirportNotFoundException(airportCode));

        // 2. Controllo interesse: se non esiste → 404
        UserAirportInterest existing = interestRepository
                .findByUserEmailAndAirport(email, airport)
                .orElseThrow(() -> new InterestNotFoundException(email, airportCode));

        // 3. Aggiornamento delle soglie (incluse eventuali rimozioni, se null)
        existing.setHighValue(highValue);
        existing.setLowValue(lowValue);

        UserAirportInterest saved = interestRepository.save(existing);
        return toResponse(saved);
    }

    @Transactional
    public void removeInterest(String userEmail, String airportCode) {
        // 1. Controllo aeroporto: se non esiste → 404
        Airport airport = airportRepository.findByCode(airportCode)
                .orElseThrow(() -> new AirportNotFoundException(airportCode));

        // 2. Controllo interesse: se non esiste → 404
        UserAirportInterest existing = interestRepository
                .findByUserEmailAndAirport(userEmail, airport)
                .orElseThrow(() -> new InterestNotFoundException(userEmail, airportCode));

        // 3. Se esiste, lo cancello
        interestRepository.delete(existing);
    }

    @Transactional(readOnly = true)
    public List<UserInterestResponse> listInterestsForUser(String userEmail) {
        List<UserAirportInterest> interests = interestRepository.findAllByUserEmail(userEmail);
        return interests.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Validazione delle soglie in fase di CREAZIONE dell'interesse.
     *
     * - Caso senza soglie (highValue == null && lowValue == null): CONSENTITO.
     * - Se entrambe presenti, highValue deve essere > lowValue.
     * - I vincoli di non negatività sono gestiti a livello DTO.
     */
    private void validateThresholdsForCreate(Integer highValue, Integer lowValue) {
        if (highValue != null && lowValue != null && highValue <= lowValue) {
            throw new IllegalArgumentException(
                    "When both thresholds are provided, highValue must be greater than lowValue."
            );
        }
    }

    /**
     * Validazione delle soglie in fase di UPDATE.
     *
     * - Caso senza soglie (highValue == null && lowValue == null): CONSENTITO
     *   e interpretato come "rimuovi le soglie" per questo interesse.
     * - Se entrambe presenti, highValue deve essere > lowValue.
     */
    private void validateThresholdsForUpdate(Integer highValue, Integer lowValue) {
        if (highValue != null && lowValue != null && highValue <= lowValue) {
            throw new IllegalArgumentException(
                    "When both thresholds are provided, highValue must be greater than lowValue."
            );
        }
    }

    private UserInterestResponse toResponse(UserAirportInterest interest) {
        return new UserInterestResponse(
            interest.getId(),
            interest.getUserEmail(),
            interest.getAirport().getCode(),
            interest.getCreatedAt(),
            interest.getHighValue(),
            interest.getLowValue()
        );
    }
}