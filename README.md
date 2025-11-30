# Mil Sabores — Backend (integrado)

Fecha: 2025-11-30

Resumen
-------
Repositorio que contiene el backend del proyecto "Mil Sabores". Está organizado como microservicios independientes implementados con Spring Boot:

- `auth-service` — gestión de usuarios y autenticación (registro, login, recuperación de contraseña básica, generación de JWT).
- `product-service` — gestión del catálogo de productos (CRUD, filtrado, listar disponibles).

Objetivo de este README
-----------------------
Proporcionar a un desarrollador las instrucciones necesarias para ejecutar los servicios en local (Windows PowerShell), entender la arquitectura, conocer los endpoints principales y seguir buenas prácticas de seguridad y despliegue.

Estado actual (comprobado)
--------------------------
- Puertos configurados en el repo:
  - `auth-service` -> server.port = 8081
  - `product-service` -> server.port = 8082
- Conexión a base de datos: Oracle (jdbc:oracle:thin) mediante wallet en ruta Unix en `application.properties` de los servicios.
- Credenciales de ejemplo encontradas en `application.properties` (sólo para desarrollo): usuario `ADMIN`, contraseña `DuocLPA_8956`. Recomendado eliminar/rotar.

Requisitos
----------
- Java JDK 17+
- Maven (se incluye Maven Wrapper: `mvnw.cmd` para Windows)
- Windows PowerShell (v5+) para ejemplos de comandos
- Acceso a una base de datos Oracle o habilitar un perfil/local DB para pruebas (H2 / Docker) si no se dispone de Oracle

Buenas prácticas (previas a ejecutar)
------------------------------------
- No deje credenciales en archivos versionados. Use variables de entorno o un gestor de secretos.
- Asegúrese de adaptar la ruta `TNS_ADMIN` (wallet) a Windows si usa wallet local (p.ej. `C:\oracle_wallet`) o use una conexión alternativa.

Variables de entorno recomendadas
--------------------------------
Para evitar editar `application.properties`, puede exportar las siguientes variables antes de ejecutar (PowerShell):

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:oracle:thin:@<TNS_ALIAS>?TNS_ADMIN=C:/ruta/a/wallet'
$env:SPRING_DATASOURCE_USERNAME = 'ADMIN'
$env:SPRING_DATASOURCE_PASSWORD = 'secret'
$env:JWT_SECRET = 'change_this_secret_in_production'
```

(Adaptar los nombres si decide mapearlos con `spring.datasource.*` como variables de entorno en su configuración.)

Compilar y ejecutar (Windows PowerShell)
---------------------------------------
1) Compilar ambos servicios desde la raíz del repo (usa el wrapper):

```powershell
# Desde la raíz del repositorio
cd 'C:\PROYECTO FINAL APP MOVILES\mil-sabores-backend'
# Compilar auth-service
cd .\auth-service
.\mvnw.cmd clean package -DskipTests
# Volver y compilar product-service
cd ..\product-service
..\auth-service\.\mvnw.cmd clean package -DskipTests
```

2) Ejecutar las aplicaciones (en consolas separadas)

```powershell
# Auth service
cd 'C:\PROYECTO FINAL APP MOVILES\mil-sabores-backend\auth-service'
.\mvnw.cmd spring-boot:run
# o ejecutar el JAR empaquetado
# java -jar target\auth-service-0.0.1-SNAPSHOT.jar

# Product service (en otra ventana de PowerShell)
cd 'C:\PROYECTO FINAL APP MOVILES\mil-sabores-backend\product-service'
.\mvnw.cmd spring-boot:run
# o ejecutar el JAR empaquetado
# java -jar target\product-service-0.0.1-SNAPSHOT.jar
```

3) Ejecutar tests (por servicio)

```powershell
cd 'C:\PROYECTO FINAL APP MOVILES\mil-sabores-backend\auth-service'
.\mvnw.cmd test

