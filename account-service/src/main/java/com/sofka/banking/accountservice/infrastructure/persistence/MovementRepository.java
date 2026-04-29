package com.sofka.banking.accountservice.infrastructure.persistence;

import com.sofka.banking.accountservice.domain.model.Movement;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementRepository extends JpaRepository<Movement, Long> {

    List<Movement> findByAccountIdOrderByMovementDateAscIdAsc(Long accountId);

    List<Movement> findByAccountCustomerIdAndMovementDateGreaterThanEqualAndMovementDateLessThanOrderByMovementDateDescIdDesc(
            String customerId,
            LocalDateTime startInclusive,
            LocalDateTime endExclusive
    );
}
