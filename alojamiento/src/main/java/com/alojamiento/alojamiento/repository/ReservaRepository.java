package com.alojamiento.alojamiento.repository;

import com.alojamiento.alojamiento.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
