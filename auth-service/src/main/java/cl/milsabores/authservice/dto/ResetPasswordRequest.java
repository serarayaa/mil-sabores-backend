package cl.milsabores.authservice.dto;

/**
 * DTO para resetear la contraseña (flujo simple/simulado)
 */
public record ResetPasswordRequest(
        String mail,
        String nuevaPassword
) {
}
