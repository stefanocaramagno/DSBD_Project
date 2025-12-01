package flights.datacollector.api;

import flights.datacollector.api.dto.AverageFlightsResponse;
import flights.datacollector.api.dto.FlightRecordResponse;
import flights.datacollector.api.dto.LastFlightResponse;
import flights.datacollector.service.FlightQueryService;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
public class FlightQueryController {

    private final FlightQueryService flightQueryService;

    public FlightQueryController(FlightQueryService flightQueryService) {
        this.flightQueryService = flightQueryService;
    }

    /**
     * Recupero dell'ultimo volo (per actualTime) in arrivo o partenza
     * da un dato aeroporto.
     *
     * Esempio:
     *  GET /api/flights/last?airportCode=CTA&direction=ARRIVAL
     */
    @GetMapping("/last")
    public ResponseEntity<LastFlightResponse> getLastFlight(
            @RequestParam("airportCode") String airportCode,
            @RequestParam("direction") String direction
    ) {
        LastFlightResponse response =
                flightQueryService.getLastFlight(airportCode, direction);
        return ResponseEntity.ok(response);
    }

    /**
     * Calcolo della media del numero di voli negli ultimi X giorni
     * per un aeroporto e una direzione.
     *
     * Esempio:
     *  GET /api/flights/average?airportCode=CTA&direction=DEPARTURE&days=7
     */
    @GetMapping("/average")
    public ResponseEntity<AverageFlightsResponse> getAverageFlights(
            @RequestParam("airportCode") String airportCode,
            @RequestParam("direction") String direction,
            @RequestParam("days") int days
    ) {
        AverageFlightsResponse response =
                flightQueryService.getAverageFlights(airportCode, direction, days);
        return ResponseEntity.ok(response);
    }

    /**
     * Lettura dei voli registrati per un aeroporto, direzione e intervallo temporale.
     *
     * Esempio:
     * GET /api/flights?airportCode=LICC&direction=ARRIVAL&from=2025-11-19T00:00:00&to=2025-11-20T00:00:00
     *
     * Se from/to non sono specificati, viene usato l'ultimo giorno [now-1d, now].
     */
    @GetMapping
    public ResponseEntity<List<FlightRecordResponse>> getFlights(
            @RequestParam("airportCode") String airportCode,
            @RequestParam("direction") String direction,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        List<FlightRecordResponse> flights =
                flightQueryService.getFlights(airportCode, direction, from, to);
        return ResponseEntity.ok(flights);
    }
}
