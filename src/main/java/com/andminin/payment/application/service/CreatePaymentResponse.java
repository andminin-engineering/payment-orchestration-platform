package com.andminin.payment.application.service;

/**
 * DTO: Respuesta al crear un pago.
 */
public class CreatePaymentResponse {
    private final String paymentId;
    private final String status;
    private final String providerTransactionId;

    public CreatePaymentResponse(String paymentId, String status, String providerTransactionId) {
        this.paymentId = paymentId;
        this.status = status;
        this.providerTransactionId = providerTransactionId;
    }

    public String getPaymentId() { return paymentId; }
    public String getStatus() { return status; }
    public String getProviderTransactionId() { return providerTransactionId; }
}
