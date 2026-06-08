package com.andminin.payment.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test: PaymentAggregateTest
 * 
 * Valida el comportamiento del agregado Payment:
 * - Creación
 * - Transiciones de estado
 * - Validaciones de negocio
 */
@DisplayName("Payment Aggregate Tests")
class PaymentAggregateTest {

    @Test
    @DisplayName("Debe crear un pago en estado PENDING")
    void testCreatePayment() {
        Money amount = new Money(new BigDecimal("100.00"), "USD");
        
        Payment payment = Payment.builder()
            .amount(amount)
            .customerEmail("customer@example.com")
            .paymentProvider("STRIPE")
            .idempotencyKey("idem-key-123")
            .build();

        assertEquals("PENDING", payment.getStatus().name());
        assertEquals("100.00 USD", payment.getAmount().toString());
        assertNull(payment.getProviderTransactionId());
    }

    @Test
    @DisplayName("Debe autorizar un pago (PENDING → AUTHORIZED)")
    void testAuthorizePayment() {
        Money amount = new Money(new BigDecimal("100.00"), "USD");
        Payment payment = Payment.builder()
            .amount(amount)
            .customerEmail("customer@example.com")
            .paymentProvider("STRIPE")
            .idempotencyKey("idem-key-123")
            .build();

        payment.authorize("txn-stripe-123");

        assertEquals("AUTHORIZED", payment.getStatus().name());
        assertEquals("txn-stripe-123", payment.getProviderTransactionId());
    }

    @Test
    @DisplayName("Debe rechazar autorización si no está en PENDING")
    void testCannotAuthorizeIfNotPending() {
        Money amount = new Money(new BigDecimal("100.00"), "USD");
        Payment payment = Payment.builder()
            .amount(amount)
            .customerEmail("customer@example.com")
            .paymentProvider("STRIPE")
            .idempotencyKey("idem-key-123")
            .build();

        payment.authorize("txn-stripe-123");
        
        // Intenta autorizar de nuevo → debe fallar
        assertThrows(IllegalStateException.class, () -> {
            payment.authorize("txn-stripe-456");
        });
    }

    @Test
    @DisplayName("Debe declinar un pago en PENDING")
    void testDeclinePayment() {
        Money amount = new Money(new BigDecimal("100.00"), "USD");
        Payment payment = Payment.builder()
            .amount(amount)
            .customerEmail("customer@example.com")
            .paymentProvider("STRIPE")
            .idempotencyKey("idem-key-123")
            .build();

        payment.decline("Insufficient funds");

        assertEquals("DECLINED", payment.getStatus().name());
        assertEquals("Insufficient funds", payment.getFailureReason());
    }

    @Test
    @DisplayName("Debe validar que amount sea positivo")
    void testMoneyMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Money(new BigDecimal("-50.00"), "USD");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Money(BigDecimal.ZERO, "USD");
        });
    }

    @Test
    @DisplayName("Debe generar eventos de dominio en transiciones")
    void testDomainEventsGeneration() {
        Money amount = new Money(new BigDecimal("100.00"), "USD");
        Payment payment = Payment.builder()
            .amount(amount)
            .customerEmail("customer@example.com")
            .paymentProvider("STRIPE")
            .idempotencyKey("idem-key-123")
            .build();

        payment.authorize("txn-stripe-123");
        
        var events = payment.pullDomainEvents();
        
        assertEquals(1, events.size());
        assertEquals("PaymentAuthorized", events.get(0).getEventType());
    }
}
