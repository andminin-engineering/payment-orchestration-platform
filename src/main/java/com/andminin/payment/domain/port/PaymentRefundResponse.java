package com.andminin.payment.domain.port;

/**
 * DTO: PaymentRefundResponse
 */
public class PaymentRefundResponse {
    private final String refundId;
    private final boolean success;
    private final String errorCode;
    private final String errorMessage;

    public PaymentRefundResponse(String refundId, boolean success, String errorCode, String errorMessage) {
        this.refundId = refundId;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static PaymentRefundResponse success(String refundId) {
        return new PaymentRefundResponse(refundId, true, null, null);
    }

    public static PaymentRefundResponse failure(String errorCode, String errorMessage) {
        return new PaymentRefundResponse(null, false, errorCode, errorMessage);
    }

    public String getRefundId() { return refundId; }
    public boolean isSuccess() { return success; }
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }
}
