package flights.alertsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Abilitazione del supporto Kafka per l'utilizzo delle annotazioni @KafkaListener.
 */
@Configuration
@EnableKafka
public class KafkaConfig {
}