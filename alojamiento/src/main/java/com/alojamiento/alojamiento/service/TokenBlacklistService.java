package com.alojamiento.alojamiento.service;

import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {

    // Usamos un Set en lugar de una List para búsquedas más rápidas (O(1)) y para evitar duplicados.
    // Lo hacemos "thread-safe" para evitar problemas en entornos con múltiples peticiones.
    private final Set<String> tokenBlacklist = Collections.synchronizedSet(new HashSet<>());

    /**
     * Añade un token a la lista negra (lo invalida).
     * @param token El token a invalidar.
     */
    public void addTokenToBlacklist(String token) {
        tokenBlacklist.add(token);
    }

    /**
     * Verifica si un token está en la lista negra.
     * @param token El token a verificar.
     * @return true si el token está en la lista negra, false en caso contrario.
     */
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
}