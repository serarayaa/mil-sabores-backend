# Mil Sabores Backend

## Descripción

Este repositorio contiene el backend de la aplicación móvil "Mil Sabores". Está organizado en dos microservicios independientes implementados con Spring Boot:

- `auth-service` — Servicio de autenticación y gestión de usuarios (registro, login, roles, manejo de imágenes de perfil).
- `product-service` — Servicio de gestión de productos (CRUD, búsqueda, filtros por categoría y disponibilidad).

El backend utiliza Oracle como base de datos y se conecta mediante wallet (TNS_ADMIN) para mayor seguridad. Ambos servicios exponen APIs REST y usan Spring Data JPA para el acceso a datos.

## Arquitectura (resumen)

- auth-service: puerto por defecto 8085
- product-service: puerto por defecto 8087
- Comunicación: HTTP REST. Los clientes obtienen JWT del `auth-service` y lo envían en el header `Authorization: Bearer <token>` al `product-service` para operaciones protegidas.

Ambos servicios comparten la misma fuente de datos Oracle (configurada en `application.properties`) y usan HikariCP para el pool de conexiones.

## Requisitos

- Java JDK 17 o superior (el proyecto contiene referencias a Java 17/21 en distintos módulos; usar JDK 17 es suficiente para ejecutar ambos servicios).
- Maven (se incluye wrapper: `mvnw` / `mvnw.cmd`).
- Windows PowerShell (los ejemplos están adaptados a PowerShell v5+).
- Acceso a una base de datos Oracle y wallet (si se usa la configuración incluida en `application.properties`).

## Archivos de configuración importantes

- `auth-service/src/main/resources/application.properties`
  - puerto: `server.port=8085`
  - datasource: configuración Oracle (ej.: `spring.datasource.url=jdbc:oracle:thin:@milsa_high?TNS_ADMIN=C:/Wallet_MILSA`)

- `product-service/src/main/resources/application.properties`
  - puerto: `server.port=8087`
  - datasource: configuración Oracle (idéntica a auth-service en este repo)

> Nota: Las credenciales de base de datos no deberían estar en repositorios públicos. En este repo hay valores en `application.properties` que deben moverse a variables de entorno o a un vault en entornos productivos.

## Variables de entorno y propiedades (ejemplos)

- SPRING_DATASOURCE_URL (alternativa a editar application.properties)
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- JWT_SECRET (si se añade soporte JWT configurable desde variables)

Puedes usar un archivo `.env` local para desarrollo (no commitear). Ejemplo (documentado en `auth-service/HELP.md` y `product-service/HELP.md`).

## Compilar y ejecutar (PowerShell)

1. Compilar ambos servicios:

```
# Desde la raíz del repositorio
cd .\auth-service; .\mvnw.cmd clean package -DskipTests=false; cd ..\product-service; .\mvnw.cmd clean package -DskipTests=false
```

2. Ejecutar un servicio (desde su carpeta):

```
# Auth service
cd .\auth-service
.\mvnw.cmd spring-boot:run
# o ejecutar JAR
java -jar target\auth-service-0.0.1-SNAPSHOT.jar

# Product service
cd ..\product-service
.\mvnw.cmd spring-boot:run
# o ejecutar JAR
java -jar target\product-service-0.0.1-SNAPSHOT.jar
```

3. Ejecutar tests (por servicio):

```
cd .\auth-service; .\mvnw.cmd test
cd ..\product-service; .\mvnw.cmd test
```

## Endpoints principales y ejemplos (PowerShell / curl)

Notas: ajustar paths reales si la aplicación expone rutas distintas; los siguientes endpoints se extrajeron de los controladores del código fuente.

