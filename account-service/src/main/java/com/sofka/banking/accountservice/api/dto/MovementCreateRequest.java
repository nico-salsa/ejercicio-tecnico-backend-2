package com.sofka.banking.accountservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovementCreateRequest(
        @NotNull LocalDateTime movementDate,
        @NotBlank @Size(max = 40) String movementType,
        @NotNull BigDecimal amount,
        @NotNull BigDecimal balance,
        @NotNull Long accountId
) {
}
