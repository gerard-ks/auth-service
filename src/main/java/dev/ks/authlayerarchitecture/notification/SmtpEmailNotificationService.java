package dev.ks.authlayerarchitecture.notification;

import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer;
import dev.ks.authlayerarchitecture.config.RQueueConfig;
import dev.ks.authlayerarchitecture.job.EmailJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SmtpEmailNotificationService implements EmailNotificationService {

    private final RqueueMessageEnqueuer rqueueMessageEnqueuer;
    private final RQueueConfig rQueueConfig;
    private final String appUrl;
    private final String appName;

    private static final String SUBJECT_VERIFICATION      =
            "Verify your email address";
    private static final String SUBJECT_RESEND             =
            "Verify your email address (resend)";
    private static final String SUBJECT_RESET              =
            "Reset your password";
    private static final String SUBJECT_RESET_CONFIRM      =
            "Your password has been reset";
    private static final String SUBJECT_PASSWORD_CHANGED   =
            "Your password has been changed";
    private static final String SUBJECT_ACCOUNT_DISABLED   =
            "Your account has been disabled";

    public SmtpEmailNotificationService(RqueueMessageEnqueuer rqueueMessageEnqueuer,
                                        RQueueConfig rQueueConfig,
                                        @Value("${auth-service.base-url}") String appUrl,
                                        @Value("${spring.application.name}") String appName) {
        this.rqueueMessageEnqueuer = rqueueMessageEnqueuer;
        this.rQueueConfig = rQueueConfig;
        this.appUrl = appUrl;
        this.appName = appName;
    }

    @Override
    public void sendVerificationEmail(String to, String linkUrl, String otpCode, int ttlMinutes) {
        enqueue(new EmailJob(
                "email/verification",
                to,
                SUBJECT_VERIFICATION,
                Map.of(
                        "appName",    appName,
                        "appUrl",     appUrl,
                        "linkUrl",    linkUrl,
                        "otpCode",    otpCode,
                        "ttlMinutes", ttlMinutes
                )
        ));
    }

    @Override
    public void sendVerificationResendEmail(String to, String linkUrl, String otpCode, int ttlMinutes) {
        enqueue(new EmailJob(
                "email/verification-resend",
                to,
                SUBJECT_RESEND,
                Map.of(
                        "appName",    appName,
                        "appUrl",     appUrl,
                        "linkUrl",    linkUrl,
                        "otpCode",    otpCode,
                        "ttlMinutes", ttlMinutes
                )
        ));
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetUrl, int ttlMinutes) {
        enqueue(new EmailJob(
                "email/password-reset",
                to,
                SUBJECT_RESET,
                Map.of(
                        "appName",    appName,
                        "appUrl",     appUrl,
                        "resetUrl",   resetUrl,
                        "ttlMinutes", ttlMinutes
                )
        ));
    }

    @Override
    public void sendPasswordResetConfirmEmail(String to, String datetime) {
        enqueue(new EmailJob(
                "email/password-reset-confirm",
                to,
                SUBJECT_RESET_CONFIRM,
                Map.of(
                        "appName",  appName,
                        "appUrl",   appUrl,
                        "datetime", datetime
                )
        ));
    }

    @Override
    public void sendPasswordChangedEmail(String to, String datetime) {
        enqueue(new EmailJob(
                "email/password-changed",
                to,
                SUBJECT_PASSWORD_CHANGED,
                Map.of(
                        "appName",  appName,
                        "appUrl",   appUrl,
                        "datetime", datetime
                )
        ));
    }

    @Override
    public void sendAccountDisabledEmail(String to, String supportUrl) {
        enqueue(new EmailJob(
                "email/account-disabled",
                to,
                SUBJECT_ACCOUNT_DISABLED,
                Map.of(
                        "appName",    appName,
                        "appUrl",     appUrl,
                        "supportUrl", supportUrl
                )
        ));
    }

    private void enqueue(EmailJob job) {
        try {
            rqueueMessageEnqueuer.enqueue(
                    rQueueConfig.getQueueName(),
                    job
            );

            log.debug(
                    "Email job enqueued [template={}] [to={}]",
                    job.templateName(),
                    job.to()
            );

        } catch (Exception ex) {
            log.error(
                    "Failed to enqueue email job [template={}]",
                    job.templateName(),
                    ex
            );
            // Ne pas propager l'exception
            // échec email ≠ échec flux principal
        }
    }
}
