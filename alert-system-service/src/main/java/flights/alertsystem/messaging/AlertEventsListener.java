package flights.alertsystem.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import flights.alertsystem.messaging.dto.FlightCollectionWindowUpdateEvent;
import flights.alertsystem.service.AlertEvaluationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listener Kafka che riceve gli eventi di aggiornamento dal Data Collector
 * e li passa al servizio di valutazione delle soglie.
 */
@Component
public class AlertEventsListener {

    private static final Logger log = LoggerFactory.getLogger(AlertEventsListener.class);

    private final ObjectMapper objectMapper;
    private final AlertEvaluationService evaluationService;

    public AlertEventsListener(ObjectMapper objectMapper,
                               AlertEvaluationService evaluationService) {
        this.objectMapper = objectMapper;
        this.evaluationService = evaluationService;
    }

    @KafkaListener(
            topics = "${app.alerts.input-topic:to-alert-system}",
            groupId = "alert-system"
    )
    public void onMessage(ConsumerRecord<String, String> record) {
        String payload = record.value();
        log.debug("Messaggio ricevuto da Kafka. topic={}, partition={}, offset={}",
                record.topic(), record.partition(), record.offset());

        try {
            FlightCollectionWindowUpdateEvent event =
                    objectMapper.readValue(payload, FlightCollectionWindowUpdateEvent.class);
            evaluationService.processWindowUpdate(event);
        } catch (Exception ex) {
            log.error("Errore nella deserializzazione o nella valutazione dell'evento ricevuto da Kafka: {}",
                    ex.getMessage(), ex);
        }
    }
}