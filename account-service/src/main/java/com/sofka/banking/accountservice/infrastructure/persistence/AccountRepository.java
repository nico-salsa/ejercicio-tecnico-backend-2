package com.sofka.banking.accountservice.infrastructure.persistence;

import com.sofka.banking.accountservice.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
