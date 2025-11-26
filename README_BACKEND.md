# Microservicios Backend: Pastelería Mil Sabores

## Resumen del proyecto

Este repositorio contiene los microservicios **auth-service** y **product-service** desarrollados en **Spring Boot** para respaldar la aplicación móvil *Pastelería Mil Sabores*. El objetivo es proporcionar un backend seguro y escalable que gestione la autenticación de usuarios y el catálogo de productos de la pastelería.  

- **auth-service**: expuesto en el puerto **8085**, se encarga del registro y autenticación de usuarios mediante credenciales (email, RUT y contraseña). Integra **Firebase** y genera tokens **JWT** para acceder a servicios protegidos【328148745639522†L4-L19】.  
- **product-service**: expuesto en el puerto **8087**, administra el catálogo de productos; permite listar productos disponibles, filtrar por categoría, crear nuevos productos, actualizar y eliminar【328148745639522†L4-L19】.  

Ambos microservicios se conectan a una base de datos **Oracle** mediante un *wallet* y siguen las mejores prácticas de seguridad (contraseñas cifradas con BCrypt, JWT de corta duración). La aplicación móvil consume estos servicios a través de REST.

## Arquitectura técnica y diagrama

Los servicios siguen una arquitectura de microservicios. Cada servicio es una aplicación Spring Boot con responsabilidades bien definidas y se comunican únicamente a través de los clientes móviles. Las características clave son:

- **Separación de dominios**: `auth-service` y `product-service` son independientes. Esto facilita el despliegue individual y la escalabilidad.  
- **Persistencia**: ambos servicios usan **Spring Data JPA** sobre Oracle.   
- **Seguridad**: `auth-service` genera tokens JWT mediante una llave secreta y expiración de 24 horas【485311177202626†L16-L38】【485311177202626†L41-L77】. `product-service` valida el JWT en cada petición.  
- **Configuración externa**: las credenciales de la base de datos y la clave JWT se externalizan mediante variables de entorno y el archivo `application.properties`【328148745639522†L47-L75】【328148745639522†L83-L156】.

La figura siguiente ilustra la relación entre la aplicación móvil y los microservicios (el mismo diagrama se usa en el README del frontend). El cliente Android se autentica en `auth-service` para obtener un token JWT y luego llama a `product-service` para obtener o administrar los productos. Ambos servicios comparten la base de datos Oracle.

![Diagrama de arquitectura]({{file:file-3qhk9LqmNoBhxhRxTqN5AM}})

## Estructura del código

Cada microservicio sigue una estructura similar. A continuación, se describe la organización de paquetes más relevante:

| Paquete / Archivo | Servicio | Descripción |
|------------------|---------|-------------|
| `auth-service/src/main/java/com/milsabores/auth/controller/` | Auth | Controladores REST para registro, login y manejo de usuarios (`UsuarioController`)【209605184952368†L26-L75】. |
| `auth-service/src/main/java/com/milsabores/auth/service/` | Auth | Lógica de negocio (`UsuarioService`) que registra usuarios, valida credenciales y genera JWT【792214114687694†L30-L97】. |
| `auth-service/src/main/java/com/milsabores/auth/model/` | Auth | Entidades JPA (`Usuario`) con campos `nombre`, `rut`, `correo`, `clave` y `jwt`【227937129191612†L20-L47】. |
| `auth-service/src/main/java/com/milsabores/auth/repository/` | Auth | Interfaces de Spring Data JPA para persistir usuarios. |
| `product-service/src/main/java/com/milsabores/product/controller/` | Product | Controladores REST para CRUD de productos (`ProductoController`)【97103411364584†L18-L67】. |
| `product-service/src/main/java/com/milsabores/product/service/` | Product | Lógica de negocio que consulta, crea y actualiza productos. |
| `product-service/src/main/java/com/milsabores/product/model/` | Product | Entidad JPA `Producto` con atributos `id`, `nombre`, `descripcion`, `categoria`, `precio`, `stock`【527715664948338†L15-L34】. |
| `product-service/src/main/java/com/milsabores/product/repository/` | Product | Interfaces de Spring Data JPA para operaciones sobre productos. |
| `common/` | Ambos | Sección (si se implementa) para clases compartidas, como excepciones o utilidades JWT. |

## Tecnologías y dependencias

