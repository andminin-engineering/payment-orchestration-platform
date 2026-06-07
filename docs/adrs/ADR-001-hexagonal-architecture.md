# ADR-001: Adopción de Arquitectura Hexagonal para Orquestación de Pagos

# Status
Accepted

# Context
La plataforma de orquestación de pagos debe integrar múltiples proveedores (por ejemplo, adquirentes, wallets y gateways), sostener cambios frecuentes de negocio y mantener continuidad operativa en un entorno fintech transaccional.

En arquitecturas acopladas a frameworks o SDKs de terceros, los cambios de proveedor impactan el núcleo de negocio, elevan el riesgo de regresiones y ralentizan la entrega. En sistemas de alta disponibilidad, este acoplamiento incrementa el blast radius ante fallas de integración.

Problema que resuelve:
- Evitar que el dominio de pagos dependa de detalles de infraestructura o proveedores externos.
- Permitir reemplazo de integraciones sin reescribir casos de uso críticos.
- Mejorar testabilidad y aislamiento de fallas.

# Decision
Adoptar Arquitectura Hexagonal (Ports and Adapters) como patrón estructural base de la plataforma.

Definición aplicada:
- Núcleo de dominio: reglas de negocio de pagos, validaciones transaccionales e invariantes.
- Puertos de entrada: casos de uso expuestos a API/consumidores internos.
- Puertos de salida: contratos para persistencia, idempotencia, proveedores de pago, publicación de eventos y observabilidad.
- Adaptadores: implementaciones concretas para HTTP, PostgreSQL, Redis, mensajería y SDKs de proveedores.

Beneficios:
- Aislamiento del dominio frente a cambios de proveedor o tecnología.
- Menor costo de evolución en integraciones críticas.
- Mayor cobertura de pruebas unitarias y de contrato.
- Mejor resiliencia organizacional para equipos multi-squad.

Riesgos:
- Curva de aprendizaje para equipos no familiarizados con ports/adapters.
- Riesgo de sobre-abstracción en dominios simples.
- Más artefactos y convenciones a gobernar.

Trade-offs:
- Se incrementa complejidad estructural inicial a cambio de mantenibilidad y desacoplamiento a mediano/largo plazo.
- Se sacrifica velocidad de prototipo rápido en favor de estabilidad evolutiva.

Cuándo usarlo:
- Plataformas fintech con múltiples integraciones externas y cambios frecuentes.
- Sistemas transaccionales con requerimientos de auditabilidad y continuidad.
- Entornos donde se espera reemplazo o coexistencia de proveedores.

Cuándo no usarlo:
- Productos muy pequeños de baja volatilidad y lifecycle corto.
- Equipos sin capacidad operativa para sostener disciplina arquitectónica.
- Casos donde el dominio es trivial y el costo de abstracción supera el beneficio.

# Consequences
Consecuencias positivas:
- El dominio de pagos se mantiene estable frente a cambios de infraestructura.
- Integraciones con proveedores se vuelven reemplazables y testeables.
- Se reduce riesgo de regresión en casos de uso críticos de cobro/autorización.

Consecuencias negativas:
- Mayor inversión en diseño inicial y definición de contratos.
- Necesidad de governance técnico para evitar inconsistencias entre adaptadores.

Impacto operativo esperado:
- Mejor MTTR en incidentes de proveedor por aislamiento de componentes.
- Mayor control de cambios en pipeline mediante pruebas por capa.

# Alternatives Considered
1. Arquitectura en capas tradicional (Controller-Service-Repository)
- Ventaja: menor complejidad inicial.
- Desventaja: alto acoplamiento progresivo a frameworks e integraciones.

2. Microservicios orientados a integración directa por proveedor
- Ventaja: entrega rápida por feature.
- Desventaja: duplicación de reglas de dominio y deuda de consistencia.

3. Modular Monolith sin puertos explícitos
- Ventaja: simplicidad de despliegue.
- Desventaja: límites de dependencia menos estrictos y menor reemplazabilidad de proveedores.

# Cómo defender esta decisión en una entrevista
Qué decir:
- Elegimos Arquitectura Hexagonal para proteger el núcleo de pagos de cambios externos y reducir el riesgo operativo en integraciones de alta criticidad.
- Esta decisión prioriza continuidad de negocio y evolución controlada en un contexto fintech.

Preguntas frecuentes:
- Por qué no usar arquitectura en capas simple.
- Si no es overengineering para una primera versión.
- Cómo se valida que el desacoplamiento realmente funciona.

Respuestas sugeridas:
- En pagos, el costo de cambiar proveedor o gestionar incidentes de terceros es alto; desacoplar desde el inicio reduce impacto futuro.
- Se evita overengineering limitando puertos a capacidades reales y revisándolos por ADR.
- Se valida con pruebas de contrato en puertos, tests del dominio aislado y simulación de reemplazo de adaptadores.

Errores comunes al explicarlo:
- Presentarlo como dogma en lugar de decisión contextual.
- No reconocer el costo inicial de complejidad.
- No mostrar métricas operativas o ejemplos de reemplazo de adaptador.