# Índice Maestro de ADRs

Este directorio consolida decisiones arquitectónicas clave de la plataforma de orquestación de pagos.

## Objetivo de estas decisiones

Definir un marco de diseño estable para un sistema fintech transaccional, con foco en:
- Alta disponibilidad.
- Integraciones externas desacopladas.
- Integridad de operaciones monetarias.
- Evolución segura del producto.

## ADRs

1. [ADR-001: Adopción de Arquitectura Hexagonal](ADR-001-hexagonal-architecture.md)
2. [ADR-002: Uso de Payment Provider Adapters](ADR-002-payment-provider-adapters.md)
3. [ADR-003: Estrategia de Idempotencia](ADR-003-idempotency-strategy.md)

## Relación entre decisiones

Secuencia lógica de arquitectura:

1. ADR-001 define el marco estructural (puertos y adaptadores).
2. ADR-002 aplica ese marco al problema de integraciones de pago multi-provider.
3. ADR-003 añade el control de integridad transaccional para evitar duplicidades bajo retry y concurrencia.

Interpretación conjunta:
- ADR-001 responde al cómo estructurar el sistema para evolucionar.
- ADR-002 responde al cómo integrar proveedores sin acoplar el dominio.
- ADR-003 responde al cómo proteger operaciones monetarias en producción.

## Trazabilidad hacia implementación

Estas decisiones guían la etapa de implementación técnica:
- Diseño de interfaces de dominio y contratos de integración.
- Implementación de adaptadores por proveedor.
- Persistencia y validación de claves idempotentes.
- Estrategias de testing por capa (dominio, adaptadores, API).

## Gobierno de cambios

- Toda modificación relevante de arquitectura debe crear un nuevo ADR o marcar estado superseded.
- Las decisiones deben revisarse con impacto en resiliencia, seguridad, compliance y operabilidad.
- Ningún cambio de integración crítica debe romper trazabilidad con este índice.