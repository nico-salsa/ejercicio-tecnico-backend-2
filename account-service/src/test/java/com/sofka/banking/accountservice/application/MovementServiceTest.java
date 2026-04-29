package com.sofka.banking.accountservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sofka.banking.accountservice.api.dto.MovementCreateRequest;
import com.sofka.banking.accountservice.api.dto.MovementPatchRequest;
import com.sofka.banking.accountservice.api.dto.MovementUpdateRequest;
import com.sofka.banking.accountservice.domain.exception.ResourceNotFoundException;
import com.sofka.banking.accountservice.domain.model.Account;
import com.sofka.banking.accountservice.domain.model.Movement;
import com.sofka.banking.accountservice.infrastructure.persistence.MovementRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private MovementService movementService;

    private Account account;
    private Movement movement;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setAccountNumber("478758");
        account.setAccountType("Ahorro");
        account.setInitialBalance(new BigDecimal("2000.00"));
        account.setStatus(true);

        movement = new Movement();
        movement.setMovementDate(LocalDateTime.now());
        movement.setMovementType("DEPOSITO");
        movement.setAmount(new BigDecimal("500.00"));
        movement.setBalance(new BigDecimal("2500.00"));
        movement.setAccount(account);
    }

    @Test
    void shouldCreateMovement() {
        MovementCreateRequest request = new MovementCreateRequest(
                LocalDateTime.of(2026, 4, 28, 10, 0),
                "DEPOSITO",
                new BigDecimal("500.00"),
                new BigDecimal("2500.00"),
                1L
        );

        when(accountService.loadAccount(1L)).thenReturn(account);
        when(movementRepository.save(any(Movement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = movementService.create(request);

        assertThat(response.accountNumber()).isEqualTo("478758");
        assertThat(response.amount()).isEqualByComparingTo("500.00");
    }

    @Test
    void shouldUpdateMovement() {
        when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));
        when(accountService.loadAccount(1L)).thenReturn(account);
        when(movementRepository.save(any(Movement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = movementService.update(1L, new MovementUpdateRequest(
                LocalDateTime.of(2026, 4, 29, 12, 0),
                "RETIRO",
                new BigDecimal("-200.00"),
                new BigDecimal("2300.00"),
                1L
        ));

        assertThat(response.movementType()).isEqualTo("RETIRO");
        assertThat(response.amount()).isEqualByComparingTo("-200.00");
    }

    @Test
    void shouldPatchMovement() {
        Account anotherAccount = new Account();
        ReflectionTestUtils.setField(anotherAccount, "id", 2L);
        anotherAccount.setAccountNumber("225487");
        anotherAccount.setAccountType("Corriente");
        anotherAccount.setInitialBalance(new BigDecimal("100.00"));
        anotherAccount.setStatus(true);

        when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));
        when(accountService.loadAccount(2L)).thenReturn(anotherAccount);
        when(movementRepository.save(any(Movement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = movementService.patch(1L, new MovementPatchRequest(
                null,
                "AJUSTE",
                null,
                new BigDecimal("2400.00"),
                2L
        ));

        assertThat(response.movementType()).isEqualTo("AJUSTE");
        assertThat(response.balance()).isEqualByComparingTo("2400.00");
        assertThat(response.accountId()).isEqualTo(2L);
        assertThat(response.accountNumber()).isEqualTo("225487");
    }

    @Test
    void shouldDeleteMovement() {
        when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));

        movementService.delete(1L);

        verify(movementRepository).delete(movement);
    }

    @Test
    void shouldReturnAllMovements() {
        when(movementRepository.findAll()).thenReturn(java.util.List.of(movement));

        var response = movementService.findAll();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).movementType()).isEqualTo("DEPOSITO");
    }

    @Test
    void shouldThrowWhenMovementNotFound() {
        when(movementRepository.findById(55L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movementService.findById(55L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Movimiento no encontrado");
    }
}
