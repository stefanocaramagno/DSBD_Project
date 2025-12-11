package flights.datacollector.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO per il mapping della risposta JSON di OpenSky per i voli.
 * Si assume lo schema dell'endpoint /flights/arrival e /flights/departure.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenSkyFlightDto {

    @JsonProperty("icao24")
    private String icao24;

    @JsonProperty("callsign")
    private String callsign;

    @JsonProperty("estDepartureAirport")
    private String estDepartureAirport;

    @JsonProperty("estArrivalAirport")
    private String estArrivalAirport;

    @JsonProperty("firstSeen")
    private long firstSeen;

    @JsonProperty("lastSeen")
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