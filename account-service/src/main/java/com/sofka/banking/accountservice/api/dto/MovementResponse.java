package com.sofka.banking.accountservice.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovementResponse(
        Long id,
        LocalDateTime movementDate,
        String movementType,
        BigDecimal amount,
        BigDecimal balance,
        Long accountId,
        String accountNumber
) {
}
