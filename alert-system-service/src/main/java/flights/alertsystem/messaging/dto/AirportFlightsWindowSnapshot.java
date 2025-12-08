package flights.alertsystem.messaging.dto;

/**
 * Snapshot aggregato dei voli per un singolo aeroporto
 * in una specifica finestra temporale.
 */
public class AirportFlightsWindowSnapshot {

    private String airportCode;
    private int arrivalsCount;
    private int departuresCount;

    public AirportFlightsWindowSnapshot() {
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
