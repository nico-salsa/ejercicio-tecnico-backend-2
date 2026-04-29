package com.sofka.banking.customerservice.infrastructure.persistence;

import com.sofka.banking.customerservice.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
