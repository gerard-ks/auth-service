package dev.ks.authlayerarchitecture.service.auth;

import dev.ks.authlayerarchitecture.dto.request.auth.LoginRequest;
import dev.ks.authlayerarchitecture.entity.AccountEntity;
import dev.ks.authlayerarchitecture.exception.auth.AccountDisabledException;
import dev.ks.authlayerarchitecture.exception.auth.AccountNotVerifiedException;
import dev.ks.authlayerarchitecture.exception.auth.InvalidCredentialsException;
import dev.ks.authlayerarchitecture.repository.AccountRepository;
import dev.ks.authlayerarchitecture.repository.AccountRoleRepository;
import dev.ks.authlayerarchitecture.security.jwt.TokenPair;
import dev.ks.authlayerarchitecture.service.password.PasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final PasswordService passwordService;
    private final BruteForceService bruteForceService;
    private final TokenService tokenService;

    public AuthServiceImpl(AccountRepository accountRepository, AccountRoleRepository accountRoleRepository, PasswordService passwordService, BruteForceService bruteForceService, TokenService tokenService) {
        this.accountRepository = accountRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.passwordService = passwordService;
        this.bruteForceService = bruteForceService;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public TokenPair login(
            LoginRequest request,
            String strategy
    ) {
        String email = request.email().toLowerCase();

        // Brute force EN PREMIER
        bruteForceService.checkNotBlocked(email);

        // Recherche compte
        AccountEntity account = accountRepository
                .findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    // Anti-énumération :
                    // on enregistre la tentative même si compte inexistant
                    bruteForceService.recordFailedAttempt(email);
                    throw new InvalidCredentialsException();
                });

        // Vérifier anonymisé
        if (account.getAnonymizedAt() != null) {
            bruteForceService.recordFailedAttempt(email);
            throw new InvalidCredentialsException();
        }

        // Vérifier password
        if (!passwordService.matches(
                request.password(),
                account.getPasswordHash()
        )) {
            bruteForceService.recordFailedAttempt(email);
            throw new InvalidCredentialsException();
        }

        // Vérifier email vérifié
        if (!account.isEmailVerified()) {
            throw new AccountNotVerifiedException();
        }

        // Vérifier compte activé
        if (!account.isEnabled()) {
            throw new AccountDisabledException();
        }

        // Charger les rôles
        List<String> roles = accountRoleRepository
                .findRoleNamesByAccountId(account.getId());

        // Générer les tokens
        TokenPair pair = tokenService.generatePair(
                account.getId(),
                account.getEmail(),
                roles,
                strategy
        );

        // Reset brute force après succès
        bruteForceService.reset(email);

        log.info("Login successful [accountId={}]", account.getId());

        return pair;
    }
}
