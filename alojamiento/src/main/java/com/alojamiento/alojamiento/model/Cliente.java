package com.alojamiento.alojamiento.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // <-- Importante
import java.util.Collection;
import java.util.Collections; // <-- Importante

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

    // --- CAMPOS AÑADIDOS PARA LA SEGURIDAD ---
    @Column(unique = true) // El email debe ser único
    private String email;
    private String password;
    // ------------------------------------------


    // --- MÉTODOS OBLIGATORIOS DE USERDETAILS ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Collections.emptyList();
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
        return true; // La cuenta nunca expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // La cuenta nunca se bloquea
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las credenciales nunca expiran
    }

    @Override
    public boolean isEnabled() {
        return true; // La cuenta siempre está habilitada
    }
}