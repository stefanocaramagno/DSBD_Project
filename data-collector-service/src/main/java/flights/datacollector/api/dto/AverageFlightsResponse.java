package flights.datacollector.api.dto;

import java.time.LocalDateTime;

public class AverageFlightsResponse {

    private String airportCode;
    private String direction;
    private int days;
    private long totalFlights;
    private double averageFlightsPerDay;
    private LocalDateTime from;
    private LocalDateTime to;

    public AverageFlightsResponse() {
    }

    public AverageFlightsResponse(String airportCode,
                                  String direction,
                                  int days,
                                  long totalFlights,
                                  double averageFlightsPerDay,
                                  LocalDateTime from,
                                  LocalDateTime to) {
        this.airportCode = airportCode;
        this.direction = direction;
        this.days = days;
        this.totalFlights = totalFlights;
        this.averageFlightsPerDay = averageFlightsPerDay;
        this.from = from;
        this.to = to;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public long getTotalFlights() {
        return totalFlights;
    }

    public void setTotalFlights(long totalFlights) {
        this.totalFlights = totalFlights;
    }

    public double getAverageFlightsPerDay() {
        return averageFlightsPerDay;
    }

    public void setAverageFlightsPerDay(double averageFlightsPerDay) {
        this.averageFlightsPerDay = averageFlightsPerDay;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }
}