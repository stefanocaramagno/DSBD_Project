package flights.datacollector.api;

import flights.datacollector.api.dto.AverageFlightsResponse;
import flights.datacollector.api.dto.LastFlightResponse;
import flights.datacollector.service.FlightQueryService;
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
}
