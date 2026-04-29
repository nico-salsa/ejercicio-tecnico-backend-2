package com.sofka.banking.customerservice.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CustomerPatchRequest(
        @Size(max = 120) String name,
        @Size(max = 20) String gender,
        @Min(0) Integer age,
        @Size(max = 50) String identification,
        @Size(max = 180) String address,
        @Size(max = 30) String phone,
        @Size(max = 50) String customerId,
        @Size(max = 120) String password,
        Boolean status
) {
}
