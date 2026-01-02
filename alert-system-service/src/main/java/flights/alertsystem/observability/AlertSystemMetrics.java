package flights.alertsystem.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class AlertSystemMetrics {

    private final Counter evaluationsTotal;
    private final Counter notificationsTotal;
    private final AtomicLong lastEvaluationDurationMs;

    public AlertSystemMetrics(MeterRegistry meterRegistry) {
        this.evaluationsTotal = Counter.builder("alert_system_evaluations_total")
                .description("Numero totale di valutazioni delle finestre di raccolta effettuate dall'Alert System")
                .register(meterRegistry);

        this.notificationsTotal = Counter.builder("alert_system_notifications_total")
                .description("Numero totale di notifiche di superamento soglia generate dall'Alert System")
                .register(meterRegistry);

        this.lastEvaluationDurationMs = new AtomicLong(0L);

        Gauge.builder("alert_system_last_eval_duration_ms", lastEvaluationDurationMs, AtomicLong::get)
                .description("Durata (ms) dell'ultima valutazione delle soglie eseguita dall'Alert System")
                .register(meterRegistry);
    }

    public void incrementEvaluations() {
        evaluationsTotal.increment();
    }

    public void incrementNotifications() {
        notificationsTotal.increment();
    }
    
    public void setLastEvaluationDurationMs(long durationMs) {
        lastEvaluationDurationMs.set(Math.max(durationMs, 0L));
    }
}