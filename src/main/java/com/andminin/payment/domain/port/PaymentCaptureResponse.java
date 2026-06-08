package com.andminin.payment.domain.port;

/**
 * DTO: PaymentCaptureResponse
 * 
 * Respuesta canónica de captura de pago.
 */
public class PaymentCaptureResponse {
    private final boolean success;
    private final String errorCode;
    private final String errorMessage;

    public PaymentCaptureResponse(boolean success, String errorCode, String errorMessage) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static PaymentCaptureResponse success() {
        return new PaymentCaptureResponse(true, null, null);
    }

    public static PaymentCaptureResponse failure(String errorCode, String errorMessage) {
        return new PaymentCaptureResponse(false, errorCode, errorMessage);
    }

    public boolean isSuccess() { return success; }
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }
}
