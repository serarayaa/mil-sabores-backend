# Arquitectura del sistema
- Añadir pipeline CI (GitHub Actions) que haga build y tests por cada PR.
- Añadir Dockerfile por servicio y un `docker-compose.yml` para desarrollo local.
- Añadir OpenAPI/Swagger en cada servicio y publicar los artefactos.

## Extensiones propuestas

- Exportar métricas a Prometheus/Grafana para monitoreo.
- Añadir logs estructurados (JSON) y correlación de trazas (trace-id) para debugging.

## Observabilidad y trazabilidad

- Se recomienda exponer un API Gateway (p. ej. Spring Cloud Gateway) para unificar autenticación, CORS y rate-limiting en producción.
- Comunicación directa HTTP (no hay bus de mensajes en la versión actual).

## Comunicación entre servicios

- Rate limiting: considerar un gateway o filtros para limitar abusos.
- CORS: limitar orígenes permitidos en la configuración del servicio.
- Roles y permisos: definir roles en claims del JWT y aplicar control de acceso en los controladores (p. ej. `@PreAuthorize`).
- Refresh tokens: almacenar de forma segura (p. ej. en DB en hashed form) y establecer vida útil corta para access tokens.
- Tokens JWT: firmados con un secreto privado (o clave RSA). Mantener el secreto fuera del repositorio.

## Diseño de seguridad

5. Si el token expira, el cliente puede solicitar un refresh (si está implementado) a `/api/auth/refresh`.
4. `product-service` verifica la validez del JWT en cada petición protegida (decode + verificación de firma y claims).
3. Cliente guarda el JWT y lo incluye en el header `Authorization: Bearer <token>` al llamar a `product-service` u otros endpoints protegidos.
2. `auth-service` valida credenciales (BCrypt) y, si son válidas, emite un JWT (y opcionalmente un refresh token).
1. Cliente realiza POST `/api/usuarios/login` con credenciales.

## Flujo de autenticación (resumen)

  - Esquema compartido por los servicios según configuración actual.
- Base de datos Oracle

  - Responsable de CRUD de productos, búsquedas y filtros.
- product-service

  - Gestiona usuarios (incluye almacenamiento de imagen como LOB).
  - Responsable de registro, autenticación y emisión de tokens JWT.
- auth-service

## Componentes principales

Este documento describe la arquitectura lógica del backend `Mil Sabores` y el flujo de comunicación entre los microservicios.


