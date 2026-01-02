package flights.alertnotifier.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import flights.alertnotifier.messaging.dto.ThresholdBreachNotificationEvent;
import flights.alertnotifier.observability.AlertNotifierMetrics;
import flights.alertnotifier.service.EmailNotificationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listener Kafka che riceve le notifiche di superamento soglia
 * e delega l'invio dell'email al servizio dedicato.
 */
@Component
public class AlertNotificationsListener {

    private static final Logger log = LoggerFactory.getLogger(AlertNotificationsListener.class);

    private final ObjectMapper objectMapper;
    private final EmailNotificationService emailNotificationService;
    private final AlertNotifierMetrics metrics;

    public AlertNotificationsListener(ObjectMapper objectMapper,
                                      EmailNotificationService emailNotificationService,
                                      AlertNotifierMetrics metrics) {
        this.objectMapper = objectMapper;
        this.emailNotificationService = emailNotificationService;
        this.metrics = metrics;
    }

    @KafkaListener(
            topics = "${app.alerts.notifications-input-topic:to-notifier}",
            groupId = "alert-notifier"
    )
    public void onMessage(ConsumerRecord<String, String> record) {
        String payload = record.value();
        log.debug("Messaggio di notifica ricevuto da Kafka. topic={}, partition={}, offset={}",
                record.topic(), record.partition(), record.offset());

        metrics.incrementNotificationsConsumed();
        final long startNs = System.nanoTime();

        try {
            ThresholdBreachNotificationEvent event =
                    objectMapper.readValue(payload, ThresholdBreachNotificationEvent.class);
            emailNotificationService.sendThresholdBreachEmail(event);
        } catch (Exception ex) {
            metrics.incrementNotificationsProcessingErrors();
            log.error("Errore nella deserializzazione o gestione della notifica ricevuta da Kafka: {}",
                    ex.getMessage(), ex);
        }
        finally {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000L;
            metrics.setLastProcessingDurationMs(durationMs);
        }

    }
}