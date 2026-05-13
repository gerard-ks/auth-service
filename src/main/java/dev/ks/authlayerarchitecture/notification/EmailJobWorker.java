package dev.ks.authlayerarchitecture.notification;

import com.github.sonus21.rqueue.annotation.RqueueListener;
import dev.ks.authlayerarchitecture.job.EmailJob;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailJobWorker {
    private final JavaMailSender mailSender;
    private final TemplateRenderer templateRenderer;

    public EmailJobWorker(JavaMailSender mailSender, TemplateRenderer templateRenderer) {
        this.mailSender = mailSender;
        this.templateRenderer = templateRenderer;
    }


    @RqueueListener(
            value                = "${auth-service.email-queue.queue-name}",
            numRetries           = "${auth-service.email-queue.max-attempts}",
            visibilityTimeout    = "${auth-service.email-queue.job-deadline-ms}",
            deadLetterQueue  = "${auth-service.email-queue.queue-name}.dlq"
    )
    public void processEmailJob(EmailJob job) {
        log.debug(
                "Processing email job [template={}] [to={}]",
                job.templateName(),
                job.to()
        );

        try {
            String htmlContent = templateRenderer.render(
                    job.templateName(),
                    job.variables()
            );

            sendEmail(job.to(), job.subject(), htmlContent);

            log.info(
                    "Email sent successfully [template={}] [to={}]",
                    job.templateName(),
                    job.to()
            );

        } catch (Exception ex) {
            log.error(
                    "Failed to process email job [template={}] [to={}]",
                    job.templateName(),
                    job.to(),
                    ex
            );
            // RQueue retry automatiquement
            throw new RuntimeException(
                    "Email job processing failed", ex
            );
        }
    }

    @RqueueListener(
            value = "${auth-service.email-queue.queue-name}.dlq"
    )
    public void processDeadLetterJob(EmailJob job) {
        log.error(
                "Email job moved to DLQ after max attempts " +
                        "[template={}] [to={}]",
                job.templateName(),
                job.to()
        );
        // Ici on pourrait :
        // alerter une équipe ops
        // sauvegarder en DB pour retry manuel
        // envoyer une métrique Micrometer
    }

    private void sendEmail(
            String to,
            String subject,
            String htmlContent
    ) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                true,
                "UTF-8"
        );

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
