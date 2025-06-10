package com.alojamiento.alojamiento.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // <-- AÑADE ESTA IMPORTACIÓN
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List; // <-- AÑADE ESTA IMPORTACIÓN

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cliente implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private String documento;
    private String telefono;

    @Column(unique = true)
    private String email;
    private String password;

    // --- MÉTODO CORREGIDO ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Asignamos a cada cliente el rol de "USER".
        // El prefijo "ROLE_" es una convención estándar de Spring Security.
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}