| Herramienta/Librería | Uso |
|----------------------|-----|
| **Spring Boot 3.x** | Base para crear microservicios rápidos y configurables. |
| **Spring Web** | Facilita la creación de controladores REST. |
| **Spring Security & JWT** | Implementa autenticación y autorización mediante tokens JSON Web. |
| **Spring Data JPA** | Acceso a base de datos relacional con repositorios. |
| **Oracle Database / JDBC** | Base de datos empresarial donde se almacenan usuarios y productos. |
| **BCryptPasswordEncoder** | Encripta contraseñas antes de persistirlas【792214114687694†L30-L97】. |
| **Maven / Maven Wrapper** | Construcción y gestión de dependencias. |
| **Swagger / OpenAPI** | Documentación interactiva (opcional, no presente por defecto pero sugerida). |
| **Docker** | Opcional para contenerizar servicios y sus dependencias. |

## Cómo ejecutar en local

Para probar los microservicios en su máquina local:

1. **Preparación del entorno**: instale **JDK 17** y **Maven 3.8+**. Asegúrese de tener acceso a una instancia de **Oracle Database** y configure un *wallet* si es necesario.

2. **Clonar el repositorio**:

   ```bash
   git clone https://github.com/serarayaa/mil-sabores-backend.git
   cd mil-sabores-backend
   ```

3. **Configurar variables de entorno**: para cada servicio, defina las variables `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` con la conexión a Oracle【328148745639522†L47-L75】. Además, ajuste la propiedad `jwt.secret` en `application.properties` o como variable de entorno.

4. **Construir y ejecutar cada servicio**:

   ```bash
   # Compilar todo el proyecto
   ./mvnw clean package -DskipTests

   # Iniciar auth-service
   cd auth-service
   java -jar target/auth-service-*.jar

   # En otra terminal, iniciar product-service
   cd ../product-service
   java -jar target/product-service-*.jar
   ```

5. **Acceder a la documentación** (si se configura Swagger): visite `http://localhost:8085/swagger-ui.html` y `http://localhost:8087/swagger-ui.html` para explorar los endpoints.

## Cómo ejecutar en AWS EC2

Siga estos pasos para desplegar los microservicios en la nube (guía general del despliegue de Spring Boot【143898066960138†L63-L77】):

1. Cree una instancia **EC2** (Amazon Linux 2) con acceso público y permita tráfico en los puertos 8085 y 8087 en su grupo de seguridad.
2. Conéctese a la instancia por SSH e instale Java:

   ```bash
   sudo yum update -y
   sudo yum install java-17-openjdk -y
   ```

3. Copie los artefactos JAR generados (`auth-service-*.jar` y `product-service-*.jar`) usando `scp` o un servicio de almacenamiento.
4. Exporte las variables de entorno necesarias (conexión a Oracle y secreto JWT). Para un entorno seguro, utilice **AWS Systems Manager Parameter Store** o **Secrets Manager**.
5. Ejecute los servicios en segundo plano:

   ```bash
   nohup java -jar auth-service-*.jar &
   nohup java -jar product-service-*.jar &
   ```

6. Verifique que cada servicio responda en los puertos 8085 y 8087 de la IP pública. Recuerde actualizar el `BASE_URL` en la aplicación móvil para apuntar a la EC2.

7. (Opcional) Utilice **Docker** y **Docker Compose** para orquestar los servicios y el wallet de Oracle; facilite así el despliegue y escalado.

## Ejemplos de endpoints

A continuación se describen los endpoints principales de cada servicio. Todos los parámetros de request y response se transmiten en formato **JSON** y, salvo los de registro y login, requieren un token JWT válido en el encabezado `Authorization`.

### auth-service

| Método | Endpoint | Descripción | Ejemplo de request |
|-------|---------|-------------|-------------------|
| `POST` | `/auth/register` | Registra un nuevo usuario con nombre, RUT, correo electrónico y contraseña【209605184952368†L26-L75】. | ```json
  {"nombre": "Ana", "rut": "12345678-9", "correo": "ana@example.com", "clave": "secreto"}
  ``` |
| `POST` | `/auth/login` | Autentica al usuario y devuelve un JWT【209605184952368†L26-L75】. | ```json
  {"correo": "ana@example.com", "clave": "secreto"}
  ``` |
