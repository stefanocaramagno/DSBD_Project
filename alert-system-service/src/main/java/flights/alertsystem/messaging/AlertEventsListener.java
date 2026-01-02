package flights.alertsystem.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import flights.alertsystem.messaging.dto.FlightCollectionWindowUpdateEvent;
import flights.alertsystem.observability.AlertSystemMetrics;
import flights.alertsystem.service.AlertEvaluationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Listener Kafka che riceve gli eventi di aggiornamento delle finestre di raccolta
 * e li inoltra al servizio di valutazione delle soglie.
 *
 * Qui vengono anche registrate le metriche di:
 * - numero di valutazioni eseguite;
 * - durata dell'ultima valutazione.
 */
@Component
public class AlertEventsListener {

    private static final Logger log = LoggerFactory.getLogger(AlertEventsListener.class);

    private final ObjectMapper objectMapper;
    private final AlertEvaluationService evaluationService;
    private final AlertSystemMetrics metrics;

    public AlertEventsListener(ObjectMapper objectMapper,
                               AlertEvaluationService evaluationService,
                               AlertSystemMetrics metrics) {
        this.objectMapper = objectMapper;
        this.evaluationService = evaluationService;
        this.metrics = metrics;
    }

    @KafkaListener(
            topics = "${app.alerts.input-topic:to-alert-system}",
            groupId = "alert-system"
    )
    public void onMessage(ConsumerRecord<String, String> record) {
        String payload = record.value();

        log.debug(
                "Messaggio ricevuto da Kafka. topic={}, partition={}, offset={}",
                record.topic(), record.partition(), record.offset()
        );

        long startNanos = System.nanoTime();
        try {
            FlightCollectionWindowUpdateEvent event =
                    objectMapper.readValue(payload, FlightCollectionWindowUpdateEvent.class);

            // COUNTER: incremento del numero di valutazioni eseguite
            metrics.incrementEvaluations();

            evaluationService.processWindowUpdate(event);
        } catch (Exception ex) {
            log.error(
                    "Errore nella deserializzazione o nella valutazione dell'evento ricevuto da Kafka: {}",
                    ex.getMessage(),
                    ex
            );
        } finally {
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

            // GAUGE: durata dell'ultima valutazione
            metrics.setLastEvaluationDurationMs(durationMs);
        }
    }
}