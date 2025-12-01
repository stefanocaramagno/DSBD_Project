package flights.datacollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DataCollectorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataCollectorServiceApplication.class, args);
    }
}