cd 'C:\PROYECTO FINAL APP MOVILES\mil-sabores-backend\product-service'
.\mvnw.cmd test
```

Endpoints principales (extraídos del código)
-------------------------------------------
A continuación se listan los endpoints principales y ejemplos de uso con PowerShell `Invoke-RestMethod` y cURL.

Auth service (base: http://localhost:8081)
- Registro: POST /auth/register
  - Body ejemplo JSON:

```json
{
  "rut": "11111111-1",
  "nombre": "Usuario Ejemplo",
  "mail": "user@example.com",
  "password": "Password123",
  "idrol": 2,
  "idfirebase": "firebase-id-123"
}
```

PowerShell:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8081/auth/register -ContentType 'application/json' -Body (ConvertTo-Json @{rut='11111111-1'; nombre='Usuario Ejemplo'; mail='user@example.com'; password='Password123'; idrol=2; idfirebase='firebase-id-123'})
```

- Login: POST /auth/login
  - Body:

```json
{ "mail": "user@example.com", "password": "Password123" }
```

PowerShell:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8081/auth/login -ContentType 'application/json' -Body (ConvertTo-Json @{mail='user@example.com'; password='Password123'})
```

- Recuperar contraseña (simulado): POST /auth/recover-password
- Resetear contraseña: POST /auth/reset-password
- Buscar por Firebase ID: GET /auth/usuarios/firebase/{idFirebase}
- Actualizar nombre: PUT /auth/actualizar-nombre/{rut}

Product service (base: http://localhost:8082)
- Listar productos: GET /api/productos

```powershell
Invoke-RestMethod -Method Get -Uri http://localhost:8082/api/productos
```

- Listar disponibles: GET /api/productos/disponibles
- Obtener por id: GET /api/productos/{id}
- Filtrar por categoría: GET /api/productos/categoria/{categoria}
- Crear producto (requiere Authorization): POST /api/productos

PowerShell ejemplo (suponiendo $env:ACCESS_TOKEN):

```powershell
$body = @{ nombre='Torta'; descripcion='Torta 1kg'; precio=15000; categoria='Tortas'; stock=5 } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8082/api/productos -Headers @{ Authorization = "Bearer $env:ACCESS_TOKEN" } -ContentType 'application/json' -Body $body
```

Notas de seguridad
------------------
- JWT: el `auth-service` maneja la emisión de JWT. Trate el secreto (`JWT_SECRET` o el valor embebido en código/config) como confidencial.
- No commitee credenciales ni secretos. Use fichero `.env` no versionado o gestor de secretos.
- Asegure las comunicaciones con HTTPS en entornos de producción.

Configuración de la base de datos
---------------------------------
Actualmente ambos servicios están configurados para conectar a Oracle mediante `spring.datasource.url` en `application.properties`. Las entradas encontradas usan rutas tipo Unix para `TNS_ADMIN` (p.ej. `/home/ubuntu/oracle_wallet`). En Windows usted deberá:

- Ajustar `TNS_ADMIN` a la ruta Windows del wallet, o
- Usar una base de datos alternativa (H2 en memoria) para desarrollo local — crear un `application-local.properties` o profile `local` que use H2 y documentarlo aquí.

Sugerencia: crear un profile `local` que arranque con H2 para que cualquier desarrollador pueda ejecutar los servicios sin depender de Oracle.

Documentación y pruebas interactivas
-----------------------------------
Si se integra SpringDoc / Swagger, las UIs suelen estar en:
- http://localhost:8081/swagger-ui/index.html
- http://localhost:8082/swagger-ui/index.html

Mejoras sugeridas (prioritarias)
-------------------------------
1. Mover credenciales a variables de entorno o gestor de secretos.
2. Añadir perfil `local` con H2 para facilitar desarrollo.
3. Agregar Swagger/OpenAPI para documentación interactiva.
4. Añadir tests de integración y CI (GitHub Actions).
5. Evitar hardcodear rutas de wallet con rutas Unix; detectar plataforma o configurar por variable.

Contribuir
----------
1. Fork -> branch descriptivo -> commit -> PR hacia `main`.
2. Ejecute tests y agregue pruebas para nueva funcionalidad antes del PR.

Contacto y autores
------------------
Repositorio mantenido por el equipo Mil Sabores. Sustituya los placeholders por información real (nombres y correos) en caso de publicar.

Licencia
--------
Ver el fichero `LICENSE` en la raíz del repositorio para detalles.

Changelog corto
---------------
- 2025-11-30: README integrado y actualizado (puertos reales, comandos PowerShell, endpoints verificados).

---

Para más documentación por módulo, revisa `auth-service/HELP.md` y `product-service/HELP.md`.
