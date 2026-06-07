# ADR-003: Estrategia de Idempotencia para Operaciones de Pago

# Status
Accepted

# Context
En plataformas transaccionales de pagos, reintentos por timeouts, duplicación de mensajes o retransmisión de clientes pueden generar cobros duplicados.

En dominios monetarios, la duplicidad de transacciones tiene impacto financiero, reputacional y regulatorio, por lo que la idempotencia es un control crítico de integridad.

Problema que resuelve:
- Prevenir ejecución múltiple de la misma intención de pago.
- Garantizar consistencia frente a reintentos en canales síncronos y asíncronos.
- Mejorar trazabilidad y auditoría de decisiones transaccionales.

# Decision
Adoptar idempotencia basada en clave de operación (Idempotency-Key) + persistencia de resultado canónico con ventana temporal configurable.

Diseño de la estrategia:
- Entrada API exige Idempotency-Key por operación de cobro/autorización.
- Se persiste hash de request normalizado, estado y respuesta canónica.
- Reintentos con misma clave y mismo payload devuelven misma respuesta.
- Reintentos con misma clave y payload distinto generan conflicto controlado.
- TTL y políticas de retención según criticidad regulatoria y reconciliación.

Beneficios:
- Prevención de cobros duplicados en escenarios de retry.
- Consistencia funcional en operaciones críticas.
- Mejor soporte para recovery e incident response.

Riesgos:
- Sobrecarga en persistencia de claves idempotentes.
- Riesgo de colisiones si la clave está mal diseñada.
- Complejidad al sincronizar idempotencia entre canales API y mensajería.

Trade-offs:
- Mayor costo de almacenamiento y validación a cambio de integridad transaccional.
- Mayor latencia marginal por lookup de idempotencia frente a evitar pérdidas económicas.

Cuándo usarlo:
- Todas las operaciones de pago con impacto monetario.
- Flujos con reintentos automáticos y latencia variable.
- Entornos de alta concurrencia con canales distribuidos.

Cuándo no usarlo:
- Operaciones puramente de consulta sin efectos de estado.
- Procesos efímeros no críticos sin consecuencias financieras.

# Consequences
Consecuencias positivas:
- Reducción de incidentes por duplicidad de transacciones.
- Mejor control de auditoría y cumplimiento.
- Respuestas deterministas en reintentos de cliente.

Consecuencias negativas:
- Necesidad de modelar cuidadosamente ventanas de retención.
- Mayor complejidad de observabilidad y troubleshooting.

Impacto operativo esperado:
- Menor número de reversos manuales por doble cobro.
- Mejor estabilidad de KPIs de éxito transaccional.

# Alternatives Considered
1. Idempotencia delegada solo al proveedor externo
- Ventaja: menor complejidad interna.
- Desventaja: dependencia externa y cobertura parcial.

2. Dedupe solo en capa de mensajería
- Ventaja: útil para eventos.
- Desventaja: no cubre API síncrona ni edge retries.

3. Confiar en timeout/retry del cliente sin control de servidor
- Ventaja: simplicidad inicial.
- Desventaja: alto riesgo de cobros duplicados y pérdida reputacional.

# Cómo defender esta decisión en una entrevista
Qué decir:
- En pagos, idempotencia no es optimización, es control de integridad financiera.
- Implementamos clave idempotente con respuesta determinista para proteger al cliente y al negocio.

Preguntas frecuentes:
- Cómo se define la ventana temporal de idempotencia.
- Qué pasa si cambia el payload con la misma clave.
- Dónde se almacena y cómo escala.

Respuestas sugeridas:
- La ventana se define por naturaleza del flujo, tiempos de reconciliación y requerimientos regulatorios.
- Mismo key + payload distinto se trata como conflicto para evitar ambigüedad transaccional.
- Se almacena en persistencia transaccional/rápida según volumen, con índices y métricas de hit/conflict.

Errores comunes al explicarlo:
- Reducirlo a un detalle de API y no a control financiero.
- No contemplar escenarios asíncronos y retries en cascada.
- Omitir política de retención y auditoría.