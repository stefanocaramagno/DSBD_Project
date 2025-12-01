package flights.datacollector.service;

import flights.datacollector.api.dto.UserInterestRequest;
import flights.datacollector.api.dto.UserInterestResponse;
import flights.datacollector.client.UserValidationGrpcClient;
import flights.datacollector.domain.Airport;
import flights.datacollector.domain.UserAirportInterest;
import flights.datacollector.exception.AirportNotFoundException;
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
     */
    @Transactional
    public UserInterestResponse registerInterest(UserInterestRequest request) {
        String email = request.getUserEmail();
        String airportCode = request.getAirportCode();

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
            return toResponse(existing);
        }

        // 4) Crea un nuovo interesse
        UserAirportInterest interest = new UserAirportInterest(
                email,
                airport,
                LocalDateTime.now()
        );

        UserAirportInterest saved = interestRepository.save(interest);
        return toResponse(saved);
    }

    @Transactional
    public void removeInterest(String userEmail, String airportCode) {
        airportRepository.findByCode(airportCode).ifPresent(airport -> {
            interestRepository.findByUserEmailAndAirport(userEmail, airport)
                    .ifPresent(interestRepository::delete);
        });
        // Nessuna eccezione lanciata: qualunque sia lo stato del DB,
        // la DELETE è idempotente e il controller risponderà sempre 204.
    }

    @Transactional(readOnly = true)
    public List<UserInterestResponse> listInterestsForUser(String userEmail) {
        List<UserAirportInterest> interests = interestRepository.findAllByUserEmail(userEmail);
        return interests.stream()
                .map(this::toResponse)
                .toList();
    }

    private UserInterestResponse toResponse(UserAirportInterest interest) {
        return new UserInterestResponse(
                interest.getId(),
                interest.getUserEmail(),
                interest.getAirport().getCode(),
                interest.getCreatedAt()
        );
    }
}