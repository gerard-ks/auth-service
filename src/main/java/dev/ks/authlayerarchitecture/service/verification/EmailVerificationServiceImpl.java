package dev.ks.authlayerarchitecture.service.verification;

import dev.ks.authlayerarchitecture.cache.EmailVerificationStore;
import dev.ks.authlayerarchitecture.cache.VerificationData;
import dev.ks.authlayerarchitecture.entity.AccountEntity;
import dev.ks.authlayerarchitecture.exception.account.AccountNotFoundException;
import dev.ks.authlayerarchitecture.exception.verification.*;
import dev.ks.authlayerarchitecture.notification.EmailNotificationService;
import dev.ks.authlayerarchitecture.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final AccountRepository accountRepository;
    private final EmailVerificationStore emailVerificationStore;
    private final EmailNotificationService emailNotificationService;
    private final String baseUrl;
    private final int ttlMinutes;

    public EmailVerificationServiceImpl(AccountRepository accountRepository,
                                        EmailVerificationStore emailVerificationStore,
                                        EmailNotificationService emailNotificationService,
                                        @Value("${auth-service.base-url}") String baseUrl,
                                        @Value("${auth-service.verification.ttl-minutes}") int ttlMinutes) {
        this.accountRepository = accountRepository;
        this.emailVerificationStore = emailVerificationStore;
        this.emailNotificationService = emailNotificationService;
        this.baseUrl = baseUrl;
        this.ttlMinutes = ttlMinutes;
    }

    @Override
    public void initiateVerification(UUID accountId, String email) {
        UUID linkToken = UUID.randomUUID();
        String otpCode = generateOtp();

        emailVerificationStore.save(
                accountId, email, linkToken, otpCode
        );

        String linkUrl = baseUrl
                + "/api/auth/verify-email/token?token="
                + linkToken;

        emailNotificationService.sendVerificationEmail(
                email, linkUrl, otpCode, ttlMinutes
        );

        log.debug(
                "Verification initiated [accountId={}]", accountId
        );
    }

    @Override
    @Transactional
    public void verifyByToken(String token) {
        UUID linkToken = parseUuid(token,
                new InvalidVerificationTokenException()
        );

        VerificationData data = emailVerificationStore
                .findByLinkToken(linkToken)
                .orElseThrow(InvalidVerificationTokenException::new);

        if (Instant.parse(data.expiresAt()).isBefore(Instant.now())) {
            throw new VerificationTokenExpiredException();
        }

        activateAccount(data);

        emailVerificationStore.delete(
                data.accountId(), data.linkToken()
        );

        log.info(
                "Email verified by token [accountId={}]",
                data.accountId()
        );
    }

    @Override
    @Transactional
    public void verifyByOtp(String email, String otpCode) {
        AccountEntity account = accountRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(InvalidOtpException::new);

        VerificationData data = emailVerificationStore
                .findByAccountId(account.getId())
                .orElseThrow(InvalidOtpException::new);

        if (emailVerificationStore.isOtpBlocked(account.getId())) {
            throw new OtpBlockedException();
        }

        if (Instant.parse(data.expiresAt()).isBefore(Instant.now())) {
            throw new OtpExpiredException();
        }

        if (!data.otpCode().equals(otpCode)) {
            emailVerificationStore.incrementOtpAttempt(account.getId());
            throw new InvalidOtpException();
        }

        activateAccount(data);

        emailVerificationStore.delete(
                data.accountId(), data.linkToken()
        );

        log.info(
                "Email verified by OTP [accountId={}]",
                account.getId()
        );
    }

    @Override
    public void resendVerification(String email) {
        AccountEntity account = accountRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(AccountNotFoundException::new);

        if (!emailVerificationStore.canResend(account.getId())) {
            throw new ResendTooSoonException();
        }

        UUID linkToken = UUID.randomUUID();
        String otpCode = generateOtp();

        emailVerificationStore.save(
                account.getId(), email, linkToken, otpCode
        );

        emailVerificationStore.markResent(account.getId());

        String linkUrl = baseUrl
                + "/api/auth/verify-email/token?token="
                + linkToken;

        emailNotificationService.sendVerificationResendEmail(
                email, linkUrl, otpCode, ttlMinutes
        );

        log.debug(
                "Verification resent [accountId={}]",
                account.getId()
        );
    }

    private void activateAccount(VerificationData data) {
        AccountEntity account = accountRepository
                .findById(data.accountId())
                .orElseThrow(AccountNotFoundException::new);

        account.setEmailVerified(true);
        account.setUpdatedAt(Instant.now());
        accountRepository.save(account);
    }

    private String generateOtp() {
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }

    private UUID parseUuid(String value, RuntimeException ex) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw ex;
        }
    }
}
