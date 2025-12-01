package flights.datacollector.service;

import flights.datacollector.api.dto.AverageFlightsResponse;
import flights.datacollector.api.dto.FlightRecordResponse;
import flights.datacollector.api.dto.LastFlightResponse;
import flights.datacollector.domain.Airport;
import flights.datacollector.domain.FlightRecord;
import flights.datacollector.exception.AirportNotFoundException;
import flights.datacollector.exception.FlightNotFoundException;
import flights.datacollector.repository.AirportRepository;
import flights.datacollector.repository.FlightRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightQueryService {

    private final AirportRepository airportRepository;
    private final FlightRecordRepository flightRecordRepository;

    public FlightQueryService(AirportRepository airportRepository,
                              FlightRecordRepository flightRecordRepository) {
        this.airportRepository = airportRepository;
        this.flightRecordRepository = flightRecordRepository;
    }

    @Transactional(readOnly = true)
    public LastFlightResponse getLastFlight(String airportCode, String direction) {
        String normalizedDirection = normalizeDirection(direction);

        Airport airport = airportRepository.findByCode(airportCode)
                .orElseThrow(() -> new AirportNotFoundException(airportCode));

        FlightRecord record = flightRecordRepository
                .findFirstByAirportAndDirectionOrderByActualTimeDesc(airport, normalizedDirection)
                .orElseThrow(() -> new FlightNotFoundException(airportCode, normalizedDirection));

        return new LastFlightResponse(
                airport.getCode(),
                record.getDirection(),
                record.getFlightNumber(),
                record.getExternalFlightId(),      // usa il getter corretto in base al nome del campo nella entity
                record.getActualTime(),
                record.getStatus(),
                record.getDelayMinutes(),
                record.getCollectedAt()
        );
    }

    @Transactional(readOnly = true)
    public AverageFlightsResponse getAverageFlights(String airportCode,
                                                    String direction,
                                                    int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Parameter 'days' must be greater than 0");
        }

        String normalizedDirection = normalizeDirection(direction);

        Airport airport = airportRepository.findByCode(airportCode)
                .orElseThrow(() -> new AirportNotFoundException(airportCode));

        LocalDateTime to = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime from = to.minusDays(days);

        long totalFlights = flightRecordRepository
                .countByAirportAndDirectionAndActualTimeBetween(
                        airport,
                        normalizedDirection,
                        from,
                        to
                );

        double averagePerDay = totalFlights / (double) days;

        return new AverageFlightsResponse(
                airport.getCode(),
                normalizedDirection,
                days,
                totalFlights,
                averagePerDay,
                from,
                to
        );
    }

        @Transactional(readOnly = true)
        public List<FlightRecordResponse> getFlights(String airportCode,
                                                    String direction,
                                                    LocalDateTime from,
                                                    LocalDateTime to) {
            String normalizedDirection = normalizeDirection(direction);

            // default: ultimi 1 giorno se from/to non specificati
            LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
            if (to == null) {
                to = nowUtc;
            }
            if (from == null) {
                from = to.minusDays(1);
            }
            if (from.isAfter(to)) {
                throw new IllegalArgumentException("'from' must be before or equal to 'to'");
            }

            Airport airport = airportRepository.findByCode(airportCode)
                    .orElseThrow(() -> new AirportNotFoundException(airportCode));

            List<FlightRecord> records = flightRecordRepository
                    .findByAirportAndDirectionAndActualTimeBetweenOrderByActualTimeDesc(
                            airport,
                            normalizedDirection,
                            from,
                            to
                    );

            return records.stream()
                    .map(record -> new FlightRecordResponse(
                        airport.getCode(),
                        record.getDirection(),
                        record.getFlightNumber(),
                        record.getExternalFlightId(),
                        record.getScheduledTime(),
                        record.getActualTime(),
                        record.getStatus(),
                        record.getDelayMinutes(),
                        record.getCollectedAt()
                    ))
                    .collect(Collectors.toList());
    }

    private String normalizeDirection(String direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Direction parameter is required");
        }
        String value = direction.trim().toUpperCase();
        if (!"ARRIVAL".equals(value) && !"DEPARTURE".equals(value)) {
            throw new IllegalArgumentException("Direction must be ARRIVAL or DEPARTURE");
        }
        return value;
    }
}