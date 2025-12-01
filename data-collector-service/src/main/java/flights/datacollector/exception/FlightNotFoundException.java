package flights.datacollector.exception;

public class FlightNotFoundException extends RuntimeException {

    public FlightNotFoundException(String airportCode, String direction) {
        super("No " + direction.toLowerCase()
              + " flights found for airport '" + airportCode + "'");
    }
}