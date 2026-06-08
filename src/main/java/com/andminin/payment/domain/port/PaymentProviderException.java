package com.andminin.payment.domain.port;

/**
 * Excepción base para errores de proveedores de pago.
 */
public class PaymentProviderException extends Exception {
    private final String errorCode;
    private final String providerName;

    public PaymentProviderException(String message, String errorCode, String providerName) {
        super(message);
        this.errorCode = errorCode;
        this.providerName = providerName;
    }

    public PaymentProviderException(String message, String errorCode, String providerName, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.providerName = providerName;
    }

    public String getErrorCode() { return errorCode; }
    public String getProviderName() { return providerName; }
}
