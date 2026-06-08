package com.andminin.payment.domain.port;

/**
 * Puerto: PaymentProvider
 * 
 * Define el contrato que deben cumplir todos los adaptadores de proveedores de pago.
 * Permite intercambiar Stripe, MercadoPago, Wallet sin afectar la lógica del dominio.
 * 
 * Contexto: ADR-002 (Payment Provider Adapters) - modelo canónico + adaptadores
 */
public interface PaymentProvider {
    /**
     * Autoriza un pago con el proveedor externo.
     * 
     * @param authorization Datos de autorización (clave pública, cliente ID, etc.)
     * @param request       Solicitud de pago normalizada
     * @return              Respuesta del proveedor con transactionId
     * @throws PaymentProviderException si el proveedor rechaza o hay error de comunicación
     */
    PaymentAuthorizationResponse authorize(ProviderAuthorization authorization, PaymentAuthorizationRequest request)
        throws PaymentProviderException;

    /**
     * Captura (finaliza) una autorización anterior.
     * Algunos proveedores requieren captura explícita (Stripe).
     */
    PaymentCaptureResponse capture(ProviderAuthorization authorization, String providerTransactionId)
        throws PaymentProviderException;

    /**
     * Reembolsa un pago capturado.
     */
    PaymentRefundResponse refund(ProviderAuthorization authorization, String providerTransactionId)
        throws PaymentProviderException;

    /**
     * Consulta el estado de un pago en el proveedor.
     */
    PaymentStatusResponse getStatus(ProviderAuthorization authorization, String providerTransactionId)
        throws PaymentProviderException;

    /**
     * Identifica el nombre del proveedor.
     * Ej: "STRIPE", "MERCADO_PAGO", "WALLET"
     */
    String getProviderName();
}
