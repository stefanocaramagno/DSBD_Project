package flights.alertsystem.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "user_airport_interest")
public class UserAirportInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * E-mail dell'utente, utilizzata come identificatore logico.
     */
    @Column(name = "user_email", nullable = false, length = 255)
    private String userEmail;

    /**
     * Aeroporto associato all'interesse.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airport_id", nullable = false)
    private Airport airport;

    /**
     * Soglia superiore sul numero di voli (arrivi + partenze) per l'aeroporto.
     */
    @Column(name = "high_value")
    private Integer highValue;

    /**
     * Soglia inferiore sul numero di voli (arrivi + partenze) per l'aeroporto.
     */
    @Column(name = "low_value")
    private Integer lowValue;

    public UserAirportInterest() {
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