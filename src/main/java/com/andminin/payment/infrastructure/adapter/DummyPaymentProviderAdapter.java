package com.andminin.payment.infrastructure.adapter;

import com.andminin.payment.domain.port.PaymentAuthorizationRequest;
import com.andminin.payment.domain.port.PaymentAuthorizationResponse;
import com.andminin.payment.domain.port.PaymentCaptureResponse;
import com.andminin.payment.domain.port.PaymentProvider;
import com.andminin.payment.domain.port.PaymentProviderException;
import com.andminin.payment.domain.port.PaymentRefundResponse;
import com.andminin.payment.domain.port.PaymentStatusResponse;
import com.andminin.payment.domain.port.ProviderAuthorization;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adaptador Dummy: DummyPaymentProviderAdapter
 * 
 * Implementa PaymentProvider de forma simulada para desarrollo/testing.
 * En producción, será reemplazado por adapters reales (StripeAdapter, MercadoPagoAdapter, WalletAdapter).
 * 
 * Contexto: ADR-002 (Payment Provider Adapters)
 * - El dominio define un contrato genérico (PaymentProvider)
 * - Cada proveedor real (Stripe, MercadoPago) tiene su propio adaptador
 * - Este dummy es para demostración y desarrollo rápido
 */
@Component
public class DummyPaymentProviderAdapter implements PaymentProvider {
    private static final String PROVIDER_NAME = "DUMMY";

    @Override
    public PaymentAuthorizationResponse authorize(ProviderAuthorization auth, PaymentAuthorizationRequest request)
            throws PaymentProviderException {
        // Simular autorización exitosa (90% de casos)
        if (request.getAmount().doubleValue() > 100000) {
            // Denegar montos muy altos en demo
            return PaymentAuthorizationResponse.failure("AMOUNT_EXCEEDED", "Monto excede límite permitido en modo demo");
        }

        // Generar transactionId ficticio
        String providerTxId = "DUMMY_TXN_" + UUID.randomUUID();
        return PaymentAuthorizationResponse.success(providerTxId);
    }

    @Override
    public PaymentCaptureResponse capture(ProviderAuthorization auth, String providerTransactionId)
            throws PaymentProviderException {
        // En dummy, captura siempre es exitosa
        return PaymentCaptureResponse.success();
    }

    @Override
    public PaymentRefundResponse refund(ProviderAuthorization auth, String providerTransactionId)
            throws PaymentProviderException {
        // Generar refund ID ficticio
        String refundId = "DUMMY_REFUND_" + UUID.randomUUID();
        return PaymentRefundResponse.success(refundId);
    }

    @Override
    public PaymentStatusResponse getStatus(ProviderAuthorization auth, String providerTransactionId)
            throws PaymentProviderException {
        // Simular consulta de estado
        return new PaymentStatusResponse("completed", "COMPLETED", true, null);
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
}
