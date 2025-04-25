package com.alojamiento.alojamiento.repository;

import com.alojamiento.alojamiento.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
