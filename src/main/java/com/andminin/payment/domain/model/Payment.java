package com.andminin.payment.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root: Payment
 * 
 * Representa un pago en el sistema. Encapsula toda la lógica de transiciones
 * de estado y validaciones de negocio.
 * 
 * Contexto arquitectónico:
 * - ADR-001: Aislado de infraestructura (no conoce BD, APIs externas)
 * - ADR-003: Garantiza idempotencia: mismo request = mismo resultado
 * - C4 Component: Orquestación de pagos y aplicación de reglas
 * 
 * Responsabilidades:
 * - Mantener invariantes del pago (ej: no cambiar amount)
 * - Registrar eventos de dominio para auditoría y side effects
 * - Validar transiciones de estado
 */
public class Payment {
    private final PaymentId id;
    private final Money amount;
    private final String customerEmail;
    private final String paymentProvider;
    private final String idempotencyKey;
    private PaymentStatus status;
    private String providerTransactionId;
    private String failureReason;
    private Instant createdAt;
    private Instant updatedAt;
    private final List<PaymentEvent> domainEvents = new ArrayList<>();

    /**
     * Constructor privado: uso del builder para construcción.
     */
    private Payment(PaymentBuilder builder) {
        this.id = builder.id != null ? builder.id : new PaymentId();
        this.amount = Objects.requireNonNull(builder.amount, "Amount es requerido");
        this.customerEmail = Objects.requireNonNull(builder.customerEmail, "CustomerEmail es requerido");
        this.paymentProvider = Objects.requireNonNull(builder.paymentProvider, "PaymentProvider es requerido");
        this.idempotencyKey = Objects.requireNonNull(builder.idempotencyKey, "IdempotencyKey es requerido");
        this.status = PaymentStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }

    public static Payment rehydrate(
            PaymentId id,
            Money amount,
            String customerEmail,
            String paymentProvider,
            String idempotencyKey,
            PaymentStatus status,
            String providerTransactionId,
            String failureReason,
            Instant createdAt,
            Instant updatedAt) {
        Payment payment = Payment.builder()
            .id(id)
            .amount(amount)
            .customerEmail(customerEmail)
            .paymentProvider(paymentProvider)
            .idempotencyKey(idempotencyKey)
            .build();

        payment.status = Objects.requireNonNull(status, "status es requerido");
        payment.providerTransactionId = providerTransactionId;
        payment.failureReason = failureReason;
        payment.createdAt = Objects.requireNonNull(createdAt, "createdAt es requerido");
        payment.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt es requerido");
        return payment;
    }

    // ============ Métodos de Transición de Estado ============

    /**
     * Autoriza el pago (transición PENDING → AUTHORIZED).
     * Registra evento de dominio para auditoría.
     */
    public void authorize(String providerTransactionId) {
        if (!this.status.equals(PaymentStatus.PENDING)) {
            throw new IllegalStateException(
                "No se puede autorizar un pago en estado: " + this.status
            );
        }
        if (providerTransactionId == null || providerTransactionId.isBlank()) {
            throw new IllegalArgumentException("providerTransactionId es requerido");
        }

        this.status = PaymentStatus.AUTHORIZED;
        this.providerTransactionId = providerTransactionId;
        this.updatedAt = Instant.now();
        this.domainEvents.add(new PaymentEvent(
            this.id,
            "PaymentAuthorized",
            "Pago autorizado por " + this.paymentProvider,
            Instant.now()
        ));
    }

    /**
     * Captura el pago (transición AUTHORIZED → CAPTURED).
     */
    public void capture() {
        if (!this.status.equals(PaymentStatus.AUTHORIZED)) {
            throw new IllegalStateException(
                "No se puede capturar un pago que no está autorizado. Estado actual: " + this.status
            );
        }

        this.status = PaymentStatus.CAPTURED;
        this.updatedAt = Instant.now();
        this.domainEvents.add(new PaymentEvent(
            this.id,
            "PaymentCaptured",
            "Pago capturado exitosamente",
            Instant.now()
        ));
    }

    /**
     * Completa el pago (transición CAPTURED → COMPLETED).
     */
    public void complete() {
        if (!this.status.equals(PaymentStatus.CAPTURED)) {
            throw new IllegalStateException(
                "No se puede completar un pago que no está capturado. Estado actual: " + this.status
            );
        }

        this.status = PaymentStatus.COMPLETED;
        this.updatedAt = Instant.now();
        this.domainEvents.add(new PaymentEvent(
            this.id,
            "PaymentCompleted",
            "Pago completado exitosamente",
            Instant.now()
        ));
    }

    /**
     * Declina el pago (transición PENDING → DECLINED).
     * Registra razón del rechazo para auditoría.
     */
    public void decline(String reason) {
        if (!this.status.equals(PaymentStatus.PENDING)) {
            throw new IllegalStateException(
                "No se puede declinar un pago en estado: " + this.status
            );
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Razón de declina es requerida");
        }

        this.status = PaymentStatus.DECLINED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
        this.domainEvents.add(new PaymentEvent(
            this.id,
            "PaymentDeclined",
            "Pago rechazado: " + reason,
            Instant.now()
        ));
    }

    /**
     * Marca el pago como fallido (transición PENDING → FAILED).
     * Diferencia: FAILED es recuperable, DECLINED es definitivo.
     */
    public void fail(String reason) {
        if (this.status.equals(PaymentStatus.COMPLETED) || 
            this.status.equals(PaymentStatus.REFUNDED)) {
            throw new IllegalStateException(
                "No se puede marcar como fallido un pago en estado: " + this.status
            );
        }

        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
        this.domainEvents.add(new PaymentEvent(
            this.id,
            "PaymentFailed",
            "Pago falló: " + reason,
            Instant.now()
        ));
    }

    /**
     * Reembolsa el pago (desde COMPLETED → REFUNDED).
     */
    public void refund() {
        if (!this.status.equals(PaymentStatus.COMPLETED)) {
            throw new IllegalStateException(
                "Solo se pueden reembolsar pagos completados. Estado actual: " + this.status
            );
        }

        this.status = PaymentStatus.REFUNDED;
        this.updatedAt = Instant.now();
        this.domainEvents.add(new PaymentEvent(
            this.id,
            "PaymentRefunded",
            "Pago reembolsado",
            Instant.now()
        ));
    }

    // ============ Accesores ============

    public PaymentId getId() {
        return id;
    }

    public Money getAmount() {
        return amount;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getProviderTransactionId() {
        return providerTransactionId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Retorna eventos de dominio y limpia la lista.
     * Patrón: "snapshots" para evitar re-publicar eventos.
     */
    public List<PaymentEvent> pullDomainEvents() {
        List<PaymentEvent> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    // ============ Builder ============

    public static class PaymentBuilder {
        private PaymentId id;
        private Money amount;
        private String customerEmail;
        private String paymentProvider;
        private String idempotencyKey;

        public PaymentBuilder id(PaymentId id) {
            this.id = id;
            return this;
        }

        public PaymentBuilder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        public PaymentBuilder customerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        public PaymentBuilder paymentProvider(String paymentProvider) {
            this.paymentProvider = paymentProvider;
            return this;
        }

        public PaymentBuilder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
