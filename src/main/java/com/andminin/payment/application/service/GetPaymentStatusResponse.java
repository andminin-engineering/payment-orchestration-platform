package com.andminin.payment.application.service;

/**
 * DTO: Respuesta al consultar estado de un pago.
 */
public class GetPaymentStatusResponse {
    private final String paymentId;
    private final String status;
    private final String amount;
    private final String providerTransactionId;

    public GetPaymentStatusResponse(String paymentId, String status, String amount, String providerTransactionId) {
        this.paymentId = paymentId;
        this.status = status;
        this.amount = amount;
        this.providerTransactionId = providerTransactionId;
    }

    public String getPaymentId() { return paymentId; }
    public String getStatus() { return status; }
    public String getAmount() { return amount; }
    public String getProviderTransactionId() { return providerTransactionId; }
}
