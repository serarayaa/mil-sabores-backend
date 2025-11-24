package cl.milsabores.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    // Debe tener buena longitud para HS256
    private static final String SECRET_KEY =
            "mil-sabores-super-secreto-para-jwt-2025-con-bastantes-caracteres";

    // 24 horas
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24;

    // =====================================
    // GENERACIÓN
    // =====================================
    public String generateToken(String rut, String email) {
        Map<String, Object> claims = Map.of("email", email);

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(rut) // el RUT será el "subject"
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // =====================================
    // VALIDACIÓN
    // =====================================
    public boolean isTokenValid(String token, String rut) {
        String rutToken = getRut(token);
        return rutToken != null && rutToken.equals(rut) && !isTokenExpired(token);
    }

    public String getRut(String token) {
        return getAllClaims(token).getSubject();
    }

    public String getEmail(String token) {
        return getAllClaims(token).get("email", String.class);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = getAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // =====================================
    // INTERNOS
    // =====================================
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(SECRET_KEY.getBytes())
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
