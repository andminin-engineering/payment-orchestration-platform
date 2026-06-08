package com.andminin.payment.application.service;

/**
 * DTO: Solicitud para capturar un pago.
 */
public class CapturePaymentRequest {
    private final String paymentId;

    public CapturePaymentRequest(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentId() { return paymentId; }
}
