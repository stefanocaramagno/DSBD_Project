package flights.alertsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flights.alertsystem.messaging.dto.ThresholdBreachNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Producer Kafka responsabile dell'invio delle notifiche di superamento soglia
 * verso il topic configurato.
 */
@Service
public class AlertNotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(AlertNotificationProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String outputTopic;

    public AlertNotificationProducer(KafkaTemplate<String, String> kafkaTemplate,
                                     ObjectMapper objectMapper,
                                     @Value("${app.alerts.output-topic:to-notifier}") String outputTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.outputTopic = outputTopic;
    }

    public void publishNotification(ThresholdBreachNotificationEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(outputTopic, event.getUserEmail(), payload);
            log.info(
                    "Notifica di superamento soglia pubblicata su Kafka. topic={}, userEmail={}, airportCode={}, breachType={}",
                    outputTopic, event.getUserEmail(), event.getAirportCode(), event.getBreachType()
            );
        } catch (JsonProcessingException e) {
            log.error(
                    "Errore nella serializzazione della notifica di superamento soglia per userEmail={}, airportCode={}: {}",
                    event.getUserEmail(), event.getAirportCode(), e.getMessage(), e
            );
        }
    }
}