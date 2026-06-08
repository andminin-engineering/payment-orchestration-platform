package com.andminin.payment.domain.port;

/**
 * DTO: PaymentStatusResponse
 * 
 * Respuesta canónica de consulta de estado del pago.
 */
public class PaymentStatusResponse {
    private final String providerStatus;
    private final String canonicalStatus;
    private final boolean success;
    private final String errorCode;

    public PaymentStatusResponse(String providerStatus, String canonicalStatus, boolean success, String errorCode) {
        this.providerStatus = providerStatus;
        this.canonicalStatus = canonicalStatus;
        this.success = success;
        this.errorCode = errorCode;
    }

    public String getProviderStatus() { return providerStatus; }
    public String getCanonicalStatus() { return canonicalStatus; }
    public boolean isSuccess() { return success; }
    public String getErrorCode() { return errorCode; }
}
