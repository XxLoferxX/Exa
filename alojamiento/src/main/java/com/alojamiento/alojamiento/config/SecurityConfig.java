package com.alojamiento.alojamiento.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Anotación importante para activar la configuración de seguridad web.
@RequiredArgsConstructor // Para inyectar las dependencias final.
public class SecurityConfig {

    // Dependencias que Spring inyectará automáticamente
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitar CSRF (Cross-Site Request Forgery) porque usamos tokens JWT sin estado.
                .csrf(csrf -> csrf.disable())

                // 2. Definir las reglas de autorización para las peticiones HTTP.
                .authorizeHttpRequests(auth -> auth
                        // 2a. Lista blanca: estas rutas son públicas y no requieren autenticación.
                        .requestMatchers(
                                "/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 2b. Regla general: cualquier otra petición debe ser autenticada.
                        .anyRequest().authenticated()
                )

                // 3. Configurar la gestión de sesiones para que sea sin estado (STATELESS).
                // Esto es crucial para las APIs REST con JWT, ya que cada petición se valida por sí misma.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Conectar nuestro proveedor de autenticación personalizado.
                // Esta es la pieza clave que le dice a Spring cómo validar usuario/contraseña.
                .authenticationProvider(authenticationProvider)

                // 5. Añadir nuestro filtro JWT antes del filtro de autenticación estándar de Spring.
                // Esto asegura que nuestro filtro revise el token en cada petición.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Este Bean es necesario para poder inyectar el AuthenticationManager en nuestro AuthService.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}