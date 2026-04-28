package com.sofka.banking.customerservice.api.dto;

public record CustomerResponse(
        Long id,
        String name,
        String gender,
        Integer age,
        String identification,
        String address,
        String phone,
        String customerId,
        Boolean status
) {
}
