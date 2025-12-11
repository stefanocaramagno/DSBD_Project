package flights.datacollector.service;

import flights.datacollector.client.OpenSkyClient;
import flights.datacollector.client.dto.OpenSkyFlightDto;
import flights.datacollector.domain.Airport;
import flights.datacollector.domain.FlightRecord;
import flights.datacollector.messaging.dto.AirportFlightsWindowSnapshot;
import flights.datacollector.repository.FlightRecordRepository;
import flights.datacollector.repository.UserAirportInterestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Servizio che coordina la raccolta dei voli da OpenSky per tutti
 * gli aeroporti di interesse e persiste i record nel Data Collector DB.
 *
 * Inoltre, restituisce per ogni aeroporto uno snapshot aggregato dei voli
 * nella finestra temporale considerata, da usare per notificare l'Alert System.
 */
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

    /**
     * Punto di ingresso principale: raccoglie i voli per tutti gli aeroporti
     * configurati come "di interesse", li salva nel database e costruisce
     * per ciascun aeroporto uno snapshot aggregato (arrivi/partenze).
     *
     * Il Circuit Breaker è applicato a livello di OpenSkyClient:
     * se OpenSky non è disponibile, per quella finestra otterremo liste vuote
     * e il sistema continuerà a funzionare senza bloccarsi.
     */
    @Transactional
    public List<AirportFlightsWindowSnapshot> collectFlightsForAllInterestedAirports(Instant begin, Instant end) {
        List<Airport> airportsOfInterest = interestRepository.findDistinctAirportsOfInterest();
        LocalDateTime collectedAt = LocalDateTime.now(ZoneOffset.UTC);

        List<FlightRecord> recordsToPersist = new ArrayList<>();
        List<AirportFlightsWindowSnapshot> snapshots = new ArrayList<>();

        for (Airport airport : airportsOfInterest) {
            String airportIcao = airport.getCode();

            int arrivalsCount = 0;
            int departuresCount = 0;

            // Arrivi
            List<OpenSkyFlightDto> arrivals =
                    openSkyClient.getArrivals(airportIcao, begin, end);
            for (OpenSkyFlightDto dto : arrivals) {
                FlightRecord record = mapOpenSkyFlightToFlightRecord(
                        dto, airport, "ARRIVAL", collectedAt
                );
                recordsToPersist.add(record);
                arrivalsCount++;
            }

            // Partenze
            List<OpenSkyFlightDto> departures =
                    openSkyClient.getDepartures(airportIcao, begin, end);
            for (OpenSkyFlightDto dto : departures) {
                FlightRecord record = mapOpenSkyFlightToFlightRecord(
                        dto, airport, "DEPARTURE", collectedAt
                );
                recordsToPersist.add(record);
                departuresCount++;
            }

            // Se per questo aeroporto abbiamo almeno un volo raccolto,
            // creiamo lo snapshot aggregato.
            if (arrivalsCount > 0 || departuresCount > 0) {
                AirportFlightsWindowSnapshot snapshot =
                        new AirportFlightsWindowSnapshot(airportIcao, arrivalsCount, departuresCount);
                snapshots.add(snapshot);
            }
        }

        if (!recordsToPersist.isEmpty()) {
            flightRecordRepository.saveAll(recordsToPersist);
        }

        return snapshots;
    }

    /**
     * Mappa un singolo DTO di OpenSky nel dominio interno FlightRecord.
     * Per semplicità:
     * - usiamo lastSeen come "tempo effettivo" dell'evento;
     * - non disponiamo di un vero orario schedulato, quindi lo
     *   impostiamo uguale all'effettivo;
     * - status fisso "ON_TIME" e delayMinutes = 0.
     * Queste scelte sono documentate e possono essere affinate in seguito.
     */
    private FlightRecord mapOpenSkyFlightToFlightRecord(OpenSkyFlightDto dto,
                                                        Airport airport,
                                                        String direction,
                                                        LocalDateTime collectedAt) {

        String externalId = String.format("%s_%d_%d",
                dto.getIcao24(),
                dto.getFirstSeen(),
                dto.getLastSeen());

        String flightNumber = dto.getCallsign() != null
                ? dto.getCallsign().trim()
                : "UNKNOWN";

        LocalDateTime actualTime = epochSecondsToLocalDateTime(dto.getLastSeen());
        LocalDateTime scheduledTime = actualTime;  // non abbiamo informazione di schedule
        String status = "ON_TIME";
        Integer delayMinutes = 0;

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