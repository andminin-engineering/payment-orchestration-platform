package com.andminin.payment.domain.port;

import com.andminin.payment.domain.model.Payment;
import com.andminin.payment.domain.model.PaymentId;

import java.util.Optional;

/**
 * Puerto: PaymentRepository
 * 
 * Define el contrato para persistencia de pagos.
 * El dominio NO sabe si usa BD, archivo, o cualquier otro almacenamiento.
 * 
 * Contexto: ADR-001 (Hexagonal) - los adaptadores implementan este puerto.
 */
public interface PaymentRepository {
    /**
     * Persiste un nuevo pago o actualiza uno existente.
     */
    void save(Payment payment);

    /**
     * Recupera un pago por ID.
     */
    Optional<Payment> findById(PaymentId paymentId);

    /**
     * Recupera un pago por Idempotency Key.
     * 
     * Contexto ADR-003: Deduplicación de requests.
     */
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}
