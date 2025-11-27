# Validación y checklist antes de un PR

Sigue estos pasos para validar cambios localmente antes de abrir un Pull Request.

## Requisitos

- JDK 17+
- Maven (o usar `mvnw.cmd` incluido)
- PowerShell (Windows)

## Pasos de validación

1. Build completo (sin saltarse tests):

```
# Desde la raíz
cd .\auth-service; .\mvnw.cmd clean package -DskipTests=false; cd ..\product-service; .\mvnw.cmd clean package -DskipTests=false
```

2. Ejecutar tests unitarios e integración (si aplican):

```
cd .\auth-service; .\mvnw.cmd test; cd ..\product-service; .\mvnw.cmd test
```

3. Revisar reportes de surefire:

- `auth-service/target/surefire-reports`
- `product-service/target/surefire-reports`

4. Smoke test local (servicios en ejecución):

```
# Arrancar ambos servicios (en dos consolas)
cd .\auth-service; .\mvnw.cmd spring-boot:run
cd ..\product-service; .\mvnw.cmd spring-boot:run

# Probar endpoints básicos
Invoke-RestMethod -Method Get -Uri http://localhost:8085/actuator/health
Invoke-RestMethod -Method Get -Uri http://localhost:8087/actuator/health
```

5. Comprobar OpenAPI (si está habilitado):

- http://localhost:8085/swagger-ui/index.html
- http://localhost:8087/swagger-ui/index.html

## Resultado esperado

- Build exitoso (`BUILD SUCCESS`).
- Tests unitarios pasan.
- Actuators salud OK.

Si algo falla, revisar logs en la consola del servicio y los archivos `target/*/logs`.


