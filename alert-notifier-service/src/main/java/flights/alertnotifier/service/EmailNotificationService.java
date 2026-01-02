package flights.alertnotifier.service;

import flights.alertnotifier.messaging.dto.ThresholdBreachNotificationEvent;
import flights.alertnotifier.observability.EmailNotificationMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Servizio responsabile della costruzione e dell'invio
 * delle email di notifica agli utenti.
 */
@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private static final DateTimeFormatter WINDOW_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.of("UTC"));

    /**
     * Intervallo minimo tra due invii per evitare il rate limit di Mailtrap.
     * Esempio: 200 ms ≈ 5 email/sec.
     * Puoi aumentare questo valore se continui a ricevere il 550.
     */
    private static final long MIN_INTERVAL_MS = 200L;

    /** Timestamp dell'ultima email inviata con successo (per il throttling). */
    private volatile long lastSendTimestamp = 0L;

    private final JavaMailSender mailSender;
    private final EmailNotificationMetrics metrics;
    private final String fromAddress;

    public EmailNotificationService(JavaMailSender mailSender,
                                    EmailNotificationMetrics metrics,
                                    @Value("${app.alerts.mail.from:no-reply@system-alerts.local}") String fromAddress) {
        this.mailSender = mailSender;
        this.metrics = metrics;
        this.fromAddress = fromAddress;
    }

    public void sendThresholdBreachEmail(ThresholdBreachNotificationEvent event) {
        String subject = buildSubject(event);
        String body = buildBody(event);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(event.getUserEmail());
        message.setSubject(subject);
        message.setText(body);

        long sendStartNs = 0L;

        try {
            // Limita il rate di invio per non saturare Mailtrap
            throttleIfNeeded();

            sendStartNs = System.nanoTime();
            mailSender.send(message);
            long durationMs = (System.nanoTime() - sendStartNs) / 1_000_000L;
            metrics.setLastSendDurationMs(durationMs);
            metrics.incrementEmailsSent();

            log.info("Email di notifica inviata a {} per aeroporto {} (breachType={})",
                    event.getUserEmail(), event.getAirportCode(), event.getBreachType());
        }
        catch (MailException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "";

            if (sendStartNs > 0L) {
                long durationMs = (System.nanoTime() - sendStartNs) / 1_000_000L;
                metrics.setLastSendDurationMs(durationMs);
            }

            metrics.incrementEmailSendErrors();

            // Caso specifico: rate limit di Mailtrap (Too many emails per second)
            if (msg.contains("Too many emails per second")) {
                metrics.incrementEmailRateLimited();
                log.warn("Rate limit SMTP di Mailtrap raggiunto durante l'invio a {} per aeroporto {}: {}. " +
                                "La notifica è stata scartata o verrà ritentata al prossimo evento.",
                        event.getUserEmail(), event.getAirportCode(), msg);
                // Qui NON rilanciamo l'eccezione: il listener Kafka continua a funzionare.
            } else {
                // Altri errori SMTP: li logghiamo a livello ERROR per evidenziarli
                log.error("Errore SMTP durante l'invio dell'email a {} per aeroporto {}: {}",
                        event.getUserEmail(), event.getAirportCode(), msg, ex);
            }
        }
        catch (Exception ex) {
            // Errori imprevisti (non MailException)
            if (sendStartNs > 0L) {
                long durationMs = (System.nanoTime() - sendStartNs) / 1_000_000L;
                metrics.setLastSendDurationMs(durationMs);
            }

            metrics.incrementEmailSendErrors();
            log.error("Errore inatteso durante l'invio dell'email a {} per aeroporto {}: {}",
                    event.getUserEmail(), event.getAirportCode(), ex.getMessage(), ex);
        }
    }

    /**
     * Implementazione molto semplice di throttling:
     * se l'ultima email è stata inviata troppo di recente, attende
     * finché non è trascorso MIN_INTERVAL_MS.
     *
     * Il metodo è synchronized per evitare race condition tra thread diversi
     * del listener Kafka.
     */
    private synchronized void throttleIfNeeded() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastSendTimestamp;

        if (elapsed < MIN_INTERVAL_MS) {
            long sleepMs = MIN_INTERVAL_MS - elapsed;
            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        lastSendTimestamp = System.currentTimeMillis();
    }

    private String buildSubject(ThresholdBreachNotificationEvent event) {
        String direction = "HIGH".equalsIgnoreCase(event.getBreachType())
                ? "superamento soglia"
                : "soglia inferiore non raggiunta";

        return String.format("[ALERT] %s - %s", event.getAirportCode(), direction);
    }

    private String buildBody(ThresholdBreachNotificationEvent event) {
        String window = WINDOW_FORMATTER.format(event.getWindowBegin()) + " - " +
                WINDOW_FORMATTER.format(event.getWindowEnd());

        String thresholdLabel = "HIGH".equalsIgnoreCase(event.getBreachType())
                ? "valore sopra la soglia"
                : "valore sotto la soglia";

        return new StringBuilder()
                .append("Gentile utente,\n\n")
                .append("È stato rilevato un evento di superamento soglia per l'aeroporto ")
                .append(event.getAirportCode()).append(".\n\n")
                .append("Dettagli dell'evento:\n")
                .append(" - Tipo di soglia: ").append(event.getBreachType()).append(" (")
                .append(thresholdLabel).append(")\n")
                .append(" - Valore soglia: ").append(event.getThresholdValue()).append("\n")
                .append(" - Valore misurato (numero di voli in arrivo e/o partenza): ")
                .append(event.getActualValue()).append("\n")
                .append(" - Intervallo di riferimento: ").append(window).append(" (UTC)\n\n")
                .append("Cordiali saluti,\n")
                .append("Flights Monitoring System\n")
                .toString();
    }
}