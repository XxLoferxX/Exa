package com.alojamiento.alojamiento.controller;

import com.alojamiento.alojamiento.model.Habitacion;
import com.alojamiento.alojamiento.repository.HabitacionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.*;

@RestController
@RequestMapping("/habitaciones")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class HabitacionController {

    @Autowired
    private HabitacionRepository habitacionRepository;

    @GetMapping
    public List<Habitacion> getAll() {
        return habitacionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Habitacion> getById(@PathVariable Long id) {
        return habitacionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Habitación no encontrada con ID: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @PostMapping
    public ResponseEntity<Habitacion> create(@RequestBody Habitacion h) {
        return new ResponseEntity<>(habitacionRepository.save(h), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Habitacion> update(@PathVariable Long id, @RequestBody Habitacion nueva) {
        return habitacionRepository.findById(id)
                .map(h -> {
                    h.setNumero(nueva.getNumero());
                    h.setTipo(nueva.getTipo());
                    h.setPrecio(nueva.getPrecio());
                    return ResponseEntity.ok(habitacionRepository.save(h));
                })
                .orElseGet(() -> {
                    log.warn("Intento de actualizar habitación inexistente: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!habitacionRepository.existsById(id)) {
            log.warn("Habitación no existe para eliminar: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        habitacionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Habitacion> patch(@PathVariable Long id, @RequestBody Map<String, Object> cambios) {
        return habitacionRepository.findById(id).map(habitacion -> {
            if (cambios.containsKey("precio")) {
                habitacion.setPrecio(Double.valueOf(cambios.get("precio").toString()));
            }
            return ResponseEntity.ok(habitacionRepository.save(habitacion));
        }).orElseGet(() -> {
            log.warn("No se encontró habitación para patch: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        });
    }
}
