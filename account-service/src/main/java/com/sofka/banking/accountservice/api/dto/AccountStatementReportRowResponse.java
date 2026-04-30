package com.sofka.banking.accountservice.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountStatementReportRowResponse(
        LocalDate fecha,
        String cliente,
        String numeroCuenta,
        String tipo,
        BigDecimal saldoInicial,
        Boolean estado,
        BigDecimal movimiento,
        BigDecimal saldoDisponible
) {
}
