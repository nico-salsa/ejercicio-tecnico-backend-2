package com.sofka.banking.accountservice.domain.exception;

public class InvalidReportQueryException extends RuntimeException {

    public InvalidReportQueryException(String message) {
        super(message);
    }
}
