package com.andminin.payment.infrastructure.persistence;

import com.andminin.payment.domain.model.Money;
import com.andminin.payment.domain.model.Payment;
import com.andminin.payment.domain.model.PaymentId;
import com.andminin.payment.domain.port.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador: PaymentRepositoryAdapter
 * 
 * Implementa el puerto PaymentRepository (definido en el dominio)
 * usando Spring Data JPA.
 * 
 * Contexto: ADR-001 (Hexagonal Architecture)
 * - El dominio define QUÉ necesita (PaymentRepository)
 * - El adaptador define CÓMO lo hace (con PostgreSQL + JPA)
 * - Si cambias de BD, solo cambias este adaptador, no el dominio
 */
@Component
public class PaymentRepositoryAdapter implements PaymentRepository {
    private final PaymentJpaRepository jpaRepository;

    public PaymentRepositoryAdapter(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Payment payment) {
        PaymentEntity entity = toDomainToPersistence(payment);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Payment> findById(PaymentId paymentId) {
        return jpaRepository.findById(paymentId.getId())
            .map(this::toPersistenceToDomain);
    }

    @Override
    public Optional<Payment> findByIdempotencyKey(String idempotencyKey) {
        return jpaRepository.findByIdempotencyKey(idempotencyKey)
            .map(this::toPersistenceToDomain);
    }

    // ========== Mappers: Domain ↔ Persistence ==========

    /**
     * Convierte agregado de dominio a entidad JPA.
     */
    private PaymentEntity toDomainToPersistence(Payment payment) {
        return new PaymentEntity(
            payment.getId().getId(),
            payment.getAmount().getAmount(),
            payment.getAmount().getCurrency(),
            payment.getCustomerEmail(),
            payment.getPaymentProvider(),
            payment.getIdempotencyKey(),
            payment.getStatus(),
            payment.getProviderTransactionId(),
            payment.getFailureReason(),
            payment.getCreatedAt(),
            payment.getUpdatedAt()
        );
    }

    /**
     * Convierte entidad JPA a agregado de dominio.
     */
    private Payment toPersistenceToDomain(PaymentEntity entity) {
        Money money = new Money(entity.getAmount(), entity.getCurrency());

        return Payment.rehydrate(
            new PaymentId(entity.getId()),
            money,
            entity.getCustomerEmail(),
            entity.getPaymentProvider(),
            entity.getIdempotencyKey(),
            entity.getStatus(),
            entity.getProviderTransactionId(),
            entity.getFailureReason(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
