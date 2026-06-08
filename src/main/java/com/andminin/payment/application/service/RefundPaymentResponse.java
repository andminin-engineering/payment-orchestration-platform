package com.andminin.payment.application.service;

/**
 * DTO: Respuesta al reembolsar un pago.
 */
public class RefundPaymentResponse {
    private final String paymentId;
    private final String status;
    private final String refundId;

    public RefundPaymentResponse(String paymentId, String status, String refundId) {
        this.paymentId = paymentId;
        this.status = status;
        this.refundId = refundId;
    }

    public String getPaymentId() { return paymentId; }
    public String getStatus() { return status; }
    public String getRefundId() { return refundId; }
}
