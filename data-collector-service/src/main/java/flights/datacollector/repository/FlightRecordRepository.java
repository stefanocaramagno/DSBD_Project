package flights.datacollector.repository;

import flights.datacollector.domain.Airport;
import flights.datacollector.domain.FlightRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface FlightRecordRepository extends JpaRepository<FlightRecord, Long> {

    List<FlightRecord> findByAirportAndCollectedAtBetween(
            Airport airport,
            LocalDateTime from,
            LocalDateTime to
    );

    /**
     * Ultimo volo (per actualTime) per aeroporto e direzione.
     */
    Optional<FlightRecord> findFirstByAirportAndDirectionOrderByActualTimeDesc(
            Airport airport,
            String direction
    );

    /**
     * Conteggio dei voli in un intervallo temporale, per aeroporto e direzione.
     * Verrà usato per la media giornaliera.
     */
    long countByAirportAndDirectionAndActualTimeBetween(
            Airport airport,
            String direction,
            LocalDateTime from,
            LocalDateTime to
    );

    List<FlightRecord> findByAirportAndDirectionAndActualTimeBetweenOrderByActualTimeDesc(
            Airport airport,
            String direction,
            LocalDateTime from,
            LocalDateTime to
    );
}