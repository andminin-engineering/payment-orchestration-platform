package com.andminin.payment.domain.port;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO: PaymentAuthorizationRequest
 * 
 * Modelo canónico de solicitud de autorización de pago.
 * Adaptadores traducen de aquí a APIs específicas de proveedores.
 * 
 * Contexto: ADR-002 - modelo canónico aísla dominio de volatilidad de APIs externas.
 */
public class PaymentAuthorizationRequest {
    private final String paymentId;
    private final BigDecimal amount;
    private final String currency;
    private final String customerEmail;
    private final String cardToken;
    private final String idempotencyKey;
    private final Map<String, String> metadata;

    public PaymentAuthorizationRequest(Builder builder) {
        this.paymentId = builder.paymentId;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.customerEmail = builder.customerEmail;
        this.cardToken = builder.cardToken;
        this.idempotencyKey = builder.idempotencyKey;
        this.metadata = builder.metadata;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPaymentId() { return paymentId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCardToken() { return cardToken; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public Map<String, String> getMetadata() { return metadata; }

    public static class Builder {
        private String paymentId;
        private BigDecimal amount;
        private String currency;
        private String customerEmail;
        private String cardToken;
        private String idempotencyKey;
        private Map<String, String> metadata = Map.of();

        public Builder paymentId(String paymentId) { this.paymentId = paymentId; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder currency(String currency) { this.currency = currency; return this; }
        public Builder customerEmail(String customerEmail) { this.customerEmail = customerEmail; return this; }
        public Builder cardToken(String cardToken) { this.cardToken = cardToken; return this; }
        public Builder idempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; return this; }
        public Builder metadata(Map<String, String> metadata) { this.metadata = metadata; return this; }

        public PaymentAuthorizationRequest build() {
            return new PaymentAuthorizationRequest(this);
        }
    }
}
