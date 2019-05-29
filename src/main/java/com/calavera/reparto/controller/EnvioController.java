/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calavera.reparto.controller;

import static com.calavera.reparto.controller.RepartidorController.distanciaCoord;
import com.calavera.reparto.exceptions.ClienteNotFoundException;
import com.calavera.reparto.model.Envio;
import com.calavera.reparto.repositories.EnvioRepository;
import com.calavera.reparto.exceptions.EnvioNotFoundException;
import com.calavera.reparto.model.Cliente;
import com.calavera.reparto.repositories.ClienteRepository;
import com.calavera.reparto.model.Constantes;
import com.calavera.reparto.model.Repartidor;
import com.calavera.reparto.repositories.RepartidorRepository;
import java.util.List;

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
    private final RepartidorRepository repartidorRepo;

    EnvioController(EnvioRepository envioRepository, ClienteRepository clienteRepository, RepartidorRepository repartidorRepo) {
        this.envioRepository = envioRepository;
        this.clienteRepository = clienteRepository;
        this.repartidorRepo = repartidorRepo;
    }

    // Aggregate root
    @GetMapping("/envio")
    Iterable<Envio> all() {
        return envioRepository.findAll();
    }
/*
    @PostMapping("/envio/{clienteOrigenId}/")
    Envio newEnvio(@RequestBody Envio newEnvio, @PathVariable Long clienteOrigenId) {
        Cliente clienteOrigen = clienteRepository.findById(clienteOrigenId)
                .orElseThrow(() -> new ClienteNotFoundException(clienteOrigenId));
        
        
        newEnvio.setIdClienteOrigen(clienteOrigenId);
        newEnvio.setDireccionOrigen(clienteOrigen.getDireccion());
        newEnvio.setLatitud(clienteOrigen.getLatitud());
        newEnvio.setLongitud(clienteOrigen.getLongitud());
        
        
        return envioRepository.save(newEnvio);
    }
    */
 
    /**
     * Un cliente crea un envio y lo asigna a otro cliente ya existente
     * y se guarda
     * 
     * @param newEnvio
     * @param clienteOrigenId
     * @param clienteDestinoId
     * @return envioGuardado
     */
    @PostMapping("/envio/{clienteOrigenId}/{clienteDestinoId}")
    Envio newEnvioToCliente(@RequestBody Envio newEnvio, 
            @PathVariable Long clienteOrigenId, @PathVariable Long clienteDestinoId) {
        Cliente clienteOrigen = clienteRepository.findById(clienteOrigenId)
                .orElseThrow(() -> new ClienteNotFoundException(clienteOrigenId));
        Cliente clienteDestino = clienteRepository.findById(clienteDestinoId)
                .orElseThrow(() -> new ClienteNotFoundException(clienteOrigenId));
        
        newEnvio.setIdClienteOrigen(clienteOrigenId);
        newEnvio.setDetalles(newEnvio.getDetalles());
        newEnvio.setFecha(newEnvio.getFecha());
        newEnvio.setLatitud(clienteOrigen.getLatitud());
        newEnvio.setLongitud(clienteOrigen.getLongitud());
        newEnvio.setDireccionOrigen(clienteOrigen.getDireccion());
        newEnvio.setDireccionDestino(clienteDestino.getDireccion());
        newEnvio.setCliente(clienteDestino);
        newEnvio.setEstado(Constantes.ENVIOPENDIENTE);
          
        newEnvio.setRepartidor(AsignarRepartidorDisponibleCercano(newEnvio));
        
        return envioRepository.save(newEnvio);
    }
    
     /**
     * Asigna repartidor disponible más cercano al envio
     *
     * @param repartidor
     * @return envio
     */
    public Repartidor AsignarRepartidorDisponibleCercano(Envio envio) {
        double latitudEnvio = envio.getLatitud();
        double longitudEnvio = envio.getLongitud();
        List<Repartidor> listaRepartidoresDisponibles = repartidorRepo.findByDisponible(true);//(List<Repartidor>) repartidorRepo.findAll();
        Repartidor repCercano = null;
        double distancia = 0.0;
        double distanciaMenor = distanciaCoord(latitudEnvio, longitudEnvio, listaRepartidoresDisponibles.get(0).getLatitud(), listaRepartidoresDisponibles.get(0).getLongitud());

        for (Repartidor rep : listaRepartidoresDisponibles) {
            distancia = distanciaCoord(latitudEnvio, longitudEnvio, rep.getLatitud(), rep.getLongitud());
            if (distancia <= distanciaMenor) {
                distanciaMenor = distancia;
                repCercano = rep;
            }
        }
        if (repCercano != null) {
            
            repCercano.setDisponible(false);
            repCercano.setEnvioId(envio.getId());//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            envio.setEstado(Constantes.ENVIOREPARTO);
            envio.setRepartidor(repCercano);
           // envioRepo.save(envio);
            // repartidorRepo.save(repartidor); !!!!!!!!!!!!!!!!!!!!!!!!!!
        }
        //Mirar si se puede dar un mensaje si no hay paquetes para dar
        return repCercano;
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
        //Entendemos que el cliente tiene su posición y direccion actualizada
        // con el método updateClientePosicion()
        newEnvio.setLatitud(cliente.getLatitud());
        newEnvio.setLongitud(cliente.getLongitud());
        newEnvio.setDireccionOrigen(cliente.getDireccion());
        //Cambia la direccón de destino del paquete a la del cliente
        newEnvio.setDireccionDestino(clienteDestino.getDireccion());  
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
    
    /**
     * El usuario escribe el estado de los paquetes que desea ver,
     * "Perdido", "Reparto", "Finalizado", "Pendiente".
     * 
     * @param estado
     * @return List <Envio> enviosList
     */
    @GetMapping("/envio/ver/{estado}")
    List<Envio> verEnviosEstado(@PathVariable String estado){
        
        return envioRepository.findByEstado(estado);    
    }

    @DeleteMapping("/envio/{id}")
    void deleteEnvio(@PathVariable Long id) {
        envioRepository.deleteById(id);
    }
}
    //PORBAR!!!!!!!!!!!!!!
    /**
     * Un cliente registrado cuyo id entra por paramentro indica el 
     * cliente receptor del envio ya hecho que todavia está pendiente
     * que será cliente registrado, sí el envio ya ha sido repartido 
     * o está en raparto devolverá nulo
     * 
     * @param id
     * @param envioId
     * @param idClienteDestino
     * @param newEnvio
     * @return envio
     
    @PutMapping("/envio/{envioId}/cliente/{id}/enviar/{idClienteDestino}")
    Envio envioFromCliente(@PathVariable Long id, @PathVariable Long envioId,
            @PathVariable Long idClienteDestino, @RequestBody Envio newEnvio) {
        //  Cliente c = clienteRepository.findById(id).get();
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new EnvioNotFoundException(id));
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        Cliente clienteDestino = clienteRepository.findById(idClienteDestino)
                .orElseThrow(() -> new ClienteNotFoundException(idClienteDestino));
        
        if(envio.getEstado().equals(Constantes.ENVIOPENDIENTE)){
        
        envio.setIdClienteOrigen(id);
        envio.setCliente(clienteDestino);
        //Entendemos que el cliente tiene su posición y direccion actualizada
        // con el método updateClientePosicion()
        envio.setLatitud(cliente.getLatitud());
        envio.setLongitud(cliente.getLongitud());
        envio.setDireccionOrigen(cliente.getDireccion());
        envio.setDetalles(newEnvio.getDetalles());
        envio.setFecha(newEnvio.getFecha());
        //Cambia la direccón de destino del paquete a la del cliente
        envio.setDireccionDestino(clienteDestino.getDireccion());
       
        envio.setEstado(Constantes.ENVIOPENDIENTE);
       // envioRepository.save(envio);
        
        return envioRepository.save(envio);
        }
        return null;
    }
    */