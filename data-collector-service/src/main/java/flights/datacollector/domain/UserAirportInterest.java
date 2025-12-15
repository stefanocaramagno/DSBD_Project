package flights.datacollector.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "user_airport_interest",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_airport_interest", columnNames = {"user_email", "airport_id"})
    }
)
public class UserAirportInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false, length = 255)
    private String userEmail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "airport_id", nullable = false)
    private Airport airport;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "high_value")
    private Integer highValue;

    @Column(name = "low_value")
    private Integer lowValue;

    protected UserAirportInterest() {
    }

    public UserAirportInterest(String userEmail, Airport airport, LocalDateTime createdAt) {
        this(userEmail, airport, createdAt, null, null);
    }

    public UserAirportInterest(String userEmail,
                               Airport airport,
                               LocalDateTime createdAt,
                               Integer highValue,
                               Integer lowValue) {
        this.userEmail = userEmail;
        this.airport = airport;
        this.createdAt = createdAt;
        this.highValue = highValue;
        this.lowValue = lowValue;
    }

    public Long getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getHighValue() {
        return highValue;
    }

    public void setHighValue(Integer highValue) {
        this.highValue = highValue;
    }

    public Integer getLowValue() {
        return lowValue;
    }

    public void setLowValue(Integer lowValue) {
        this.lowValue = lowValue;
    }
}