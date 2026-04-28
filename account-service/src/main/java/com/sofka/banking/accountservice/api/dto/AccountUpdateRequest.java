package com.sofka.banking.accountservice.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AccountUpdateRequest(
        @NotBlank @Size(max = 50) String accountNumber,
        @NotBlank @Size(max = 40) String accountType,
        @NotNull @DecimalMin("0.00") BigDecimal initialBalance,
        @NotNull Boolean status
) {
}
