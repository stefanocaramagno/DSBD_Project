package flights.datacollector.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserInterestRequest {

    @Email
    @NotBlank
    private String userEmail;

    @NotBlank
    private String airportCode;

    public UserInterestRequest() {
    }

    public UserInterestRequest(String userEmail, String airportCode) {
        this.userEmail = userEmail;
        this.airportCode = airportCode;
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
}