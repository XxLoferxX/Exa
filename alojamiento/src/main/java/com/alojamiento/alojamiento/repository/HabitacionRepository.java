package com.alojamiento.alojamiento.repository;

import com.alojamiento.alojamiento.model.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
}
