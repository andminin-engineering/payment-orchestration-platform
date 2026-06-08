package com.andminin.payment.domain.port;

import java.util.Objects;

/**
 * DTO: PaymentAuthorizationResponse
 * 
 * Respuesta canónica de autorización de pago.
 * Adaptadores normalizan respuestas de proveedores a este formato.
 */
public class PaymentAuthorizationResponse {
    private final String providerTransactionId;
    private final boolean success;
    private final String errorCode;
    private final String errorMessage;

    public PaymentAuthorizationResponse(String providerTransactionId, boolean success,
                                        String errorCode, String errorMessage) {
        this.providerTransactionId = providerTransactionId;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static PaymentAuthorizationResponse success(String providerTransactionId) {
        return new PaymentAuthorizationResponse(providerTransactionId, true, null, null);
    }

    public static PaymentAuthorizationResponse failure(String errorCode, String errorMessage) {
        return new PaymentAuthorizationResponse(null, false, errorCode, errorMessage);
    }

    public String getProviderTransactionId() { return providerTransactionId; }
    public boolean isSuccess() { return success; }
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }

    @Override
    public String toString() {
        return String.format("PaymentAuthorizationResponse{success=%s, providerTxId=%s, errorCode=%s}",
            success, providerTransactionId, errorCode);
    }
}
