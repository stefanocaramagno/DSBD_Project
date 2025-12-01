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

    protected UserAirportInterest() {
    }

    public UserAirportInterest(String userEmail, Airport airport, LocalDateTime createdAt) {
        this.userEmail = userEmail;
        this.airport = airport;
        this.createdAt = createdAt;
    }

    // Getter e setter

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
}
