package flights.datacollector.exception;

public class InterestNotFoundException extends RuntimeException {

    public InterestNotFoundException(String userEmail, String airportCode) {
        super("Interest for user '" + userEmail + "' and airport '" + airportCode + "' not found");
    }
}