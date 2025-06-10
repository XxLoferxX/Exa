package com.alojamiento.alojamiento.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Service // Usamos @Service porque es una clase de lógica de negocio/servicio
public class JwtService {

    // Clave secreta directamente en el código, igual que el docente.
    // IMPORTANTE: Esta clave debe tener 32 bytes (256 bits) para HS256.
    private final String SECRET = "thisIsASecretKeyThatIsAtLeast32BytesLong!";


    private final SecretKey SECRET_KEY = new SecretKeySpec(SECRET.getBytes(), SignatureAlgorithm.HS256.getJcaName());

    /**
     * Genera un token JWT para un nombre de usuario.
     * Expira en 1 hora (60 * 60 * 1000 milisegundos).
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            // Si el token es inválido o ha expirado, Jwts.parser lanzará una excepción.
            return null;
        }
    }

    /**
     * Valida si un token es correcto para un usuario y no ha expirado.
     */
    public boolean validateToken(String token, String username) {
        final String usernameFromToken = extractUsername(token);
        return (username.equals(usernameFromToken) && !isTokenExpired(token));
    }

    /**
     * Verifica si el token ha expirado.
     */
    private boolean isTokenExpired(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            // Si el token no se puede parsear, lo consideramos expirado/inválido.
            return true;
        }
    }
}