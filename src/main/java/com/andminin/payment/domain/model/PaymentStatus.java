package com.andminin.payment.domain.model;

/**
 * Estados posibles de un pago en el ciclo de vida transaccional.
 * 
 * Contexto: ADR-001 (Hexagonal Architecture) aísla esta lógica del framework.
 * ADR-003 (Idempotency) garantiza determinismo en transiciones de estado.
 */
public enum PaymentStatus {
    /**
     * Pago inicialmente recibido, pendiente de autorización.
     */
    PENDING,

    /**
     * Pago autorizado exitosamente por el proveedor.
     */
    AUTHORIZED,

    /**
     * Pago capturado (dinero comprometido).
     */
    CAPTURED,

    /**
     * Pago completado exitosamente.
     */
    COMPLETED,

    /**
     * Pago rechazado por el proveedor o reglas del negocio.
     */
    DECLINED,

    /**
     * Pago cancelado por solicitud del usuario.
     */
    CANCELLED,

    /**
     * Pago reembolsado (reversión parcial o total).
     */
    REFUNDED,

    /**
     * Pago en error temporal (timeout, reintentar después).
     */
    FAILED
}
