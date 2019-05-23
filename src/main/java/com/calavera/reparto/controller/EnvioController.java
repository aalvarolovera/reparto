/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calavera.reparto.controller;

import com.calavera.reparto.exceptions.ClienteNotFoundException;
import com.calavera.reparto.model.Envio;
import com.calavera.reparto.repositories.EnvioRepository;
import com.calavera.reparto.exceptions.EnvioNotFoundException;
import com.calavera.reparto.model.Cliente;
import com.calavera.reparto.repositories.ClienteRepository;
import com.calavera.reparto.model.Constantes;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EnvioController {

    private final EnvioRepository envioRepository;
    private final ClienteRepository clienteRepository;

    EnvioController(EnvioRepository envioRepository, ClienteRepository clienteRepository) {
        this.envioRepository = envioRepository;
        this.clienteRepository = clienteRepository;
    }

    // Aggregate root
    @GetMapping("/envio")
    Iterable<Envio> all() {
        return envioRepository.findAll();
    }

    @PostMapping("/envio")
    Envio newEnvio(@RequestBody Envio newEnvio) {
        return envioRepository.save(newEnvio);
    }
 
    /**
     * Un cliente registrado cuyo id entra por paramentro hace un envio nuevo a
     * otro cliente registrado (@RequestBody Envio newEnvio)
     * 
     * @param id
     * @param newEnvio
     * @return newEnvio
     */
    @PostMapping("/envio/cliente/{id}/enviar/{idClienteDestino}")
    Envio newEnvioFromCliente(@PathVariable Long id,
            @PathVariable Long idClienteDestino, @RequestBody Envio newEnvio) {
        //  Cliente c = clienteRepository.findById(id).get();      
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        Cliente clienteDestino = clienteRepository.findById(idClienteDestino)
                .orElseThrow(() -> new ClienteNotFoundException(idClienteDestino));

        newEnvio.setIdClienteOrigen(id);
        newEnvio.setCliente(clienteDestino);
        newEnvio.setLatitud(cliente.getLatitudOrigen());
        newEnvio.setLongitud(cliente.getLongitudOrigen());
        newEnvio.setEstado(Constantes.ENVIOPENDIENTE);
        envioRepository.save(newEnvio);

        return envioRepository.save(newEnvio);
    }

    // Single item
    @GetMapping("/envio/{id}")
    Envio one(@PathVariable Long id) {

        return envioRepository.findById(id)
                .orElseThrow(() -> new EnvioNotFoundException(id));
    }

    @PutMapping("/envio/{id}")
    Envio replaceEnvio(@RequestBody Envio newEnvio, @PathVariable Long id) {

        return envioRepository.findById(id)
                .map(envio -> {
                    envio.setCliente(newEnvio.getCliente());
                    envio.setDireccionOrigen(newEnvio.getDireccionOrigen());
                    envio.setDireccionDestino(newEnvio.getDireccionDestino());
                    envio.setLatitud(newEnvio.getLatitud());
                    envio.setLongitud(newEnvio.getLongitud());
                    envio.setEstado(newEnvio.getEstado());
                    envio.setFecha(newEnvio.getFecha());
                    envio.setRepartidor(newEnvio.getRepartidor());
                   // envio.setCliente(newEnvio.getCliente());
                    envio.setIdClienteOrigen(newEnvio.getIdClienteOrigen());
                    return envioRepository.save(envio);
                })
                .orElseGet(() -> {
                    newEnvio.setId(id);
                    return envioRepository.save(newEnvio);
                });
    }

    @DeleteMapping("/envio/{id}")
    void deleteEnvio(@PathVariable Long id) {
        envioRepository.deleteById(id);
    }
}
