package dev.ks.authlayerarchitecture.service.account;

import dev.ks.authlayerarchitecture.dto.request.account.UpdateAccountRequest;
import dev.ks.authlayerarchitecture.dto.response.auth.AccountResponse;
import dev.ks.authlayerarchitecture.entity.AccountEntity;
import dev.ks.authlayerarchitecture.exception.account.AccountNotFoundException;
import dev.ks.authlayerarchitecture.mapper.AccountMapper;
import dev.ks.authlayerarchitecture.repository.AccountRepository;
import dev.ks.authlayerarchitecture.repository.AccountRoleRepository;
import dev.ks.authlayerarchitecture.security.principal.AccountPrincipal;
import dev.ks.authlayerarchitecture.service.auth.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final TokenService tokenService;

    public AccountServiceImpl(AccountRepository accountRepository, AccountRoleRepository accountRoleRepository, TokenService tokenService) {
        this.accountRepository = accountRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getProfile(AccountPrincipal principal) {
        AccountEntity account = findAccount(principal.accountId());

        List<String> roles = accountRoleRepository
                .findRoleNamesByAccountId(principal.accountId());

        return AccountMapper.toAccountResponse(account, roles);
    }

    @Override
    @Transactional
    public AccountResponse updateProfile(
            AccountPrincipal principal,
            UpdateAccountRequest request
    ) {
        AccountEntity account = findAccount(principal.accountId());

        account.setFirstName(request.firstName());
        account.setLastName(request.lastName());
        account.setUpdatedAt(Instant.now());

        accountRepository.save(account);

        List<String> roles = accountRoleRepository
                .findRoleNamesByAccountId(principal.accountId());

        log.info("Profile updated [accountId={}]", principal.accountId());

        return AccountMapper.toAccountResponse(account, roles);
    }

    @Override
    @Transactional
    public void anonymize(AccountPrincipal principal) {
        AccountEntity account = findAccount(principal.accountId());

        // Anonymisation RGPD
        account.setEmail("anonymized_" + account.getId() + "@deleted.invalid");
        account.setPasswordHash("anonymized");
        account.setFirstName("Anonymized");
        account.setLastName("Anonymized");
        account.setEmailVerified(false);
        account.setEnabled(false);
        account.setAnonymizedAt(Instant.now());
        account.setUpdatedAt(Instant.now());

        accountRepository.save(account);

        // Révoquer toutes les sessions
        tokenService.revokeAll(principal.accountId());

        log.info("Account anonymized [accountId={}]", principal.accountId());
    }

    private AccountEntity findAccount(UUID accountId) {
        return accountRepository
                .findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
    }
}
