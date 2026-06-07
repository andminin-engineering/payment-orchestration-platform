# payment-orchestration-platform

Plataforma de orquestación de pagos orientada a portfolio profesional para demostrar diseño y ejecución de soluciones fintech con Java 21 y Spring Boot 3.

## Propósito del repositorio

Este repositorio demuestra cómo diseñar un núcleo de pagos con enfoque enterprise:

- Arquitectura Hexagonal para aislar dominio y proveedores externos.
- Integraciones de pago desacopladas mediante adapters.
- Estrategia de idempotencia para evitar cobros duplicados.
- Buenas prácticas de seguridad (OWASP), testing y escalabilidad.

## Stack objetivo

- Java 21
- Spring Boot 3
- Maven
- Spring Web
- Bean Validation
- Spring Data JPA
- PostgreSQL
- Docker y Docker Compose
- JUnit 5 y Mockito

## Alcance por etapas

### Etapa 1: Base documental

- Estructura de documentación.
- C4 (Context, Container, Component).
- ADRs iniciales:
  - ADR-001: Why Hexagonal Architecture
  - ADR-002: Why Payment Provider Adapters
  - ADR-003: Why Idempotency Strategy

### Etapa 2: Aplicación Spring Boot

- Servicio base con arquitectura por puertos y adaptadores.
- API REST de pagos con validaciones.
- Persistencia en PostgreSQL con JPA.

### Etapa 3: Integraciones de proveedores

- `PaymentProvider` interface.
- `MercadoPagoAdapter`.
- `StripeAdapter`.
- `WalletAdapter`.

### Etapa 4: Calidad operativa

- Tests unitarios con JUnit 5 y Mockito.
- Dockerfile y docker-compose.
- Endurecimiento de seguridad y criterios de producción.

## Qué demuestra este artefacto

### Para recruiter técnico

- Capacidad de construir software con criterio arquitectónico.
- Dominio de stack backend moderno en entorno transaccional.
- Pensamiento orientado a calidad, seguridad y mantenibilidad.

### Para arquitecto de software

- Separación de responsabilidades y límites de dominio.
- Justificación de decisiones con ADRs y C4.
- Diseño orientado a reemplazo de proveedores sin afectar el core.

### Para CTO o Engineering Leader

- Enfoque pragmático de entrega incremental.
- Gobernanza técnica y trazabilidad de decisiones.
- Base sólida para escalar equipo, producto y operaciones.

## Estructura inicial

```text
payment-orchestration-platform/
  README.md
  docs/
    c4/
    adrs/
    diagrams/
```

## Estado actual

Repositorio en fase de arranque documental. La siguiente etapa creará ADRs y diagramas C4 para establecer el contrato arquitectónico antes de implementar código de negocio.
