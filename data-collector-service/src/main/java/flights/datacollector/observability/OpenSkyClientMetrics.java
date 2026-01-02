package flights.datacollector.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class OpenSkyClientMetrics {

    private final Counter requestsTotal;
    private final Counter requestErrorsTotal;
    private final Counter fallbacksTotal;
    private final AtomicLong lastFetchDurationMs;

    public OpenSkyClientMetrics(MeterRegistry registry) {
        this.requestsTotal = Counter.builder("opensky_requests_total")
                .description("Numero totale di richieste effettuate verso OpenSky")
                .register(registry);

        this.requestErrorsTotal = Counter.builder("opensky_request_errors_total")
                .description("Numero totale di richieste OpenSky fallite (HTTP 4xx/5xx non-404 o errori di comunicazione)")
                .register(registry);

        this.fallbacksTotal = Counter.builder("opensky_fallback_total")
                .description("Numero totale di attivazioni del fallback per le chiamate OpenSky")
                .register(registry);

        this.lastFetchDurationMs = new AtomicLong(0L);
        Gauge.builder("opensky_last_fetch_duration_ms", lastFetchDurationMs, AtomicLong::get)
                .description("Durata (ms) dell'ultima chiamata OpenSky (fetchFlights)")
                .register(registry);
    }

    public void incrementRequests() {
        requestsTotal.increment();
    }

    public void incrementRequestErrors() {
        requestErrorsTotal.increment();
    }

    public void incrementFallbacks() {
        fallbacksTotal.increment();
    }

    public void setLastFetchDurationMs(long ms) {
        lastFetchDurationMs.set(Math.max(ms, 0L));
    }
}