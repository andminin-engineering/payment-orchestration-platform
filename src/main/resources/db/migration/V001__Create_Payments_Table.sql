-- Migration: V001__Create_Payments_Table
-- Descripción: Tabla base para el agregado Payment
-- Contexto: ADR-003 (Idempotency) - índice único en idempotency_key previene duplicados

CREATE TABLE payments (
    id VARCHAR(36) PRIMARY KEY,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    payment_provider VARCHAR(50) NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    provider_transaction_id VARCHAR(255),
    failure_reason TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Índices para optimizar queries
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_created_at ON payments(created_at);
CREATE INDEX idx_payments_customer_email ON payments(customer_email);
CREATE INDEX idx_payments_provider_tx_id ON payments(provider_transaction_id);

-- Comentarios para documentación
COMMENT ON TABLE payments IS 'Agregado Payment - transacciones de pago orquestadas';
COMMENT ON COLUMN payments.idempotency_key IS 'Clave de idempotencia para deduplicar requests (ADR-003)';
COMMENT ON COLUMN payments.status IS 'Estado del pago: PENDING, AUTHORIZED, CAPTURED, COMPLETED, DECLINED, FAILED, REFUNDED';
COMMENT ON COLUMN payments.provider_transaction_id IS 'ID de transacción del proveedor externo (Stripe, MercadoPago, etc.)';
