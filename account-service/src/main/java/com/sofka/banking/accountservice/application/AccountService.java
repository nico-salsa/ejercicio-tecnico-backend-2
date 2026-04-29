package com.sofka.banking.accountservice.application;

import com.sofka.banking.accountservice.api.dto.AccountCreateRequest;
import com.sofka.banking.accountservice.api.dto.AccountPatchRequest;
import com.sofka.banking.accountservice.api.dto.AccountResponse;
import com.sofka.banking.accountservice.api.dto.AccountUpdateRequest;
import com.sofka.banking.accountservice.domain.exception.ResourceNotFoundException;
import com.sofka.banking.accountservice.domain.model.Account;
import com.sofka.banking.accountservice.infrastructure.persistence.AccountRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        return accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse findById(Long id) {
        return toResponse(loadAccount(id));
    }

    public AccountResponse create(AccountCreateRequest request) {
        Account account = new Account();
        applyCreate(account, request);
        return toResponse(accountRepository.save(account));
    }

    public AccountResponse update(Long id, AccountUpdateRequest request) {
        Account account = loadAccount(id);
        applyUpdate(account, request);
        return toResponse(accountRepository.save(account));
    }

    public AccountResponse patch(Long id, AccountPatchRequest request) {
        Account account = loadAccount(id);

        if (request.accountNumber() != null) {
            account.setAccountNumber(request.accountNumber());
        }
        if (request.accountType() != null) {
            account.setAccountType(request.accountType());
        }
        if (request.initialBalance() != null) {
            applyInitialBalanceChange(account, request.initialBalance());
        }
        if (request.status() != null) {
            account.setStatus(request.status());
        }

        return toResponse(accountRepository.save(account));
    }

    public void delete(Long id) {
        Account account = loadAccount(id);
        accountRepository.delete(account);
    }

    public Account loadAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id " + id));
    }

    private void applyCreate(Account account, AccountCreateRequest request) {
        account.setAccountNumber(request.accountNumber());
        account.setAccountType(request.accountType());
        account.setInitialBalance(request.initialBalance());
        account.setAvailableBalance(request.initialBalance());
        account.setStatus(request.status());
    }

    private void applyUpdate(Account account, AccountUpdateRequest request) {
        account.setAccountNumber(request.accountNumber());
        account.setAccountType(request.accountType());
        applyInitialBalanceChange(account, request.initialBalance());
        account.setStatus(request.status());
    }

    Account persist(Account account) {
        return accountRepository.save(account);
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getInitialBalance(),
                account.getAvailableBalance(),
                account.getStatus()
        );
    }

    private void applyInitialBalanceChange(Account account, BigDecimal newInitialBalance) {
        if (account.getAvailableBalance() == null || account.getInitialBalance() == null) {
            account.setInitialBalance(newInitialBalance);
            account.setAvailableBalance(newInitialBalance);
            return;
        }

        BigDecimal delta = newInitialBalance.subtract(account.getInitialBalance());
        account.setInitialBalance(newInitialBalance);
        account.setAvailableBalance(account.getAvailableBalance().add(delta));
    }
}
