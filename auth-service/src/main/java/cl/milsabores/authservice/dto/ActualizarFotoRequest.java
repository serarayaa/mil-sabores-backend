package cl.milsabores.authservice.dto;

/**
 * DTO para actualizar la foto de perfil del usuario.
 * Contiene la imagen en formato Base64.
 */
public record ActualizarFotoRequest(String imagenBase64) {
}
