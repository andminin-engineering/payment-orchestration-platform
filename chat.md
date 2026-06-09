# Bitacora Interagente

## Fecha
2026-06-08

## Estado del pipeline
- Build and Test: en ajuste, con fixes ya subidos en main.
- CodeQL: estable en success en corridas recientes.
- SonarCloud Scan: falla por configuracion de organizacion.

## Error registrado
```
Failed to execute goal org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar
...
Error 404 on https://api.sonarcloud.io/analysis/analyses
{"errors":[{"msg":"Organization key '***' does not exist."}]}
```

## Causa probable
El valor usado en SONAR_ORGANIZATION (environment production en GitHub) no coincide con el organization key real de SonarCloud.

## Verificaciones para manana
1. Confirmar organization key exacto en SonarCloud:
   - SonarCloud -> Organization -> Administration -> Organization key
2. Revisar en GitHub Environment production:
   - SONAR_ORGANIZATION
   - SONAR_PROJECT_KEY
   - SONAR_HOST_URL (debe ser https://sonarcloud.io)
   - SONAR_TOKEN vigente (no revocado)
3. Validar que SONAR_PROJECT_KEY pertenezca a esa organizacion.
4. Re-ejecutar workflow CI en main tras corregir variables.

## Valores esperados de referencia
- SONAR_HOST_URL=https://sonarcloud.io
- SONAR_PROJECT_KEY=andminin-engineering_payment-orchestration-platform
- SONAR_ORGANIZATION=<organization key real de SonarCloud>

## Nota
No continuar con deploy hasta tener SonarCloud Scan en verde en main.

## Update 2026-06-09
- Se ajusto CI para evitar mismatch de organizacion en SonarCloud.
- Cambio aplicado en .github/workflows/ci.yml:
   - Se removio el override por secrets de:
      - SONAR_ORGANIZATION
      - SONAR_PROJECT_KEY
      - SONAR_HOST_URL
   - Sonar ahora usa:
      - organization/projectKey desde sonar-project.properties
      - host fijo: https://sonarcloud.io
      - token desde secret SONAR_TOKEN
- Proximo paso: push y rerun de CI en main para confirmar SonarCloud Scan en verde.

## Update 2026-06-09 (segunda iteracion)
- Nuevo error observado en SonarCloud Scan:
   - "You must define mandatory property: sonar.organization"
- Causa:
   - El plugin sonar-maven en este flujo no tomo la propiedad desde sonar-project.properties.
- Fix aplicado:
   - En .github/workflows/ci.yml se agregaron explicitamente:
      - -Dsonar.organization=andminin-engineering
      - -Dsonar.projectKey=andminin-engineering_payment-orchestration-platform
- Commit aplicado en main:
   - 6c8d59f970e00ff7e873e92e260b14f48020cb1a
- Estado actual:
   - CI run #18 en espera de aprobacion de environment production.

## Update 2026-06-09 (tercera iteracion)
- Se confirmo el organization key real de SonarCloud para este proyecto:
   - andminin-biz
- Evidencia:
   - API publica de SonarCloud para el componente devolvio organization=andminin-biz.
- Fix aplicado:
   - .github/workflows/ci.yml ahora usa SONAR_ORGANIZATION=andminin-biz.
   - sonar-project.properties tambien fue alineado a andminin-biz.
- El project key se mantiene:
   - andminin-engineering_payment-orchestration-platform
