package flights.datacollector.messaging.dto;

/**
 * Snapshot aggregato dei voli per un singolo aeroporto
 * in una specifica finestra temporale di raccolta.
 */
public class AirportFlightsWindowSnapshot {

    private String airportCode;
    private int arrivalsCount;
    private int departuresCount;

    public AirportFlightsWindowSnapshot() {
        // Costruttore vuoto richiesto da Jackson
    }

    public AirportFlightsWindowSnapshot(String airportCode, int arrivalsCount, int departuresCount) {
        this.airportCode = airportCode;
        this.arrivalsCount = arrivalsCount;
        this.departuresCount = departuresCount;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public int getArrivalsCount() {
        return arrivalsCount;
    }

    public void setArrivalsCount(int arrivalsCount) {
        this.arrivalsCount = arrivalsCount;
    }

    public int getDeparturesCount() {
        return departuresCount;
    }

    public void setDeparturesCount(int departuresCount) {
        this.departuresCount = departuresCount;
    }
}