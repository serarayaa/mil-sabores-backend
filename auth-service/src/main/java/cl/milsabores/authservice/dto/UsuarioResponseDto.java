package cl.milsabores.authservice.dto;

public class UsuarioResponseDto {

    private String rut;
    private String email;
    private String token;

    public UsuarioResponseDto(String rut, String email, String token) {
        this.rut = rut;
        this.email = email;
        this.token = token;
    }

    public String getRut() {
        return rut;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }
}
