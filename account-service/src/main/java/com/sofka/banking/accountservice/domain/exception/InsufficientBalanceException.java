package com.sofka.banking.accountservice.domain.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException() {
        super("Saldo no disponible");
    }
}
