package flights.datacollector.scheduler;

import flights.datacollector.service.FlightCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class FlightCollectionScheduler {

    private static final Logger log = LoggerFactory.getLogger(FlightCollectionScheduler.class);

    private final FlightCollectionService flightCollectionService;

    public FlightCollectionScheduler(FlightCollectionService flightCollectionService) {
        this.flightCollectionService = flightCollectionService;
    }

    /**
     * Job di raccolta voli.
     *
     * Configurazione di sviluppo:
     *  - ogni minuto
     *  - intervallo temporale: ultimi 24 ore [now-24h, now]
     *
     * In produzione (o nella relazione) potrai documentare una versione
     * più “realistica” (es. una volta al giorno, ogni 12 ore, ecc.).
     */
    @Scheduled(fixedRateString = "60000") // ogni minuto per test
    public void collectLastHourForAllAirports() {
        Instant end = Instant.now();
        Instant begin = end.minus(24, ChronoUnit.HOURS); // ultime 24 ore

        log.info("Starting flight collection for all interested airports in interval [{} - {}]", begin, end);

        try {
            flightCollectionService.collectFlightsForAllInterestedAirports(begin, end);
            log.info("Completed flight collection for all interested airports");
        } catch (Exception ex) {
            log.error("Error during flight collection: {}", ex.getMessage(), ex);
        }
    }
}