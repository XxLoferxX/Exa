package com.alojamiento.alojamiento.controller;

import com.alojamiento.alojamiento.model.Cliente;
import com.alojamiento.alojamiento.model.Habitacion;
import com.alojamiento.alojamiento.model.Reserva;
import com.alojamiento.alojamiento.repository.ClienteRepository;
import com.alojamiento.alojamiento.repository.HabitacionRepository;
import com.alojamiento.alojamiento.repository.ReservaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/reservas")
@Slf4j
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @GetMapping
    public List<Reserva> getAll() {
        return reservaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getById(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Reserva no encontrada: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Reserva r) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(r.getCliente().getId());
        Optional<Habitacion> habitacionOpt = habitacionRepository.findById(r.getHabitacion().getId());

        if (clienteOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Cliente no encontrado con ID: " + r.getCliente().getId());
        }

        if (habitacionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Habitación no encontrada con ID: " + r.getHabitacion().getId());
        }

        r.setCliente(clienteOpt.get());
        r.setHabitacion(habitacionOpt.get());

        Reserva guardada = reservaRepository.save(r);
        return new ResponseEntity<>(guardada, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> update(@PathVariable Long id, @RequestBody Reserva r) {
        return reservaRepository.findById(id).map(reserva -> {
            reserva.setCliente(r.getCliente());
            reserva.setHabitacion(r.getHabitacion());
            reserva.setFechaEntrada(r.getFechaEntrada());
            reserva.setFechaSalida(r.getFechaSalida());
            return ResponseEntity.ok(reservaRepository.save(reserva));
        }).orElseGet(() -> {
            log.warn("Intento de actualizar reserva no encontrada: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!reservaRepository.existsById(id)) {
            log.warn("Reserva no existe para eliminar: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        reservaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Reserva> patch(@PathVariable Long id, @RequestBody Map<String, Object> cambios) {
        return reservaRepository.findById(id).map(reserva -> {
            if (cambios.containsKey("fechaSalida")) {
                reserva.setFechaSalida(LocalDate.parse(cambios.get("fechaSalida").toString()));
            }
            return ResponseEntity.ok(reservaRepository.save(reserva));
        }).orElseGet(() -> {
            log.warn("No se encontró reserva para patch: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        });
    }
}
