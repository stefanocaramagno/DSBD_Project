package flights.alertnotifier.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Se usi LocalDate/LocalDateTime, registriamo il modulo Java 8
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}