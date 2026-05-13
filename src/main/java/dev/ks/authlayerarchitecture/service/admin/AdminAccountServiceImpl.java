package dev.ks.authlayerarchitecture.service.admin;

import dev.ks.authlayerarchitecture.constant.RoleConstants;
import dev.ks.authlayerarchitecture.dto.response.admin.AccountDetailResponse;
import dev.ks.authlayerarchitecture.dto.response.admin.AccountSummaryResponse;
import dev.ks.authlayerarchitecture.dto.response.pagination.PageResponse;
import dev.ks.authlayerarchitecture.entity.AccountEntity;
import dev.ks.authlayerarchitecture.entity.AccountRoleEntity;
import dev.ks.authlayerarchitecture.entity.RoleEntity;
import dev.ks.authlayerarchitecture.exception.account.*;
import dev.ks.authlayerarchitecture.exception.role.*;
import dev.ks.authlayerarchitecture.mapper.AccountMapper;
import dev.ks.authlayerarchitecture.mapper.PageResponseMapper;
import dev.ks.authlayerarchitecture.notification.EmailNotificationService;
import dev.ks.authlayerarchitecture.repository.AccountRepository;
import dev.ks.authlayerarchitecture.repository.AccountRoleRepository;
import dev.ks.authlayerarchitecture.repository.RoleRepository;
import dev.ks.authlayerarchitecture.security.principal.AccountPrincipal;
import dev.ks.authlayerarchitecture.service.auth.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AdminAccountServiceImpl implements AdminAccountService {

    private final AccountRepository     accountRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final RoleRepository        roleRepository;
    private final TokenService          tokenService;
    private final EmailNotificationService emailNotificationService;

    private final String baseUrl;

    public AdminAccountServiceImpl(AccountRepository accountRepository,
                                   AccountRoleRepository accountRoleRepository,
                                   RoleRepository roleRepository,
                                   TokenService tokenService,
                                   EmailNotificationService emailNotificationService,
                                   @Value("${auth-service.base-url}") String baseUrl) {
        this.accountRepository = accountRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.roleRepository = roleRepository;
        this.tokenService = tokenService;
        this.emailNotificationService = emailNotificationService;
        this.baseUrl = baseUrl;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AccountSummaryResponse> findAll(
            String email,
            Boolean enabled,
            Boolean verified,
            Pageable pageable
    ) {
        String emailFilter = (email != null) ? email.trim() : "";

        Page<AccountEntity> page = accountRepository
                .findAllWithFilters(emailFilter, enabled, verified, pageable);

        return PageResponseMapper.from(
                page.map(AccountMapper::toSummary)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDetailResponse findById(UUID accountId) {
        AccountEntity account = findAccount(accountId);

        List<String> roles = accountRoleRepository
                .findRoleNamesByAccountId(accountId);

        return AccountMapper.toDetail(account, roles);
    }

    @Override
    @Transactional
    public void disable(UUID accountId, AccountPrincipal admin) {
        // admin ne peut pas se désactiver
        if (accountId.equals(admin.accountId())) {
            throw new SelfDisableNotAllowedException();
        }

        AccountEntity account = findAccount(accountId);

        // disable uniquement sur ACTIVE
        if (!account.isEmailVerified()) {
            throw new AccountNotActiveException();
        }

        if (!account.isEnabled()) {
            throw new AccountAlreadyDisabledException();
        }

        account.setEnabled(false);
        account.setUpdatedAt(Instant.now());
        accountRepository.save(account);

        // revokeAll
        tokenService.revokeAll(accountId);

        // Notification
        emailNotificationService.sendAccountDisabledEmail(
                account.getEmail(),
                baseUrl + "/support"
        );

        log.info(
                "Account disabled [accountId={}] by [adminId={}]",
                accountId, admin.accountId()
        );
    }

    @Override
    @Transactional
    public void enable(UUID accountId) {
        AccountEntity account = findAccount(accountId);

        if (account.isEnabled()) {
            throw new AccountAlreadyEnabledException();
        }

        account.setEnabled(true);
        account.setUpdatedAt(Instant.now());
        accountRepository.save(account);

        log.info("Account enabled [accountId={}]", accountId);
    }

    @Override
    @Transactional
    public void assignRole(
            UUID accountId,
            UUID roleId,
            AccountPrincipal admin
    ) {
        RoleEntity role = roleRepository
                .findById(roleId)
                .orElseThrow(RoleNotFoundException::new);

        if (accountRoleRepository.existsByAccountIdAndRoleId(
                accountId, roleId
        )) {
            throw new RoleAlreadyAssignedException();
        }

        AccountRoleEntity accountRole = new AccountRoleEntity();
        accountRole.setAccountId(accountId);
        accountRole.setRoleId(roleId);
        accountRole.setAssignedAt(Instant.now());
        accountRole.setAssignedBy(admin.accountId());
        accountRole.setSource(RoleConstants.SOURCE_ADMIN_MANUAL);

        accountRoleRepository.save(accountRole);

        log.info(
                "Role [{}] assigned to [accountId={}] by [adminId={}]",
                role.getName(), accountId, admin.accountId()
        );
    }

    @Override
    @Transactional
    public void revokeRole(UUID accountId, UUID roleId) {
        AccountRoleEntity accountRole = accountRoleRepository
                .findByAccountIdAndRoleId(accountId, roleId)
                .orElseThrow(RoleNotAssignedException::new);

        accountRoleRepository.delete(accountRole);

        log.info(
                "Role [{}] revoked from [accountId={}]",
                roleId, accountId
        );
    }

    private AccountEntity findAccount(UUID accountId) {
        return accountRepository
                .findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
    }
}

