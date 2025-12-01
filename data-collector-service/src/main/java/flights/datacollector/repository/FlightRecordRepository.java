package flights.datacollector.repository;

import flights.datacollector.domain.Airport;
import flights.datacollector.domain.FlightRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRecordRepository extends JpaRepository<FlightRecord, Long> {

    List<FlightRecord> findByAirportAndCollectedAtBetween(
            Airport airport,
            LocalDateTime from,
            LocalDateTime to
    );
}