| `GET` | `/auth/usuarios/firebase/{idFirebase}` | Obtiene los datos del usuario a partir de su ID de Firebase【209605184952368†L26-L75】. | – |
| `PUT` | `/auth/actualizar-nombre/{rut}` | Actualiza el nombre del usuario. Requiere token y se envía el nuevo nombre en el cuerpo. | ```json
  {"nombre": "Ana María"}
  ``` |

### product-service

| Método | Endpoint | Descripción | Ejemplo de uso |
|-------|---------|-------------|----------------|
| `GET` | `/api/productos` | Devuelve la lista completa de productos【97103411364584†L18-L67】. | `curl -H "Authorization: Bearer <token>" http://localhost:8087/api/productos` |
| `GET` | `/api/productos/disponibles` | Lista los productos con stock disponible【97103411364584†L18-L67】. | – |
| `GET` | `/api/productos/{id}` | Obtiene un producto por su identificador【97103411364584†L18-L67】. | – |
| `GET` | `/api/productos/categoria/{categoria}` | Filtra productos por categoría【97103411364584†L18-L67】. | – |
| `POST` | `/api/productos` | Crea un nuevo producto (requiere token con rol de administrador). | ```json
  {"nombre": "Torta de chocolate", "descripcion": "Deliciosa torta de 1 kg", "categoria": "Tortas", "precio": 15.99, "stock": 10}
  ``` |
| `PUT` | `/api/productos/{id}` | Actualiza los datos de un producto existente【97103411364584†L18-L67】. | – |
| `DELETE` | `/api/productos/{id}` | Elimina un producto【97103411364584†L18-L67】. | – |

## Flujo de autenticación y autorización

1. **Registro**: el cliente envía un POST a `/auth/register` con los datos del usuario. El servicio valida que el correo no exista, codifica la contraseña con BCrypt y genera un token JWT【792214114687694†L30-L97】.  
2. **Inicio de sesión**: el cliente envía correo y contraseña a `/auth/login`. El servicio valida la contraseña y devuelve un JWT con expiración de 24 horas【792214114687694†L98-L123】【485311177202626†L41-L77】.  
3. **Acceso a recursos protegidos**: el cliente incluye el encabezado `Authorization: Bearer <token>` al llamar a cualquier endpoint de `product-service`. Este servicio verifica el token y autoriza el acceso.  
4. **Renovación de token**: al expirar el token, el cliente debe autenticarse nuevamente.

## Integración con la aplicación Android

- **Base URL**: configure la URL base en el cliente Retrofit de la app con la IP o dominio de `product-service`. Para pruebas locales con el emulador de Android, se recomienda usar `http://10.0.2.2:8087`.  
- **Manejo de tokens**: la app debe almacenar el JWT de manera segura y enviarlo en cada petición.  
- **Endpoints de autenticación**: utilice los endpoints de `auth-service` para registro y login; los datos devueltos se utilizan para personalizar la experiencia en la app (nombre de usuario, RUT, etc.).

## Puntos débiles detectados y sugerencias de mejora

- **Separación de configuraciones sensibles**: mover el secreto JWT y las credenciales de la base de datos a un servicio de gestión de secretos (AWS Secrets Manager) para evitar comprometerlas.  
- **Versionado de API**: incluir prefijos como `/api/v1/` para facilitar cambios futuros.  
- **Validación y manejo de errores**: unificar respuestas de error con un esquema estándar (códigos y mensajes).  
- **Tests unitarios e integración**: implementar pruebas para controladores, servicios y repositorios a fin de prevenir regresiones.  
- **Documentación interactiva**: añadir Swagger/OpenAPI para generar documentación en línea y pruebas de endpoints.  
- **CI/CD**: automatizar la construcción y despliegue usando GitHub Actions y AWS CodeDeploy.  
- **Observabilidad**: incorporar logs estructurados y monitoreo (por ejemplo, con Spring Boot Actuator) para la detección temprana de problemas.

## Notas finales y créditos

Este backend forma parte del proyecto final del curso de desarrollo de microservicios. Agradecemos a los docentes y colaboradores que brindaron asesoría en la definición de la arquitectura y la implementación.  

**Autores**: @serarayaa y el equipo de desarrollo Mil Sabores.  
**Licencia**: MIT (ver archivo `LICENSE`).  
**Contribuciones**: son bienvenidas. Abra un *issue* o envíe un *pull request* para proponer mejoras o correcciones.
