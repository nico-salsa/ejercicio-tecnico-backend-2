package com.sofka.banking.customerservice.api.controller;

import com.sofka.banking.customerservice.api.dto.CustomerCreateRequest;
import com.sofka.banking.customerservice.api.dto.CustomerPatchRequest;
import com.sofka.banking.customerservice.api.dto.CustomerResponse;
import com.sofka.banking.customerservice.api.dto.CustomerUpdateRequest;
import com.sofka.banking.customerservice.application.CustomerService;
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
@RequestMapping("/clientes")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerResponse> findAll() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    public CustomerResponse findById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerCreateRequest request) {
        CustomerResponse response = customerService.create(request);
        return ResponseEntity.created(URI.create("/clientes/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateRequest request) {
        return customerService.update(id, request);
    }

    @PatchMapping("/{id}")
    public CustomerResponse patch(@PathVariable Long id, @Valid @RequestBody CustomerPatchRequest request) {
        return customerService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
