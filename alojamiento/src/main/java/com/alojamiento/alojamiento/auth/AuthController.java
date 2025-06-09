package com.alojamiento.alojamiento.auth;

import com.alojamiento.alojamiento.service.AuthService;
import com.alojamiento.alojamiento.service.TokenBlacklistService; // <-- Importa el servicio
import jakarta.servlet.http.HttpServletRequest; // <-- Importa HttpServletRequest
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService blacklistService; // <-- Inyecta el servicio

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // ¡NUEVO ENDPOINT DE LOGOUT!
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            blacklistService.addTokenToBlacklist(token);
        }

        return ResponseEntity.ok("Logout exitoso.");
    }
}