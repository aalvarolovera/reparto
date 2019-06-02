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
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Álvaro Lovera Almagro
 */
@RestController
public class ClienteController {

    private final ClienteRepository repository;
    private final EnvioRepository repositoryEnvio;

    ClienteController(ClienteRepository repository, EnvioRepository repositoryEnvio) {
        this.repository = repository;
        this.repositoryEnvio = repositoryEnvio;
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
    /**
     * Actaliza un cliente, si el Id que del parametro no existe crea uno nuevo 
     * con ese id
     * @param newCliente
     * @param id
     * @return 
     */
    @PutMapping("/cliente/{id}")
    Cliente updateCliente(@RequestBody Cliente newCliente, @PathVariable Long id) {

        return repository.findById(id)
                .map(cliente -> {
                    cliente.setNombre(newCliente.getNombre());
                    cliente.setDireccion(newCliente.getDireccion());
                    // cliente.setDireccionDestino(newCliente.getDireccionDestino());
                    cliente.setApellidos(newCliente.getApellidos());
                    cliente.setDni(newCliente.getDni());
                    cliente.setLatitud(newCliente.getLatitud());
                    cliente.setLongitud(newCliente.getLongitud());
                    // cliente.setLatitudDestino(newCliente.getLatitudDestino());
                    // cliente.setLongitudDestino(newCliente.getLongitudDestino());
                    return repository.save(cliente);
                })
                .orElseGet(() -> {
                    newCliente.setId(id);
                    return repository.save(newCliente);
                });
    }

    /**
     * Actaliza las coordenadas del cliente y su dirección
     *
     * @param newCliente
     * @param id
     * @return Cliente clienteguardado
     */
    @PutMapping("/cliente/{id}/posicion")
    Cliente updateClientePosicion(@RequestBody Cliente newCliente, @PathVariable Long id) {

        return repository.findById(id)
                .map(cliente -> {
                    //cliente.setNombre(newCliente.getNombre());
                    // cliente.setDireccion(newCliente.getDireccion());
                    cliente.setDireccion(newCliente.getDireccion());
                    // cliente.setApellidos(newCliente.getApellidos());
                    //  cliente.setDni(newCliente.getDni());
                    cliente.setLatitud(newCliente.getLatitud());
                    cliente.setLongitud(newCliente.getLongitud());
                    // cliente.setLatitudDestino(newCliente.getLatitudDestino());
                    // cliente.setLongitudDestino(newCliente.getLongitudDestino());
                    return repository.save(cliente);
                })
                .orElseThrow(() -> new ClienteNotFoundException(id));
    }

    // Single item
    /**
     * Devuelve los todos los envios que tiene o ha tenido un cliente
     *
     * @param id
     * @return envios
     */
    @GetMapping("/cliente/{id}/historial/recibidos")
    List<Envio> historialEnviosRecibidos(@PathVariable Long id) {

        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        List<Envio> envios = repositoryEnvio.findByClienteId(id);

        return envios;
    }

    // Single item
    /**
     * Devuelve los todos los envios que a mandado un cliente
     *
     * @param id
     * @return envios
     */
    @GetMapping("/cliente/{id}/historial/enviados")
    List<Envio> historialEnviosEnviados(@PathVariable Long id) {

        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        List<Envio> envios = repositoryEnvio.findByIdClienteOrigen(id);

        return envios;
    }

    @DeleteMapping("/cliente/{id}")
    void deleteCliente(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
