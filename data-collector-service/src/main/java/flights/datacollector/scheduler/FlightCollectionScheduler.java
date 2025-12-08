package flights.datacollector.scheduler;

import flights.datacollector.messaging.dto.AirportFlightsWindowSnapshot;
import flights.datacollector.service.AlertUpdateProducer;
import flights.datacollector.service.FlightCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class FlightCollectionScheduler {

    private static final Logger log = LoggerFactory.getLogger(FlightCollectionScheduler.class);

    private final FlightCollectionService flightCollectionService;
    private final AlertUpdateProducer alertUpdateProducer;

    public FlightCollectionScheduler(FlightCollectionService flightCollectionService,
                                     AlertUpdateProducer alertUpdateProducer) {
        this.flightCollectionService = flightCollectionService;
        this.alertUpdateProducer = alertUpdateProducer;
    }

    @Scheduled(fixedRateString = "60000") // ogni minuto per test
    public void collectLastHourForAllAirports() {
        Instant end = Instant.now();
        // In questo momento si considerano le ultime 24 ore; il commento può
        // essere aggiornato se si modifica la logica della finestra.
        Instant begin = end.minus(24, ChronoUnit.HOURS);

        log.info("Starting flight collection for all interested airports in interval [{} - {}]", begin, end);

        try {
            List<AirportFlightsWindowSnapshot> snapshots =
                    flightCollectionService.collectFlightsForAllInterestedAirports(begin, end);

            int airportsCount = (snapshots != null) ? snapshots.size() : 0;
            log.info("Completed flight collection for all interested airports. airportsWithNewData={}",
                    airportsCount);

            // Pubblica su Kafka l'evento di aggiornamento, se presente almeno uno snapshot
            alertUpdateProducer.publishCollectionWindowUpdate(begin, end, snapshots);
        } catch (Exception ex) {
            log.error("Error during flight collection: {}", ex.getMessage(), ex);
        }
    }
}