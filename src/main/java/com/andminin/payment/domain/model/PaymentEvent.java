package com.andminin.payment.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain Event: PaymentEvent
 * 
 * Representa eventos ocurridos en el ciclo de vida del pago.
 * Usado para auditoría, trazabilidad y notificaciones asincrónicas.
 * 
 * Contexto: ADR-003 (Idempotency) se apoya en eventos para garantizar
 * que mismos eventos no se procesen dos veces.
 */
public final class PaymentEvent {
    private final PaymentId paymentId;
    private final String eventType;
    private final String description;
    private final Instant occurredAt;

    public PaymentEvent(PaymentId paymentId, String eventType, String description, Instant occurredAt) {
        this.paymentId = Objects.requireNonNull(paymentId, "paymentId es requerido");
        this.eventType = Objects.requireNonNull(eventType, "eventType es requerido");
        this.description = Objects.requireNonNull(description, "description es requerida");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt es requerido");
    }

    public PaymentId getPaymentId() {
        return paymentId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDescription() {
        return description;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String toString() {
        return String.format(
            "PaymentEvent{paymentId=%s, type=%s, description=%s, time=%s}",
            paymentId, eventType, description, occurredAt
        );
    }
}
