package flights.alertnotifier.messaging.dto;

import java.time.Instant;

/**
 * Evento ricevuto dal broker quando viene superata
 * una soglia per un certo utente su un dato aeroporto.
 */
public class ThresholdBreachNotificationEvent {

    private String userEmail;
    private String airportCode;
    private String breachType; // "HIGH" oppure "LOW"
    private int actualValue;
    private int thresholdValue;
    private Instant windowBegin;
    private Instant windowEnd;

    public ThresholdBreachNotificationEvent() {
    }

    public ThresholdBreachNotificationEvent(String userEmail,
                                            String airportCode,
                                            String breachType,
                                            int actualValue,
                                            int thresholdValue,
                                            Instant windowBegin,
                                            Instant windowEnd) {
        this.userEmail = userEmail;
        this.airportCode = airportCode;
        this.breachType = breachType;
        this.actualValue = actualValue;
        this.thresholdValue = thresholdValue;
        this.windowBegin = windowBegin;
        this.windowEnd = windowEnd;
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

    public String getBreachType() {
        return breachType;
    }

    public void setBreachType(String breachType) {
        this.breachType = breachType;
    }

    public int getActualValue() {
        return actualValue;
    }

    public void setActualValue(int actualValue) {
        this.actualValue = actualValue;
    }

    public int getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(int thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public Instant getWindowBegin() {
        return windowBegin;
    }

    public void setWindowBegin(Instant windowBegin) {
        this.windowBegin = windowBegin;
    }

    public Instant getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(Instant windowEnd) {
        this.windowEnd = windowEnd;
    }
}
