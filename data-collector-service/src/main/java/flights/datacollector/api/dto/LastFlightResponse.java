package flights.datacollector.api.dto;

import java.time.LocalDateTime;

public class LastFlightResponse {

    private String airportCode;
    private String direction;
    private String flightNumber;
    private String externalId;
    private LocalDateTime actualTime;
    private String status;
    private Integer delayMinutes;
    private LocalDateTime collectedAt;

    public LastFlightResponse() {
    }

    public LastFlightResponse(String airportCode,
                              String direction,
                              String flightNumber,
                              String externalId,
                              LocalDateTime actualTime,
                              String status,
                              Integer delayMinutes,
                              LocalDateTime collectedAt) {
        this.airportCode = airportCode;
        this.direction = direction;
        this.flightNumber = flightNumber;
        this.externalId = externalId;
        this.actualTime = actualTime;
        this.status = status;
        this.delayMinutes = delayMinutes;
        this.collectedAt = collectedAt;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public LocalDateTime getActualTime() {
        return actualTime;
    }

    public void setActualTime(LocalDateTime actualTime) {
        this.actualTime = actualTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDelayMinutes() {
        return delayMinutes;
    }

    public void setDelayMinutes(Integer delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public LocalDateTime getCollectedAt() {
        return collectedAt;
    }

    public void setCollectedAt(LocalDateTime collectedAt) {
        this.collectedAt = collectedAt;
    }
}