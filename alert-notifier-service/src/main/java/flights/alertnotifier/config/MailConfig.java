package flights.alertnotifier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Parametri base da env
        mailSender.setHost(System.getenv().getOrDefault("MAIL_HOST", "localhost"));
        mailSender.setPort(Integer.parseInt(System.getenv().getOrDefault("MAIL_PORT", "25")));
        mailSender.setUsername(System.getenv().getOrDefault("MAIL_USERNAME", ""));
        mailSender.setPassword(System.getenv().getOrDefault("MAIL_PASSWORD", ""));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");

        // Leggo le flag da env, default false
        String auth = System.getenv().getOrDefault("MAIL_SMTP_AUTH", "false");
        String starttls = System.getenv().getOrDefault("MAIL_SMTP_STARTTLS_ENABLE", "false");

        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);

        // Per debug in console
        props.put("mail.debug", "true");

        return mailSender;
    }
}