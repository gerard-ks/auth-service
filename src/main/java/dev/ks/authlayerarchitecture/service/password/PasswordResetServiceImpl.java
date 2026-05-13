package dev.ks.authlayerarchitecture.service.password;

import dev.ks.authlayerarchitecture.cache.PasswordResetStore;
import dev.ks.authlayerarchitecture.entity.AccountEntity;
import dev.ks.authlayerarchitecture.exception.account.AccountNotFoundException;
import dev.ks.authlayerarchitecture.exception.password.InvalidCurrentPasswordException;
import dev.ks.authlayerarchitecture.exception.token.InvalidResetTokenException;
import dev.ks.authlayerarchitecture.notification.EmailNotificationService;
import dev.ks.authlayerarchitecture.repository.AccountRepository;
import dev.ks.authlayerarchitecture.service.auth.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;


@Slf4j
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final AccountRepository accountRepository;
    private final PasswordResetStore passwordResetStore;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final EmailNotificationService emailNotificationService;

    private final String baseUrl;

    private final int ttlMinutes;

    public PasswordResetServiceImpl(AccountRepository accountRepository,
                                    PasswordResetStore passwordResetStore,
                                    PasswordService passwordService,
                                    TokenService tokenService,
                                    EmailNotificationService emailNotificationService,
                                    @Value("${auth-service.base-url}") String baseUrl,
                                    @Value("${auth-service.verification.ttl-minutes}") int ttlMinutes) {
        this.accountRepository = accountRepository;
        this.passwordResetStore = passwordResetStore;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
        this.emailNotificationService = emailNotificationService;
        this.baseUrl = baseUrl;
        this.ttlMinutes = ttlMinutes;
    }

    @Override
    public void forgotPassword(String email) {
        // toujours 200, pas d'exception
        accountRepository
                .findByEmailIgnoreCase(email)
                .ifPresent(account -> {
                    UUID token = UUID.randomUUID();

                    passwordResetStore.save(account.getId(), token);

                    String resetUrl = baseUrl
                            + "/api/auth/reset-password?token="
                            + token;

                    emailNotificationService.sendPasswordResetEmail(
                            email, resetUrl, ttlMinutes
                    );

                    log.debug(
                            "Password reset initiated [accountId={}]",
                            account.getId()
                    );
                });
    }

    @Override
    @Transactional
    public void resetPassword(String tokenStr, String newPassword) {
        UUID token = parseUuid(tokenStr);

        UUID accountId = passwordResetStore
                .findAccountIdByToken(token)
                .orElseThrow(InvalidResetTokenException::new);

        // vérifier token soumis == token Redis
        UUID storedToken = passwordResetStore
                .findTokenByAccountId(accountId)
                .orElseThrow(InvalidResetTokenException::new);

        if (!token.equals(storedToken)) {
            throw new InvalidResetTokenException();
        }

        AccountEntity account = accountRepository
                .findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        // nouveau ≠ ancien
        passwordService.assertNotSamePassword(
                newPassword, account.getPasswordHash()
        );

        // Changer le mot de passe
        account.setPasswordHash(
                passwordService.encode(newPassword)
        );
        account.setPasswordChangedAt(Instant.now());
        account.setUpdatedAt(Instant.now());
        accountRepository.save(account);

        // Supprimer le token de reset
        passwordResetStore.delete(accountId, token);

        // revokeAll
        tokenService.revokeAll(accountId);

        // Notification
        emailNotificationService.sendPasswordResetConfirmEmail(
                account.getEmail(),
                Instant.now().toString()
        );

        log.info("Password reset successful [accountId={}]", accountId);
    }

    @Override
    @Transactional
    public void changePassword(
            UUID accountId,
            String currentPassword,
            String newPassword
    ) {
        AccountEntity account = accountRepository
                .findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        // Vérifier ancien mot de passe
        if (!passwordService.matches(
                currentPassword, account.getPasswordHash()
        )) {
            throw new InvalidCurrentPasswordException();
        }

        // nouveau ≠ ancien
        passwordService.assertNotSamePassword(
                newPassword, account.getPasswordHash()
        );

        // Changer le mot de passe
        account.setPasswordHash(
                passwordService.encode(newPassword)
        );
        account.setPasswordChangedAt(Instant.now());
        account.setUpdatedAt(Instant.now());
        accountRepository.save(account);

        // Notification
        emailNotificationService.sendPasswordChangedEmail(
                account.getEmail(),
                Instant.now().toString()
        );

        log.info(
                "Password changed [accountId={}]", accountId
        );
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new InvalidResetTokenException();
        }
    }
}
