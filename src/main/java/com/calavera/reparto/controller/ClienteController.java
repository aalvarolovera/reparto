/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calavera.reparto.controller;

import com.calavera.reparto.exceptions.ClienteNotFoundException;
import com.calavera.reparto.exceptions.EnvioNotFoundException;
import com.calavera.reparto.model.Cliente;
import com.calavera.reparto.model.Envio;
import com.calavera.reparto.repositories.ClienteRepository;
import com.calavera.reparto.repositories.EnvioRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author admin
 */
@RestController
public class ClienteController {

    private final ClienteRepository repository;

    ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/cliente")
    Iterable<Cliente> all() {
        return repository.findAll();
    }

    @PostMapping("/cliente")
    Cliente newCliente(@RequestBody Cliente newCliente) {
        return repository.save(newCliente);
    }

    // Single item
    @GetMapping("/cliente/{id}")
    Cliente one(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
    }

    @PutMapping("/cliente/{id}")
    Cliente updateCliente(@RequestBody Cliente newCliente, @PathVariable Long id) {

        return repository.findById(id)
                .map(cliente -> {
                    cliente.setNombre(newCliente.getNombre());
                    cliente.setDireccionOrigen(newCliente.getDireccionOrigen());
                    cliente.setDireccionDestino(newCliente.getDireccionDestino());
                    cliente.setApellidos(newCliente.getApellidos());
                    cliente.setDni(newCliente.getDni());
                    cliente.setLatitudOrigen(newCliente.getLatitudOrigen());
                    cliente.setLongitudOrigen(newCliente.getLongitudOrigen());
                    cliente.setLatitudDestino(newCliente.getLatitudDestino());
                    cliente.setLongitudDestino(newCliente.getLongitudDestino());
                    return repository.save(cliente);
                })
                .orElseGet(() -> {
                    newCliente.setId(id);
                    return repository.save(newCliente);
                });
    }

    @DeleteMapping("/cliente/{id}")
    void deleteCliente(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
