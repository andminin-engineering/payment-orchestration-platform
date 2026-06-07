# ADR-002: Uso de Payment Provider Adapters para Integraciones Externas

# Status
Accepted

# Context
La plataforma debe orquestar pagos contra múltiples proveedores con diferencias en APIs, autenticación, semántica de errores, tiempos de respuesta y mecanismos de reconciliación.

Una integración directa del dominio con SDKs externos genera acoplamiento, dificulta fallback entre proveedores y complica la operación en escenarios de degradación parcial.

Problema que resuelve:
- Uniformar la interacción con proveedores heterogéneos.
- Evitar fuga de detalles de proveedor al dominio.
- Habilitar estrategia multi-provider con menor riesgo de cambio.

# Decision
Definir un contrato de proveedor de pagos (port de salida) y materializar adaptadores específicos por proveedor:
- MercadoPagoAdapter
- StripeAdapter
- WalletAdapter

Principios de implementación:
- El dominio trabaja con un modelo canónico de autorización/captura/estado.
- Cada adaptador traduce request/response, errores y timeouts al contrato interno.
- La selección de proveedor se resuelve por política de enrutamiento configurable.

Beneficios:
- Intercambiabilidad de proveedores sin alterar lógica de negocio.
- Menor impacto ante cambios de API externa.
- Mejor capacidad de testing con mocks/fakes por adaptador.
- Mayor resiliencia operativa con estrategias de failover.

Riesgos:
- Incremento de complejidad por mapeos y normalización.
- Riesgo de pérdida de capacidades específicas de un proveedor al usar modelo canónico.
- Necesidad de gobernar versiones de contrato interno.

Trade-offs:
- Se sacrifica acceso directo a features propietarias para ganar consistencia operativa.
- Más esfuerzo de mantenimiento en adaptadores a cambio de desacoplamiento y continuidad.

Cuándo usarlo:
- Plataformas de pago con estrategia multi-adquirente o multi-gateway.
- Sistemas con requerimientos de alta disponibilidad y continuidad comercial.
- Contextos regulatorios donde se necesita trazabilidad uniforme.

Cuándo no usarlo:
- Soluciones con un único proveedor estable y baja probabilidad de cambio.
- MVPs de vida corta donde el costo de estandarización no se amortiza.

# Consequences
Consecuencias positivas:
- Reducción de dependencia estratégica de un proveedor.
- Mejora de tiempos de respuesta ante incidentes externos.
- Mayor velocidad para incorporar nuevos medios/proveedores.

Consecuencias negativas:
- Mayor carga de mantenimiento en capa de adaptación.
- Requiere disciplina para evitar lógica de negocio dentro de adaptadores.

Impacto operativo esperado:
- Menor indisponibilidad percibida por capacidad de rerouting.
- Mejores dashboards de error por taxonomía estandarizada.

# Alternatives Considered
1. Integración directa de cada caso de uso con SDK externo
- Ventaja: implementación inicial rápida.
- Desventaja: alto acoplamiento y difícil gobernanza.

2. BFF por proveedor sin contrato canónico
- Ventaja: equipos autónomos por integración.
- Desventaja: fragmentación semántica y duplicación funcional.

3. Externalizar toda la orquestación en un tercero
- Ventaja: menor esfuerzo interno inicial.
- Desventaja: menor control sobre costos, SLA y diferenciación de negocio.

# Cómo defender esta decisión en una entrevista
Qué decir:
- Diseñamos adaptadores por proveedor para desacoplar el dominio y habilitar continuidad operativa frente a caídas o cambios externos.
- La clave es un contrato canónico interno y observabilidad homogénea.

Preguntas frecuentes:
- No se pierde funcionalidad específica de cada proveedor.
- Cómo manejan diferencias de error y estados transaccionales.
- Cuándo conviene un proveedor único.

Respuestas sugeridas:
- Las capacidades diferenciales se exponen vía extensiones controladas, evitando contaminar el dominio central.
- Estandarizamos taxonomía de errores y estados para tomar decisiones de retry, fallback y compensación.
- Si el negocio no requiere multi-provider, puede iniciarse con uno y mantener el contrato para crecimiento futuro.

Errores comunes al explicarlo:
- Hablar solo de código sin mencionar continuidad de negocio.
- Ignorar costos de mantenimiento de adaptadores.
- No justificar la estrategia de canonical model.