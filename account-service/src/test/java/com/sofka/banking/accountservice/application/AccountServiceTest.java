package com.sofka.banking.accountservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sofka.banking.accountservice.api.dto.AccountCreateRequest;
import com.sofka.banking.accountservice.api.dto.AccountPatchRequest;
import com.sofka.banking.accountservice.api.dto.AccountUpdateRequest;
import com.sofka.banking.accountservice.domain.exception.ResourceNotFoundException;
import com.sofka.banking.accountservice.domain.model.Account;
import com.sofka.banking.accountservice.infrastructure.persistence.AccountRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setAccountNumber("478758");
        account.setAccountType("Ahorro");
        account.setInitialBalance(new BigDecimal("2000.00"));
        account.setStatus(true);
    }

    @Test
    void shouldCreateAccount() {
        AccountCreateRequest request = new AccountCreateRequest(
                "478758",
                "Ahorro",
                new BigDecimal("2000.00"),
                true
        );

        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = accountService.create(request);

        assertThat(response.accountNumber()).isEqualTo("478758");
        assertThat(response.initialBalance()).isEqualByComparingTo("2000.00");
    }

    @Test
    void shouldPatchAccountStatus() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = accountService.patch(1L, new AccountPatchRequest(
                null,
                null,
                null,
                false
        ));

        assertThat(response.status()).isFalse();
    }

    @Test
    void shouldUpdateAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = accountService.update(1L, new AccountUpdateRequest(
                "585545",
                "Corriente",
                new BigDecimal("1000.00"),
                true
        ));

        assertThat(response.accountNumber()).isEqualTo("585545");
        assertThat(response.accountType()).isEqualTo("Corriente");
        assertThat(response.initialBalance()).isEqualByComparingTo("1000.00");
    }

    @Test
    void shouldDeleteAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.delete(1L);

        verify(accountRepository).delete(account);
    }

    @Test
    void shouldReturnAllAccounts() {
        when(accountRepository.findAll()).thenReturn(java.util.List.of(account));

        var response = accountService.findAll();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).accountNumber()).isEqualTo("478758");
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cuenta no encontrada");
    }
}
