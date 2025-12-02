package flights.datacollector.service;

import flights.datacollector.client.OpenSkyClient;
import flights.datacollector.client.dto.OpenSkyFlightDto;
import flights.datacollector.domain.Airport;
import flights.datacollector.domain.FlightRecord;
import flights.datacollector.repository.FlightRecordRepository;
import flights.datacollector.repository.UserAirportInterestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightCollectionService {

    private final UserAirportInterestRepository interestRepository;
    private final FlightRecordRepository flightRecordRepository;
    private final OpenSkyClient openSkyClient;

    public FlightCollectionService(UserAirportInterestRepository interestRepository,
                                   FlightRecordRepository flightRecordRepository,
                                   OpenSkyClient openSkyClient) {
        this.interestRepository = interestRepository;
        this.flightRecordRepository = flightRecordRepository;
        this.openSkyClient = openSkyClient;
    }

    @Transactional
    public void collectFlightsForAllInterestedAirports(Instant begin, Instant end) {
        List<Airport> airports = interestRepository.findDistinctAirportsOfInterest();

        for (Airport airport : airports) {
            collectFlightsForAirport(airport, begin, end);
        }
    }

    /**
     * Colleziona i voli (arrivi e partenze) per un singolo aeroporto di interesse
     * nell'intervallo [begin, end].
     */
    @Transactional
    public void collectFlightsForAirport(Airport airport, Instant begin, Instant end) {
        String airportCode = airport.getCode();

        // Chiamata a OpenSky per arrivi
        List<OpenSkyFlightDto> arrivals = openSkyClient.getArrivals(airportCode, begin, end);

        // Chiamata a OpenSky per partenze
        List<OpenSkyFlightDto> departures = openSkyClient.getDepartures(airportCode, begin, end);

        List<FlightRecord> toSave = new ArrayList<>();

        for (OpenSkyFlightDto dto : arrivals) {
            FlightRecord record = mapToFlightRecord(dto, airport, "ARRIVAL");
            toSave.add(record);
        }

        for (OpenSkyFlightDto dto : departures) {
            FlightRecord record = mapToFlightRecord(dto, airport, "DEPARTURE");
            toSave.add(record);
        }

        if (!toSave.isEmpty()) {
            flightRecordRepository.saveAll(toSave);
        }
    }

    /**
     * Mapping da DTO OpenSky a entità di dominio FlightRecord.
     * I campi che OpenSky non fornisce (status, delayMinutes, scheduledTime)
     * vengono lasciati null; verranno usati solo actualTime e collectedAt
     * nelle analisi successive (ultimo volo, medie, ecc.).
     */
    private FlightRecord mapToFlightRecord(OpenSkyFlightDto dto, Airport airport, String direction) {
        // Identificativo esterno: icao24 + firstSeen + lastSeen
        String externalId = dto.getIcao24() + "-" + dto.getFirstSeen() + "-" + dto.getLastSeen();

        // Utilizziamo il callsign come "flightNumber" (se presente)
        String flightNumber = dto.getCallsign();

        // Per semplicità lasciamo scheduledTime a null
        LocalDateTime scheduledTime = null;

        // Determiniamo actualTime in base alla direzione:
        // - ARRIVAL: lastSeen ~ momento di arrivo
        // - DEPARTURE: firstSeen ~ momento di partenza
        LocalDateTime actualTime;
        if ("ARRIVAL".equals(direction)) {
            actualTime = epochSecondsToLocalDateTime(dto.getLastSeen());
        } else {
            actualTime = epochSecondsToLocalDateTime(dto.getFirstSeen());
        }

        // Per ora non gestiamo status e ritardi: li lasciamo null
        String status = null;
        Integer delayMinutes = null;

        // Timestamp di raccolta (quando il nostro sistema ha inserito il record)
        LocalDateTime collectedAt = LocalDateTime.now(ZoneOffset.UTC);

        return new FlightRecord(
                airport,
                externalId,
                flightNumber,
                direction,
                scheduledTime,
                actualTime,
                status,
                delayMinutes,
                collectedAt
        );
    }

    private LocalDateTime epochSecondsToLocalDateTime(long epochSeconds) {
        return LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC);
    }
}