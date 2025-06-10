package com.alojamiento.alojamiento.service;

import com.alojamiento.alojamiento.auth.AuthResponse;
import com.alojamiento.alojamiento.auth.LoginRequest;
import com.alojamiento.alojamiento.auth.RegisterRequest;
import com.alojamiento.alojamiento.model.Cliente;
import com.alojamiento.alojamiento.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    // Repositorio y modelos actualizados
    private final ClienteRepository clienteRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Buscamos el Cliente para generar el token.
        UserDetails user = clienteRepository.findByEmail(request.getEmail()).orElseThrow();

        String token = jwtService.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        // Creamos un nuevo objeto Cliente
        Cliente cliente = Cliente.builder()
                .nombre(request.getFirstname()) // Usamos los campos de RegisterRequest
                .apellido(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))

                .build();

        // Guardamos el nuevo Cliente en la base de datos.
        clienteRepository.save(cliente);

        // Generamos un token para el nuevo cliente.
        String token = jwtService.generateToken(cliente.getUsername()); // getUsername() devuelve el email

        return AuthResponse.builder()
                .token(token)
                .build();
    }
}