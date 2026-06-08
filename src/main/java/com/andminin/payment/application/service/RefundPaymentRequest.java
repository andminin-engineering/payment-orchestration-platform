package com.andminin.payment.application.service;

/**
 * DTO: Solicitud para reembolsar un pago.
 */
public class RefundPaymentRequest {
    private final String paymentId;

    public RefundPaymentRequest(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentId() { return paymentId; }
}
