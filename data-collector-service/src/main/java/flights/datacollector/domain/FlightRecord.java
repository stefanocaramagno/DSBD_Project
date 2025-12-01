package flights.datacollector.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "flight_records")
public class FlightRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "airport_id", nullable = false)
    private Airport airport;

    @Column(name = "external_flight_id", length = 64)
    private String externalFlightId;

    @Column(name = "flight_number", length = 32)
    private String flightNumber;

    @Column(name = "direction", length = 16)
    private String direction;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "actual_time")
    private LocalDateTime actualTime;

    @Column(name = "status", length = 32)
    private String status;

    @Column(name = "delay_minutes")
    private Integer delayMinutes;

    @Column(name = "collected_at", nullable = false)
    private LocalDateTime collectedAt;

    protected FlightRecord() {
    }

    public FlightRecord(
            Airport airport,
            String externalFlightId,
            String flightNumber,
            String direction,
            LocalDateTime scheduledTime,
            LocalDateTime actualTime,
            String status,
            Integer delayMinutes,
            LocalDateTime collectedAt
    ) {
        this.airport = airport;
        this.externalFlightId = externalFlightId;
        this.flightNumber = flightNumber;
        this.direction = direction;
        this.scheduledTime = scheduledTime;
        this.actualTime = actualTime;
        this.status = status;
        this.delayMinutes = delayMinutes;
        this.collectedAt = collectedAt;
    }

    // Getter e setter

    public Long getId() {
        return id;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    public String getExternalFlightId() {
        return externalFlightId;
    }

    public void setExternalFlightId(String externalFlightId) {
        this.externalFlightId = externalFlightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
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