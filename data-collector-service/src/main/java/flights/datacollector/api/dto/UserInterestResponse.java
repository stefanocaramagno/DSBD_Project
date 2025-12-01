package flights.datacollector.api.dto;

import java.time.LocalDateTime;

public class UserInterestResponse {

    private Long id;
    private String userEmail;
    private String airportCode;
    private LocalDateTime createdAt;

    public UserInterestResponse() {
    }

    public UserInterestResponse(Long id, String userEmail, String airportCode, LocalDateTime createdAt) {
        this.id = id;
        this.userEmail = userEmail;
        this.airportCode = airportCode;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}