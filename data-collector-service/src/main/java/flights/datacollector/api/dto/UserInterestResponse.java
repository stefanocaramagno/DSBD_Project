package flights.datacollector.api.dto;

import java.time.LocalDateTime;

public class UserInterestResponse {

    private Long id;
    private String userEmail;
    private String airportCode;
    private LocalDateTime createdAt;
    private Integer highValue;
    private Integer lowValue;

    public UserInterestResponse() {
    }

    public UserInterestResponse(Long id,
                                String userEmail,
                                String airportCode,
                                LocalDateTime createdAt,
                                Integer highValue,
                                Integer lowValue) {
        this.id = id;
        this.userEmail = userEmail;
        this.airportCode = airportCode;
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

    public String getAirportCode() {
        return airportCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Integer getHighValue() {
        return highValue;
    }

    public Integer getLowValue() {
        return lowValue;
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

    public void setHighValue(Integer highValue) {
        this.highValue = highValue;
    }

    public void setLowValue(Integer lowValue) {
        this.lowValue = lowValue;
    }
}