package com.sofka.banking.accountservice.api.controller;

import com.sofka.banking.accountservice.api.dto.MovementCreateRequest;
import com.sofka.banking.accountservice.api.dto.MovementPatchRequest;
import com.sofka.banking.accountservice.api.dto.MovementResponse;
import com.sofka.banking.accountservice.api.dto.MovementUpdateRequest;
import com.sofka.banking.accountservice.application.MovementService;
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
@RequestMapping("/movimientos")
public class MovementController {

    private final MovementService movementService;

    public MovementController(MovementService movementService) {
        this.movementService = movementService;
    }

    @GetMapping
    public List<MovementResponse> findAll() {
        return movementService.findAll();
    }

    @GetMapping("/{id}")
    public MovementResponse findById(@PathVariable Long id) {
        return movementService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MovementResponse> create(@Valid @RequestBody MovementCreateRequest request) {
        MovementResponse response = movementService.create(request);
        return ResponseEntity.created(URI.create("/movimientos/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    public MovementResponse update(@PathVariable Long id, @Valid @RequestBody MovementUpdateRequest request) {
        return movementService.update(id, request);
    }

    @PatchMapping("/{id}")
    public MovementResponse patch(@PathVariable Long id, @Valid @RequestBody MovementPatchRequest request) {
        return movementService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
