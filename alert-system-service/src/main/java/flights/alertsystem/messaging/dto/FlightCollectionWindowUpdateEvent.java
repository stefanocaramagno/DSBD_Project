package flights.alertsystem.messaging.dto;

import java.time.Instant;
import java.util.List;

/**
 * Evento ricevuto dal broker che descrive l'aggiornamento effettuato
 * dal Data Collector in una certa finestra temporale.
 */
public class FlightCollectionWindowUpdateEvent {

    private Instant windowBegin;
    private Instant windowEnd;
    private List<AirportFlightsWindowSnapshot> airports;

    public FlightCollectionWindowUpdateEvent() {
    }

    public FlightCollectionWindowUpdateEvent(Instant windowBegin,
                                             Instant windowEnd,
                                             List<AirportFlightsWindowSnapshot> airports) {
        this.windowBegin = windowBegin;
        this.windowEnd = windowEnd;
        this.airports = airports;
    }

    public Instant getWindowBegin() {
        return windowBegin;
    }

    public void setWindowBegin(Instant windowBegin) {
        this.windowBegin = windowBegin;
    }

    public Instant getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(Instant windowEnd) {
        this.windowEnd = windowEnd;
    }

    public List<AirportFlightsWindowSnapshot> getAirports() {
        return airports;
    }

    public void setAirports(List<AirportFlightsWindowSnapshot> airports) {
        this.airports = airports;
    }
}