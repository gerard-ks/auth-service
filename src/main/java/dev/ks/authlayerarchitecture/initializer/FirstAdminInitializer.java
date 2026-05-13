package dev.ks.authlayerarchitecture.initializer;

import dev.ks.authlayerarchitecture.constant.RoleConstants;
import dev.ks.authlayerarchitecture.entity.AccountEntity;
import dev.ks.authlayerarchitecture.entity.AccountRoleEntity;
import dev.ks.authlayerarchitecture.repository.AccountRepository;
import dev.ks.authlayerarchitecture.repository.AccountRoleRepository;
import dev.ks.authlayerarchitecture.repository.RoleRepository;
import dev.ks.authlayerarchitecture.service.password.PasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class FirstAdminInitializer implements ApplicationRunner {

    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;

    private final String adminEmail;

    private final String adminPassword;

    private final String adminFirstName;

    private final String adminLastName;

    public FirstAdminInitializer(AccountRepository accountRepository,
                                 AccountRoleRepository accountRoleRepository,
                                 RoleRepository roleRepository,
                                 PasswordService passwordService,
                                 @Value("${auth-service.initial-admin.email}") String adminEmail,
                                 @Value("${auth-service.initial-admin.password}") String adminPassword,
                                 @Value("${auth-service.initial-admin.first-name}") String adminFirstName,
                                 @Value("${auth-service.initial-admin.last-name}") String adminLastName) {
        this.accountRepository = accountRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.roleRepository = roleRepository;
        this.passwordService = passwordService;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminFirstName = adminFirstName;
        this.adminLastName = adminLastName;
    }


    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String email = adminEmail.toLowerCase();

        // Skip silencieux si admin existe déjà
        if (accountRepository.existsByEmailIgnoreCase(email)) {
            log.info(
                    "First admin already exists [email={}] — skipping",
                    email
            );
            return;
        }

        log.info("Creating first admin account [email={}]", email);

        Instant now = Instant.now();
        UUID accountId = UUID.randomUUID();

        // Créer le compte admin
        AccountEntity admin = new AccountEntity();
        admin.setId(accountId);
        admin.setEmail(email);
        admin.setPasswordHash(passwordService.encode(adminPassword));
        admin.setFirstName(adminFirstName);
        admin.setLastName(adminLastName);
        admin.setEmailVerified(true);   // ACTIVE dès le départ
        admin.setEnabled(true);
        admin.setPasswordChangedAt(null);
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);

        accountRepository.save(admin);

        // Assigner rôle USER
        assignRole(accountId, RoleConstants.USER, now);

        // Assigner rôle ADMIN
        assignRole(accountId, RoleConstants.ADMIN, now);

        log.info(
                "First admin created successfully [accountId={}]",
                accountId
        );
    }

    private void assignRole(
            UUID accountId,
            String roleName,
            Instant now
    ) {
        roleRepository
                .findByName(roleName)
                .ifPresentOrElse(
                        role -> {
                            AccountRoleEntity accountRole =
                                    new AccountRoleEntity();
                            accountRole.setAccountId(accountId);
                            accountRole.setRoleId(role.getId());
                            accountRole.setAssignedAt(now);
                            accountRole.setAssignedBy(null);
                            accountRole.setSource(
                                    RoleConstants.SOURCE_REGISTRATION
                            );
                            accountRoleRepository.save(accountRole);

                            log.debug(
                                    "Role [{}] assigned to admin [accountId={}]",
                                    roleName,
                                    accountId
                            );
                        },
                        () -> log.warn(
                                "Role [{}] not found — skipping assignment",
                                roleName
                        )
                );
    }
}
