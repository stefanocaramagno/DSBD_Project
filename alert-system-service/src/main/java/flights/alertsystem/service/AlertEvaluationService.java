package flights.alertsystem.service;

import flights.alertsystem.domain.UserAirportInterest;
import flights.alertsystem.messaging.dto.AirportFlightsWindowSnapshot;
import flights.alertsystem.messaging.dto.FlightCollectionWindowUpdateEvent;
import flights.alertsystem.messaging.dto.ThresholdBreachNotificationEvent;
import flights.alertsystem.repository.UserAirportInterestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servizio che, dato un aggiornamento di finestra proveniente dal Data Collector,
 * valuta il superamento delle soglie per ciascun profilo utente.
 */
@Service
public class AlertEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(AlertEvaluationService.class);

    private final UserAirportInterestRepository interestRepository;
    private final AlertNotificationProducer notificationProducer;

    public AlertEvaluationService(UserAirportInterestRepository interestRepository,
                                  AlertNotificationProducer notificationProducer) {
        this.interestRepository = interestRepository;
        this.notificationProducer = notificationProducer;
    }

    public void processWindowUpdate(FlightCollectionWindowUpdateEvent event) {
        if (event == null || event.getAirports() == null || event.getAirports().isEmpty()) {
            log.info("Evento di aggiornamento privo di aeroporti, nessuna valutazione soglie eseguita.");
            return;
        }

        Instant windowBegin = event.getWindowBegin();
        Instant windowEnd = event.getWindowEnd();

        Map<String, AirportFlightsWindowSnapshot> snapshotByCode =
                event.getAirports().stream()
                        .collect(Collectors.toMap(AirportFlightsWindowSnapshot::getAirportCode, s -> s));

        Set<String> airportCodes = snapshotByCode.keySet();

        List<UserAirportInterest> interests =
                interestRepository.findWithThresholdsByAirportCodes(airportCodes);

        if (interests.isEmpty()) {
            log.info("Nessun interesse con soglie configurate per gli aeroporti: {}", airportCodes);
            return;
        }

        for (UserAirportInterest interest : interests) {
            String airportCode = interest.getAirport().getCode();
            AirportFlightsWindowSnapshot snapshot = snapshotByCode.get(airportCode);

            if (snapshot == null) {
                continue;
            }

            int totalFlights = snapshot.getArrivalsCount() + snapshot.getDeparturesCount();
            Integer high = interest.getHighValue();
            Integer low = interest.getLowValue();

            if (high != null && totalFlights > high) {
                ThresholdBreachNotificationEvent notification = new ThresholdBreachNotificationEvent(
                        interest.getUserEmail(),
                        airportCode,
                        "HIGH",
                        totalFlights,
                        high,
                        windowBegin,
                        windowEnd
                );
                notificationProducer.publishNotification(notification);
            }

            if (low != null && totalFlights < low) {
                ThresholdBreachNotificationEvent notification = new ThresholdBreachNotificationEvent(
                        interest.getUserEmail(),
                        airportCode,
                        "LOW",
                        totalFlights,
                        low,
                        windowBegin,
                        windowEnd
                );
                notificationProducer.publishNotification(notification);
            }
        }
    }
}