package com.sofka.banking.accountservice.api.dto;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        String accountType,
        BigDecimal initialBalance,
        BigDecimal availableBalance,
        Boolean status
) {
}
