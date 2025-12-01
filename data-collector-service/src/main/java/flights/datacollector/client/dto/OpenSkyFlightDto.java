package flights.datacollector.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenSkyFlightDto {

    // Identificativo del transponder
    private String icao24;

    // Call-sign del volo (es. "DLH1234")
    private String callsign;

    // Aeroporto stimato di partenza (ICAO, es. "EDDF")
    private String estDepartureAirport;

    // Aeroporto stimato di arrivo (ICAO)
    private String estArrivalAirport;

    // Istante di partenza (epoch seconds)
    private long firstSeen;

    // Istante di arrivo (epoch seconds)
    private long lastSeen;

    public OpenSkyFlightDto() {
    }

    public String getIcao24() {
        return icao24;
    }

    public void setIcao24(String icao24) {
        this.icao24 = icao24;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public String getEstDepartureAirport() {
        return estDepartureAirport;
    }

    public void setEstDepartureAirport(String estDepartureAirport) {
        this.estDepartureAirport = estDepartureAirport;
    }

    public String getEstArrivalAirport() {
        return estArrivalAirport;
    }

    public void setEstArrivalAirport(String estArrivalAirport) {
        this.estArrivalAirport = estArrivalAirport;
    }

    public long getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(long firstSeen) {
        this.firstSeen = firstSeen;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }
}