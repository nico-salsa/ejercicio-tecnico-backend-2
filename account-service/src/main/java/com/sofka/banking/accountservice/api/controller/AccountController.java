package com.sofka.banking.accountservice.api.controller;

import com.sofka.banking.accountservice.api.dto.AccountCreateRequest;
import com.sofka.banking.accountservice.api.dto.AccountPatchRequest;
import com.sofka.banking.accountservice.api.dto.AccountResponse;
import com.sofka.banking.accountservice.api.dto.AccountUpdateRequest;
import com.sofka.banking.accountservice.application.AccountService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cuentas")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResponse> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public AccountResponse findById(@PathVariable Long id) {
        return accountService.findById(id);
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountCreateRequest request) {
        AccountResponse response = accountService.create(request);
        return ResponseEntity.created(URI.create("/cuentas/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable Long id, @Valid @RequestBody AccountUpdateRequest request) {
        return accountService.update(id, request);
    }

    @PatchMapping("/{id}")
    public AccountResponse patch(@PathVariable Long id, @Valid @RequestBody AccountPatchRequest request) {
        return accountService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
