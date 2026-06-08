package com.andminin.payment.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: PaymentId
 * 
 * Identificador único del agregado Payment.
 * Inmutable y garantiza unicidad global.
 * 
 * Contexto: ADR-003 (Idempotency) usa PaymentId para deduplicación.
 */
public final class PaymentId {
    private final String id;

    /**
     * Crea un nuevo PaymentId único.
     */
    public PaymentId() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Crea un PaymentId a partir de un string existente.
     * Útil para recuperación de datos.
     */
    public PaymentId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("PaymentId no puede estar vacío");
        }
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentId paymentId = (PaymentId) o;
        return Objects.equals(id, paymentId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}
