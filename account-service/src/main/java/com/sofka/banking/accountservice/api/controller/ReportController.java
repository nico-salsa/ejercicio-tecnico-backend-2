package com.sofka.banking.accountservice.api.controller;

import com.sofka.banking.accountservice.api.dto.AccountStatementReportRowResponse;
import com.sofka.banking.accountservice.application.AccountService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes")
public class ReportController {

    private final AccountService accountService;

    public ReportController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountStatementReportRowResponse> generateReport(
            @RequestParam String clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        return accountService.generateReport(clienteId, fechaInicio, fechaFin);
    }
}
