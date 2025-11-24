# Mil Sabores Backend

## Descripción

Este proyecto backend consta de dos microservicios desarrollados con Spring Boot para la aplicación móvil "Mil Sabores". Los servicios manejan la autenticación de usuarios y la gestión de productos, utilizando una base de datos Oracle con conexión segura mediante wallet.

## Arquitectura

- **auth-service**: Servicio de autenticación y gestión de usuarios. Corre en el puerto 8085.
- **product-service**: Servicio de gestión de productos. Corre en el puerto 8087.

Ambos servicios están construidos con Spring Boot 3.2.5, utilizan JPA/Hibernate para el acceso a datos, y se conectan a una base de datos Oracle mediante JDBC con configuración de wallet para seguridad.

## Tecnologías Utilizadas

- **Java**: Versión 17 para product-service, 21 para auth-service.
- **Spring Boot**: Framework principal para los microservicios.
- **Spring Data JPA**: Para el mapeo objeto-relacional.
- **Oracle Database**: Base de datos principal.
- **HikariCP**: Pool de conexiones.
- **Lombok**: Para reducir código boilerplate.
- **SpringDoc OpenAPI**: Para documentación de APIs.
- **BCrypt**: Para encriptación de contraseñas (solo en auth-service).

## Servicios Detallados

### Auth Service

Este servicio maneja todo lo relacionado con la autenticación y el perfil de usuarios.

#### Archivos y Funcionalidades

- **pom.xml**: Archivo de configuración de Maven. Define las dependencias necesarias, incluyendo Spring Boot Starter Web, JPA, Oracle JDBC, librerías de seguridad para wallet (oraclepki, osdt_core, osdt_cert), BCrypt para contraseñas, Lombok, SpringDoc para Swagger, y H2 para pruebas locales.

- **src/main/resources/application.properties**: Configura el puerto del servidor (8085), la conexión a la base de datos Oracle usando wallet (TNS_ADMIN=C:/Wallet_MILSA), credenciales de admin, configuración del pool HikariCP (máximo 5 conexiones, etc.), y JPA con dialecto Oracle, ddl-auto update, y logging de SQL.

- **src/main/java/cl/milsabores/authservice/AuthServiceApplication.java**: Clase principal anotada con @SpringBootApplication. Punto de entrada para iniciar la aplicación Spring Boot.

- **src/main/java/cl/milsabores/authservice/controller/UsuarioController.java**: Controlador REST mapeado a "/api/usuarios". Expone endpoints para:
  - POST / : Crear un nuevo usuario (registro).
  - GET /firebase/{idFirebase} : Buscar usuario por ID de Firebase.
  - POST /login : Autenticar usuario con mail y password.
  - PATCH /{rut}/nombre : Actualizar el nombre del usuario.
  - PATCH /{rut}/imagen : Actualizar la imagen del usuario (multipart file).
  Incluye un método mapper para convertir entidades a DTOs.

- **src/main/java/cl/milsabores/authservice/dto/CrearUsuarioRequest.java**: Record que representa la solicitud para crear un usuario, con campos: rut, nombre, mail, password, idrol, idfirebase.

- **src/main/java/cl/milsabores/authservice/dto/LoginRequest.java**: Record para la solicitud de login, con campos: mail, password.

- **src/main/java/cl/milsabores/authservice/dto/ActualizarNombreRequest.java**: Record para actualizar el nombre, con campo: nuevoNombre.

- **src/main/java/cl/milsabores/authservice/dto/UsuarioResponseDto.java**: Record para la respuesta de usuario, con campos: rut, nombre, mail, idrol, idfirebase (excluye password e imagen por seguridad).

- **src/main/java/cl/milsabores/authservice/model/Usuario.java**: Entidad JPA mapeada a la tabla "usuario". Campos: rut (clave primaria), nombre, mail, password, idrol, idfirebase, imagen (como byte[] con @Lob). Usa Lombok para getters/setters.

- **src/main/java/cl/milsabores/authservice/repository/UsuarioRepository.java**: Interfaz que extiende JpaRepository. Define métodos personalizados: findByIdfirebase (buscar por ID Firebase), findByMail (buscar por correo), existsByMail (verificar si el correo existe).

