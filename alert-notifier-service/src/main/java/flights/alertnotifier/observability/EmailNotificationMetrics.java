package flights.alertnotifier.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class EmailNotificationMetrics {

    private final Counter emailsSentTotal;
    private final Counter emailSendErrorsTotal;
    private final Counter emailRateLimitedTotal;
    private final AtomicLong lastSendDurationMs;

    public EmailNotificationMetrics(MeterRegistry registry) {
        this.emailsSentTotal = Counter.builder("email_sent_total")
                .description("Numero totale di email inviate con successo")
                .register(registry);

        this.emailSendErrorsTotal = Counter.builder("email_send_errors_total")
                .description("Numero totale di errori durante l'invio email (SMTP o errori inattesi)")
                .register(registry);

        this.emailRateLimitedTotal = Counter.builder("email_rate_limited_total")
                .description("Numero totale di invii bloccati dal rate limit del provider SMTP")
                .register(registry);

        this.lastSendDurationMs = new AtomicLong(0L);
        Gauge.builder("email_last_send_duration_ms", lastSendDurationMs, AtomicLong::get)
                .description("Durata (ms) dell'ultimo invio email (chiamata al mail sender)")
                .register(registry);
    }

    public void incrementEmailsSent() {
        emailsSentTotal.increment();
    }

    public void incrementEmailSendErrors() {
        emailSendErrorsTotal.increment();
    }

    public void incrementEmailRateLimited() {
        emailRateLimitedTotal.increment();
    }

    public void setLastSendDurationMs(long ms) {
        lastSendDurationMs.set(Math.max(ms, 0L));
    }
}