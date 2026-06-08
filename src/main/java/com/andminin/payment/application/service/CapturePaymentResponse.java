package com.andminin.payment.application.service;

/**
 * DTO: Respuesta al capturar un pago.
 */
public class CapturePaymentResponse {
    private final String paymentId;
    private final String status;

    public CapturePaymentResponse(String paymentId, String status) {
        this.paymentId = paymentId;
        this.status = status;
    }

    public String getPaymentId() { return paymentId; }
    public String getStatus() { return status; }
}
