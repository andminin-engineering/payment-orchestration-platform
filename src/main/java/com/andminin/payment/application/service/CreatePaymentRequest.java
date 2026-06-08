package com.andminin.payment.application.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO: Solicitud para crear un pago.
 */
public class CreatePaymentRequest {
    @NotNull(message = "amount es requerido")
    @DecimalMin(value = "0.01", message = "amount debe ser mayor a 0")
    private BigDecimal amount;

    @NotBlank(message = "currency es requerida")
    private String currency;

    @NotBlank(message = "customerEmail es requerido")
    @Email(message = "customerEmail debe tener formato valido")
    private String customerEmail;

    @NotBlank(message = "cardToken es requerido")
    private String cardToken;

    @NotBlank(message = "idempotencyKey es requerido")
    private String idempotencyKey;

    public CreatePaymentRequest() {
        // Constructor vacio requerido por Jackson.
    }

    public CreatePaymentRequest(Builder builder) {
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.customerEmail = builder.customerEmail;
        this.cardToken = builder.cardToken;
        this.idempotencyKey = builder.idempotencyKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCardToken() { return cardToken; }
    public String getIdempotencyKey() { return idempotencyKey; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public void setCardToken(String cardToken) { this.cardToken = cardToken; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public static class Builder {
        private BigDecimal amount;
        private String currency;
        private String customerEmail;
        private String cardToken;
        private String idempotencyKey;

        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder currency(String currency) { this.currency = currency; return this; }
        public Builder customerEmail(String customerEmail) { this.customerEmail = customerEmail; return this; }
        public Builder cardToken(String cardToken) { this.cardToken = cardToken; return this; }
        public Builder idempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; return this; }

        public CreatePaymentRequest build() {
            return new CreatePaymentRequest(this);
        }
    }
}
