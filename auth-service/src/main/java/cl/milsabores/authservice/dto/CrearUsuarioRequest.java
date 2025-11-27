// CrearUsuarioRequest.java
package cl.milsabores.authservice.dto;

public record CrearUsuarioRequest(
        String rut,
        String nombre,
        String mail,
        String password,
        Integer idrol,
        String idfirebase,
        // 🔹 Nueva propiedad: fecha de nacimiento en formato "dd-MM-yyyy", ej: "10-05-2020"
        String fechaNac
) {}
