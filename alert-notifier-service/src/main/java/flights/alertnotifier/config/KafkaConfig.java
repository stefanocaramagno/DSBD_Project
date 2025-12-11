package flights.alertnotifier.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Abilitazione del supporto Kafka per l'uso delle annotazioni @KafkaListener.
 */
@Configuration
@EnableKafka
public class KafkaConfig {
}
