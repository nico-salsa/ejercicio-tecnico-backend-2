package com.sofka.banking.accountservice.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AccountPatchRequest(
        @Size(max = 50) String accountNumber,
        @Size(max = 40) String accountType,
        @DecimalMin("0.00") BigDecimal initialBalance,
        Boolean status
) {
}