- **src/main/java/cl/milsabores/authservice/service/UsuarioService.java**: Servicio que contiene la lógica de negocio. Métodos:
  - crear: Valida que el RUT y mail no existan, encripta la password con BCrypt, y guarda el usuario.
  - buscarPorFirebase: Busca usuario por ID Firebase.
  - buscarPorRut: Busca usuario por RUT.
  - login: Busca por mail y verifica la password con BCrypt.
  - actualizarNombre: Actualiza el nombre del usuario identificado por RUT.
  - actualizarImagen: Actualiza la imagen del usuario como arreglo de bytes.

### Product Service

Este servicio maneja la gestión de productos para la aplicación.

#### Archivos y Funcionalidades

- **pom.xml**: Similar al auth-service, pero sin dependencias de BCrypt y usando Java 17. Incluye Spring Boot Web, JPA, Oracle JDBC, wallet libraries, Lombok, SpringDoc.

- **src/main/resources/application.properties**: Configura el puerto (8087), conexión a Oracle idéntica al auth-service, pool HikariCP, y JPA.

- **src/main/java/cl/milsabores/productservice/ProductServiceApplication.java**: Clase principal de Spring Boot para el servicio de productos.

- **src/main/java/cl/milsabores/productservice/controller/ProductoController.java**: Controlador REST mapeado a "/api/productos". Endpoints:
  - GET / : Listar todos los productos.
  - GET /disponibles : Listar productos disponibles.
  - GET /{id} : Obtener producto por ID.
  - GET /categoria/{categoria} : Listar productos por categoría (ignorando mayúsculas).
  - POST / : Crear un nuevo producto.
  - PUT /{id} : Actualizar un producto existente.
  - DELETE /{id} : Eliminar un producto.

- **src/main/java/cl/milsabores/productservice/model/Producto.java**: Entidad JPA mapeada a la tabla "productos". Campos: id (auto-generado), nombre, descripcion, precio (BigDecimal), categoria, disponible (boolean), urlImagen. Usa Lombok.

- **src/main/java/cl/milsabores/productservice/repository/ProductoRepository.java**: Interfaz JpaRepository. Métodos: findByDisponibleTrue (productos disponibles), findByCategoriaIgnoreCase (por categoría).

- **src/main/java/cl/milsabores/productservice/service/ProductoService.java**: Servicio con lógica de negocio. Métodos:
  - listarTodos: Retorna todos los productos.
  - listarDisponibles: Retorna productos con disponible=true.
  - buscarPorId: Busca producto por ID, lanza excepción si no existe.
  - buscarPorCategoria: Busca productos por categoría.
  - crear: Guarda un nuevo producto.
  - actualizar: Actualiza un producto existente copiando campos del nuevo objeto.
  - eliminar: Elimina un producto si existe.

## C��mo Ejecutar el Proyecto

1. **Requisitos previos**:
   - JDK 17 o 21 instalado.
   - Maven instalado.
   - Base de datos Oracle configurada con wallet en C:/Wallet_MILSA.
   - Credenciales válidas (ADMIN/DuocLPA_8956).

2. **Ejecución**:
   - Para auth-service: Navegar a `auth-service/` y ejecutar `mvn spring-boot:run`.
   - Para product-service: Navegar a `product-service/` y ejecutar `mvn spring-boot:run`.
   - Alternativamente, compilar con `mvn clean install` y ejecutar los JARs generados en `target/`.

3. **Verificación**:
   - Auth-service: http://localhost:8085
   - Product-service: http://localhost:8087

## Documentación de APIs

Ambos servicios incluyen SpringDoc OpenAPI para documentación interactiva:
- Auth-service: http://localhost:8085/swagger-ui/index.html
- Product-service: http://localhost:8087/swagger-ui/index.html

Aquí se pueden probar los endpoints directamente desde el navegador.

## Notas Adicionales

- La configuración de JPA tiene `ddl-auto=update`, lo que actualiza el esquema automáticamente (útil para desarrollo).
- Las contraseñas se encriptan con BCrypt en el auth-service para seguridad.
- Los servicios usan HikariCP para manejo eficiente de conexiones a la base de datos.
- La wallet de Oracle asegura una conexión segura sin exponer credenciales en el código.
