package cl.milsabores.authservice.dto;

/**
 * DTO para iniciar el proceso de recuperación de contraseña
 */
public record RecuperarPasswordRequest(
        String mail
) {
}
