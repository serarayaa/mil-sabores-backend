// UsuarioResponseDto.java
package cl.milsabores.authservice.dto;

public record UsuarioResponseDto(
        String rut,
        String nombre,
        String mail,
        Integer idrol,
        String idfirebase
) {}
