package com.sofka.banking.accountservice.api.dto;

import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovementPatchRequest(
        LocalDateTime movementDate,
        @Size(max = 40) String movementType,
        BigDecimal amount,
        BigDecimal balance,
        Long accountId
) {
}
