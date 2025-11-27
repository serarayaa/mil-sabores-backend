// LoginRequest.java
package cl.milsabores.authservice.dto;

public record LoginRequest(
        String mail,
        String password
) {}
