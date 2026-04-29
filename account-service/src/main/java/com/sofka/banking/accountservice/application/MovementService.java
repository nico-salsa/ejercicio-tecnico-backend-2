package com.sofka.banking.accountservice.application;

import com.sofka.banking.accountservice.api.dto.MovementCreateRequest;
import com.sofka.banking.accountservice.api.dto.MovementPatchRequest;
import com.sofka.banking.accountservice.api.dto.MovementResponse;
import com.sofka.banking.accountservice.api.dto.MovementUpdateRequest;
import com.sofka.banking.accountservice.domain.exception.InsufficientBalanceException;
import com.sofka.banking.accountservice.domain.exception.ResourceNotFoundException;
import com.sofka.banking.accountservice.domain.model.Account;
import com.sofka.banking.accountservice.domain.model.Movement;
import com.sofka.banking.accountservice.infrastructure.persistence.MovementRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MovementService {

    private final MovementRepository movementRepository;
    private final AccountService accountService;

    public MovementService(MovementRepository movementRepository, AccountService accountService) {
        this.movementRepository = movementRepository;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public List<MovementResponse> findAll() {
        return movementRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovementResponse findById(Long id) {
        return toResponse(loadMovement(id));
    }

    public MovementResponse create(MovementCreateRequest request) {
        Movement movement = new Movement();
        applyCreate(movement, request);
        ensureNoNegativeBalance(null, movement, movement.getAccount());
        Movement savedMovement = movementRepository.save(movement);
        recalculateAccountBalances(savedMovement.getAccount());
        return toResponse(savedMovement);
    }

    public MovementResponse update(Long id, MovementUpdateRequest request) {
        Movement movement = loadMovement(id);
        Account originalAccount = movement.getAccount();
        applyUpdate(movement, request);
        ensureNoNegativeBalance(originalAccount, movement, movement.getAccount());
        Movement savedMovement = movementRepository.save(movement);
        recalculateAffectedAccounts(originalAccount, savedMovement.getAccount());
        return toResponse(savedMovement);
    }

    public MovementResponse patch(Long id, MovementPatchRequest request) {
        Movement movement = loadMovement(id);
        Account originalAccount = movement.getAccount();

        if (request.movementDate() != null) {
            movement.setMovementDate(request.movementDate());
        }
        if (request.movementType() != null) {
            movement.setMovementType(request.movementType());
        }
        if (request.amount() != null) {
            movement.setAmount(request.amount());
        }
        if (request.accountId() != null) {
            movement.setAccount(accountService.loadAccount(request.accountId()));
        }

        movement.setMovementType(resolveMovementType(movement.getMovementType(), movement.getAmount()));
        ensureNoNegativeBalance(originalAccount, movement, movement.getAccount());
        Movement savedMovement = movementRepository.save(movement);
        recalculateAffectedAccounts(originalAccount, savedMovement.getAccount());
        return toResponse(savedMovement);
    }

    public void delete(Long id) {
        Movement movement = loadMovement(id);
        Account account = movement.getAccount();
        movementRepository.delete(movement);
        recalculateAccountBalances(account);
    }

    private Movement loadMovement(Long id) {
        return movementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id " + id));
    }

    private void applyCreate(Movement movement, MovementCreateRequest request) {
        Account account = accountService.loadAccount(request.accountId());
        movement.setMovementDate(request.movementDate());
        movement.setAmount(request.amount());
        movement.setMovementType(resolveMovementType(request.movementType(), request.amount()));
        movement.setBalance(BigDecimal.ZERO);
        movement.setAccount(account);
    }

    private void applyUpdate(Movement movement, MovementUpdateRequest request) {
        Account account = accountService.loadAccount(request.accountId());
        movement.setMovementDate(request.movementDate());
        movement.setAmount(request.amount());
        movement.setMovementType(resolveMovementType(request.movementType(), request.amount()));
        movement.setAccount(account);
    }

    private MovementResponse toResponse(Movement movement) {
        return new MovementResponse(
                movement.getId(),
                movement.getMovementDate(),
                movement.getMovementType(),
                movement.getAmount(),
                movement.getBalance(),
                movement.getAccount().getId(),
                movement.getAccount().getAccountNumber()
        );
    }

    private void recalculateAffectedAccounts(Account originalAccount, Account updatedAccount) {
        recalculateAccountBalances(updatedAccount);

        if (!originalAccount.getId().equals(updatedAccount.getId())) {
            recalculateAccountBalances(originalAccount);
        }
    }

    private void recalculateAccountBalances(Account accountReference) {
        Account account = accountService.loadAccount(accountReference.getId());
        List<Movement> movements = movementRepository.findByAccountIdOrderByMovementDateAscIdAsc(account.getId());
        BigDecimal runningBalance = account.getInitialBalance();

        for (Movement movement : movements) {
            movement.setMovementType(resolveMovementType(movement.getMovementType(), movement.getAmount()));
            runningBalance = runningBalance.add(movement.getAmount());
            movement.setBalance(runningBalance);
        }

        movementRepository.saveAll(movements);
        account.setAvailableBalance(runningBalance);
        accountService.persist(account);
    }

    private void ensureNoNegativeBalance(Account originalAccount, Movement candidateMovement, Account updatedAccount) {
        validateAccountMovements(updatedAccount, candidateMovements(updatedAccount, candidateMovement));

        if (originalAccount != null && !originalAccount.getId().equals(updatedAccount.getId())) {
            validateAccountMovements(originalAccount, candidateMovements(originalAccount, candidateMovement));
        }
    }

    private List<Movement> candidateMovements(Account account, Movement candidateMovement) {
        List<Movement> movements = new ArrayList<>(movementRepository.findByAccountIdOrderByMovementDateAscIdAsc(account.getId()));
        movements.removeIf(existing -> sameMovement(existing, candidateMovement));

        if (account.getId().equals(candidateMovement.getAccount().getId())) {
            movements.add(candidateMovement);
        }

        movements.sort(Comparator
                .comparing(Movement::getMovementDate)
                .thenComparing(Movement::getId, Comparator.nullsLast(Long::compareTo)));
        return movements;
    }

    private void validateAccountMovements(Account account, List<Movement> movements) {
        BigDecimal runningBalance = account.getInitialBalance();

        for (Movement movement : movements) {
            runningBalance = runningBalance.add(movement.getAmount());
            if (runningBalance.signum() < 0) {
                throw new InsufficientBalanceException();
            }
        }
    }

    private boolean sameMovement(Movement existing, Movement candidate) {
        return existing.getId() != null && existing.getId().equals(candidate.getId());
    }

    private String resolveMovementType(String requestedType, BigDecimal amount) {
        if (amount.signum() > 0) {
            return "DEPOSITO";
        }
        if (amount.signum() < 0) {
            return "RETIRO";
        }
        if (requestedType == null || requestedType.isBlank()) {
            return "AJUSTE";
        }
        return requestedType.trim().toUpperCase();
    }
}
