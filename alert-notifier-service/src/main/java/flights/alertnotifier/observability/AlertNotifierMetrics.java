package flights.alertnotifier.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class AlertNotifierMetrics {

    private final Counter notificationsConsumedTotal;
    private final Counter notificationsProcessingErrorsTotal;
    private final AtomicLong lastProcessingDurationMs;

    public AlertNotifierMetrics(MeterRegistry registry) {
        this.notificationsConsumedTotal = Counter.builder("alert_notifier_notifications_consumed_total")
                .description("Numero totale di notifiche consumate da Kafka")
                .register(registry);

        this.notificationsProcessingErrorsTotal = Counter.builder("alert_notifier_notifications_processing_errors_total")
                .description("Numero totale di errori di processing (deserializzazione/gestione) delle notifiche")
                .register(registry);

        this.lastProcessingDurationMs = new AtomicLong(0L);
        Gauge.builder("alert_notifier_last_processing_duration_ms", lastProcessingDurationMs, AtomicLong::get)
                .description("Durata (ms) dell'ultimo processing end-to-end della notifica (deserializzazione + invio email)")
                .register(registry);
    }

    public void incrementNotificationsConsumed() {
        notificationsConsumedTotal.increment();
    }

    public void incrementNotificationsProcessingErrors() {
        notificationsProcessingErrorsTotal.increment();
    }

    public void setLastProcessingDurationMs(long ms) {
        lastProcessingDurationMs.set(Math.max(ms, 0L));
    }
}