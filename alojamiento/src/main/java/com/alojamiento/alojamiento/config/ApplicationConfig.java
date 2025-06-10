package com.alojamiento.alojamiento.config;

import com.alojamiento.alojamiento.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final ClienteRepository clienteRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> clienteRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente no encontrado con email: " + username));
    }

    // --- BEAN AÑADIDO #1: EL PROVEEDOR DE AUTENTICACIÓN ---
    // Este Bean une el buscador de usuarios con el verificador de contraseñas.
    // Es la pieza que le faltaba a tu SecurityConfig.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService()); // Le decimos cómo encontrar usuarios.
        authProvider.setPasswordEncoder(passwordEncoder()); // Le decimos cómo verificar contraseñas.
        return authProvider;
    }

    // --- BEAN AÑADIDO #2: EL CODIFICADOR DE CONTRASEÑAS ---
    // Este Bean es necesario para que el AuthenticationProvider sepa
    // qué algoritmo usar para comparar la contraseña enviada con la guardada.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}