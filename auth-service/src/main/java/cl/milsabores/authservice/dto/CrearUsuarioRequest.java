// CrearUsuarioRequest.java
package cl.milsabores.authservice.dto;

public record CrearUsuarioRequest(
        String rut,
        String nombre,
        String mail,
        String password,
        Integer idrol,
        String idfirebase
) {}
