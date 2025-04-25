package com.alojamiento.alojamiento.controller;

import com.alojamiento.alojamiento.model.Cliente;
import com.alojamiento.alojamiento.repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/clientes")
@Slf4j
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public List<Cliente> getAll() {
        return clienteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Cliente no encontrado con ID: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @PostMapping
    public ResponseEntity<Cliente> create(@RequestBody Cliente cliente) {
        return new ResponseEntity<>(clienteRepository.save(cliente), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(@PathVariable Long id, @RequestBody Cliente nuevoCliente) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNombre(nuevoCliente.getNombre());
                    cliente.setApellido(nuevoCliente.getApellido());
                    cliente.setDocumento(nuevoCliente.getDocumento());
                    cliente.setTelefono(nuevoCliente.getTelefono());
                    return ResponseEntity.ok(clienteRepository.save(cliente));
                })
                .orElseGet(() -> {
                    log.warn("Intento de actualizar cliente no existente. ID: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!clienteRepository.existsById(id)) {
            log.warn("Intento de eliminar cliente no existente. ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        clienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Cliente> patch(@PathVariable Long id, @RequestBody Map<String, Object> cambios) {
        return clienteRepository.findById(id).map(cliente -> {
            if (cambios.containsKey("telefono")) {
                cliente.setTelefono((String) cambios.get("telefono"));
            }
            return ResponseEntity.ok(clienteRepository.save(cliente));
        }).orElseGet(() -> {
            log.warn("Intento de modificar cliente no existente. ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        });
    }
}
