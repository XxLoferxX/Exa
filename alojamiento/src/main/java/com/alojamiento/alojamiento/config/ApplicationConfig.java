package com.alojamiento.alojamiento.config;

import com.alojamiento.alojamiento.repository.ClienteRepository; // <-- ¡Cambiado!
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    // Ahora inyectamos el repositorio de Cliente
    private final ClienteRepository clienteRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        // Le decimos a Spring que busque un Cliente por su email.
        // Como Cliente ya implementa UserDetails, podemos devolverlo directamente.
        return username -> clienteRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente no encontrado con email: " + username));
    }
}