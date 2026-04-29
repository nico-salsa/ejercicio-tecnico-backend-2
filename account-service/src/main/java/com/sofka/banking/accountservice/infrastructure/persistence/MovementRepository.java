package com.sofka.banking.accountservice.infrastructure.persistence;

import com.sofka.banking.accountservice.domain.model.Movement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementRepository extends JpaRepository<Movement, Long> {

    List<Movement> findByAccountIdOrderByMovementDateAscIdAsc(Long accountId);
}