Auth service (http://localhost:8085)

- Registro de usuario: POST /api/usuarios
  - Payload JSON ejemplo:

```
{
  "rut": "11111111-1",
  "nombre": "Usuario Ejemplo",
  "mail": "user@example.com",
  "password": "Password123",
  "idrol": 2,
  "idfirebase": "firebase-id-123"
}
```

PowerShell (Invoke-RestMethod):

```
Invoke-RestMethod -Method Post -Uri http://localhost:8085/api/usuarios -ContentType 'application/json' -Body (ConvertTo-Json @{rut='11111111-1'; nombre='Usuario Ejemplo'; mail='user@example.com'; password='Password123'; idrol=2; idfirebase='firebase-id-123'})
```

cURL equivalente:

```
curl -X POST http://localhost:8085/api/usuarios -H "Content-Type: application/json" -d '{"rut":"11111111-1","nombre":"Usuario Ejemplo","mail":"user@example.com","password":"Password123","idrol":2,"idfirebase":"firebase-id-123"}'
```

- Login: POST /api/usuarios/login (o /api/auth/login según el controlador)
  - Payload:

```
{
  "mail": "user@example.com",
  "password": "Password123"
}
```

PowerShell:

```
Invoke-RestMethod -Method Post -Uri http://localhost:8085/api/usuarios/login -ContentType 'application/json' -Body (ConvertTo-Json @{mail='user@example.com'; password='Password123'})
```

Respuesta esperada: token JWT (o estructura con access & refresh token). Guardar `accessToken` para requests autenticados.

- Refresh token: POST /api/auth/refresh (si está implementado)

Product service (http://localhost:8087)

- Listar productos: GET /api/productos

```
Invoke-RestMethod -Method Get -Uri http://localhost:8087/api/productos
```

- Obtener producto por id: GET /api/productos/{id}

```
Invoke-RestMethod -Method Get -Uri http://localhost:8087/api/productos/1
```

- Crear producto (requiere Authorization header): POST /api/productos

```
$body = @{nombre='Producto 1'; descripcion='Descripción'; precio=12000; categoria='Bebidas'; disponible=$true; urlImagen='http://...'} | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8087/api/productos -Headers @{Authorization = "Bearer $env:ACCESS_TOKEN"} -ContentType 'application/json' -Body $body
```

cURL equivalente:

```
curl -X POST http://localhost:8087/api/productos -H "Content-Type: application/json" -H "Authorization: Bearer <TOKEN>" -d '{"nombre":"Producto 1","descripcion":"Descripción","precio":12000,"categoria":"Bebidas","disponible":true}'
```

## Documentación interactiva (Swagger/OpenAPI)

Si los servicios incluyen SpringDoc OpenAPI, las UIs están normalmente en:

- http://localhost:8085/swagger-ui/index.html
- http://localhost:8087/swagger-ui/index.html

Comprueba estos endpoints cuando el servicio esté corriendo.

## Notas de seguridad y buenas prácticas

- No dejar credenciales en `application.properties` en repositorios públicos.
- Usar variables de entorno o vaults para secretos (p.ej. JWT secret, DB password).
- Proteger endpoints sensibles con roles y scopes (RBAC).
- Implementar HTTPS en front/proxy y validar CORS.
- Rotación y almacenamiento seguro de refresh tokens.

## Cómo contribuir

1. Fork del repo.
2. Crear una rama con nombre descriptivo: `feature/<lo-que-haces>` o `fix/<issue-number>`.
3. Ejecutar tests localmente y añadir tests para nuevas funcionalidades.
4. Hacer PR hacia `main` y agregar descripción clara.

Consulta `docs/CONTRIBUTING.md` para más detalles.

## Mantenedores

- Nombre: [Placeholder]
- Email: [placeholder@example.com]

(Sustituir por datos reales de los responsables del proyecto).

## Licencia

Este repositorio incluye un archivo `LICENSE` en la raíz; revisa ese fichero para detalles de la licencia.

## Changelog (resumen)

- v0.0.1 - Inicial: Creación de `auth-service` y `product-service` con conexión Oracle y endpoints básicos.

---

Para más documentación por módulo, revisa `auth-service/HELP.md` y `product-service/HELP.md`.
