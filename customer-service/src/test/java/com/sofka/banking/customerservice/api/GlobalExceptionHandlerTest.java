package com.sofka.banking.customerservice.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.sofka.banking.customerservice.api.error.ApiError;
import com.sofka.banking.customerservice.api.error.GlobalExceptionHandler;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleConstraintViolation() {
        var response = handler.handleConstraintViolation(new ConstraintViolationException("dato invalido", Set.of()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void shouldHandleGenericException() {
        var response = handler.handleGeneric(new IllegalStateException("fallo interno"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(ApiError.class);
        assertThat(response.getBody().message()).isEqualTo("fallo interno");
    }
}
