package flights.datacollector.api.dto;

import java.time.LocalDateTime;

public class FlightRecordResponse {

    private final String airportCode;
    private final String direction;
    private final String flightNumber;
    private final String externalFlightId;
    private final LocalDateTime scheduledTime;
    private final LocalDateTime actualTime;
    private final String status;
    private final Integer delayMinutes;
    private final LocalDateTime collectedAt;

    public FlightRecordResponse(String airportCode,
                                String direction,
                                String flightNumber,
                                String externalFlightId,
                                LocalDateTime scheduledTime,
                                LocalDateTime actualTime,
                                String status,
                                Integer delayMinutes,
                                LocalDateTime collectedAt) {
        this.airportCode = airportCode;
        this.direction = direction;
        this.flightNumber = flightNumber;
        this.externalFlightId = externalFlightId;
        this.scheduledTime = scheduledTime;
        this.actualTime = actualTime;
        this.status = status;
        this.delayMinutes = delayMinutes;
        this.collectedAt = collectedAt;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public String getDirection() {
        return direction;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getExternalFlightId() {
        return externalFlightId;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public LocalDateTime getActualTime() {
        return actualTime;
    }

    public String getStatus() {
        return status;
    }

    public Integer getDelayMinutes() {
        return delayMinutes;
    }

    public LocalDateTime getCollectedAt() {
        return collectedAt;
    }
}