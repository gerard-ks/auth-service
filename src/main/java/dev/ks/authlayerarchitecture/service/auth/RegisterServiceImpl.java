package dev.ks.authlayerarchitecture.service.auth;

import dev.ks.authlayerarchitecture.constant.RoleConstants;
import dev.ks.authlayerarchitecture.dto.request.auth.RegisterRequest;
import dev.ks.authlayerarchitecture.entity.AccountEntity;
import dev.ks.authlayerarchitecture.entity.AccountRoleEntity;
import dev.ks.authlayerarchitecture.exception.account.EmailAlreadyExistsException;
import dev.ks.authlayerarchitecture.exception.role.RoleNotFoundException;
import dev.ks.authlayerarchitecture.repository.AccountRepository;
import dev.ks.authlayerarchitecture.repository.AccountRoleRepository;
import dev.ks.authlayerarchitecture.repository.RoleRepository;
import dev.ks.authlayerarchitecture.service.password.PasswordService;
import dev.ks.authlayerarchitecture.service.verification.EmailVerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;


@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;
    private final EmailVerificationService emailVerificationService;

    private final boolean verificationEnabled;

    public RegisterServiceImpl(AccountRepository accountRepository,
                               AccountRoleRepository accountRoleRepository,
                               RoleRepository roleRepository,
                               PasswordService passwordService,
                               EmailVerificationService emailVerificationService,
                               @Value("${auth-service.verification.enabled}") boolean verificationEnabled) {
        this.accountRepository = accountRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.roleRepository = roleRepository;
        this.passwordService = passwordService;
        this.emailVerificationService = emailVerificationService;
        this.verificationEnabled = verificationEnabled;
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        String email = request.email().toLowerCase();

        // Vérifier email unique
        if (accountRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException();
        }

        Instant now = Instant.now();
        UUID accountId = UUID.randomUUID();

        // Créer le compte
        AccountEntity account = new AccountEntity();
        account.setId(accountId);
        account.setEmail(email);
        account.setPasswordHash(
                passwordService.encode(request.password())
        );
        account.setFirstName(request.firstName());
        account.setLastName(request.lastName());
        account.setEmailVerified(!verificationEnabled);
        account.setEnabled(true);
        account.setPasswordChangedAt(null);
        account.setCreatedAt(now);
        account.setUpdatedAt(now);

        accountRepository.save(account);

        // Assigner rôle USER
        assignUserRole(accountId, now);

        // Initier vérification email si activée
        if (verificationEnabled) {
            emailVerificationService.initiateVerification(
                    accountId, email
            );
        }

        log.info(
                "Account registered [accountId={}] [verified={}]",
                accountId,
                !verificationEnabled
        );
    }

    private void assignUserRole(UUID accountId, Instant now) {
        var userRole = roleRepository
                .findByName(RoleConstants.USER)
                .orElseThrow(RoleNotFoundException::new);

        AccountRoleEntity accountRole = new AccountRoleEntity();
        accountRole.setAccountId(accountId);
        accountRole.setRoleId(userRole.getId());
        accountRole.setAssignedAt(now);
        accountRole.setAssignedBy(null); // NULL si REGISTRATION
        accountRole.setSource(RoleConstants.SOURCE_REGISTRATION);

        accountRoleRepository.save(accountRole);
    }
}
