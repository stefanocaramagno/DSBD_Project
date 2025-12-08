package flights.datacollector.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class UserInterestRequest {

    @Email
    @NotBlank
    private String userEmail;

    @NotBlank
    private String airportCode;

    @PositiveOrZero(message = "highValue must be >= 0")
    private Integer highValue;

    @PositiveOrZero(message = "lowValue must be >= 0")
    private Integer lowValue;

    public UserInterestRequest() {
    }

    public UserInterestRequest(String userEmail,
                               String airportCode,
                               Integer highValue,
                               Integer lowValue) {
        this.userEmail = userEmail;
        this.airportCode = airportCode;
        this.highValue = highValue;
        this.lowValue = lowValue;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
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