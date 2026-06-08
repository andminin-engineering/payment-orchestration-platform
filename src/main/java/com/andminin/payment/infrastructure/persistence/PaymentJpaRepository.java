package com.andminin.payment.infrastructure.persistence;

import com.andminin.payment.domain.model.Payment;
import com.andminin.payment.domain.model.PaymentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository Spring Data JPA.
 * 
 * Maneja la persistencia de pagos en PostgreSQL.
 * Marca como @Repository para que Spring lo gestione.
 */
@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, String> {
    /**
     * Busca un pago por idempotency key.
     * Crucial para ADR-003 (Idempotency): deduplicación de requests.
     */
    Optional<PaymentEntity> findByIdempotencyKey(String idempotencyKey);
}
