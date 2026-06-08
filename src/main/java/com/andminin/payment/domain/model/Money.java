package com.andminin.payment.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object: Money
 * 
 * Encapsula la lógica de dinero (monto + moneda).
 * Inmutable y con validaciones de negocio.
 * 
 * Justificación: Los value objects protegen invariantes del dominio
 * (ej: no permitir montos negativos, moneda válida).
 */
public final class Money {
    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Moneda es requerida");
        }
        this.amount = amount;
        this.currency = currency.toUpperCase();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) &&
               Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
