package flights.datacollector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flights.datacollector.messaging.dto.AirportFlightsWindowSnapshot;
import flights.datacollector.messaging.dto.FlightCollectionWindowUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Servizio responsabile della pubblicazione su Kafka degli eventi
 * di aggiornamento prodotti dal Data Collector.
 */
@Service
public class AlertUpdateProducer {

    private static final Logger log = LoggerFactory.getLogger(AlertUpdateProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public AlertUpdateProducer(KafkaTemplate<String, String> kafkaTemplate,
                               ObjectMapper objectMapper,
                               @Value("${app.alerts.topic:to-alert-system}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    /**
     * Pubblica su Kafka un evento che descrive l'aggiornamento dei voli
     * effettuato nella finestra [windowBegin, windowEnd].
     *
     * @param windowBegin inizio della finestra di raccolta (Instant UTC)
     * @param windowEnd   fine della finestra di raccolta (Instant UTC)
     * @param snapshots   lista di snapshot per ciascun aeroporto
     */
    public void publishCollectionWindowUpdate(Instant windowBegin,
                                              Instant windowEnd,
                                              List<AirportFlightsWindowSnapshot> snapshots) {
        if (snapshots == null || snapshots.isEmpty()) {
            log.info("Nessun aggiornamento voli da pubblicare su Kafka per l'intervallo [{} - {}].",
                    windowBegin, windowEnd);
            return;
        }

        FlightCollectionWindowUpdateEvent event =
                new FlightCollectionWindowUpdateEvent(windowBegin, windowEnd, snapshots);

        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, payload);
            log.info(
                    "Evento di aggiornamento voli pubblicato su Kafka. topic={}, window=[{} - {}], airportsCount={}",
                    topic, windowBegin, windowEnd, snapshots.size()
            );
        } catch (JsonProcessingException e) {
            log.error(
                    "Errore nella serializzazione dell'evento di aggiornamento voli per l'intervallo [{} - {}]: {}",
                    windowBegin, windowEnd, e.getMessage(), e
            );
        }
    }
}