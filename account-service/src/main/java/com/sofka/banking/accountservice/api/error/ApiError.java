package com.sofka.banking.accountservice.api.error;

import java.time.Instant;

public record ApiError(
        String message,
        String error,
        int status,
        Instant timestamp
) {
}
