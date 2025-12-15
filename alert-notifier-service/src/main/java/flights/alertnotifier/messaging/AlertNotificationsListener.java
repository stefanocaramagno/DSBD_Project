package flights.alertnotifier.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import flights.alertnotifier.messaging.dto.ThresholdBreachNotificationEvent;
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

    public AlertNotificationsListener(ObjectMapper objectMapper,
                                      EmailNotificationService emailNotificationService) {
        this.objectMapper = objectMapper;
        this.emailNotificationService = emailNotificationService;
    }

    @KafkaListener(
            topics = "${app.alerts.notifications-input-topic:to-notifier}",
            groupId = "alert-notifier"
    )
    public void onMessage(ConsumerRecord<String, String> record) {
        String payload = record.value();
        log.debug("Messaggio di notifica ricevuto da Kafka. topic={}, partition={}, offset={}",
                record.topic(), record.partition(), record.offset());

        try {
            ThresholdBreachNotificationEvent event =
                    objectMapper.readValue(payload, ThresholdBreachNotificationEvent.class);
            emailNotificationService.sendThresholdBreachEmail(event);
        } catch (Exception ex) {
            log.error("Errore nella deserializzazione o gestione della notifica ricevuta da Kafka: {}",
                    ex.getMessage(), ex);
        }
    }
}