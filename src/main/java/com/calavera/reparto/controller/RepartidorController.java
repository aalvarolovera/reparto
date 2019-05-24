/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calavera.reparto.controller;

import com.calavera.reparto.exceptions.EnvioNotFoundException;
import com.calavera.reparto.exceptions.RepartidorNotFoundException;
import com.calavera.reparto.model.Constantes;
import com.calavera.reparto.model.Envio;
import com.calavera.reparto.model.Repartidor;
import com.calavera.reparto.repositories.EnvioRepository;
import com.calavera.reparto.repositories.RepartidorRepository;
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
 * @author admin
 */
@RestController
public class RepartidorController {

    private final RepartidorRepository repartidorRepo;
    private final EnvioRepository envioRepo;

    RepartidorController(RepartidorRepository rRepository, EnvioRepository eRepository) {
        this.repartidorRepo = rRepository;
        this.envioRepo = eRepository;
    }

    @GetMapping("/repartidor")
    Iterable<Repartidor> all() {
        return repartidorRepo.findAll();
    }

    @PostMapping("/repartidor")
    Repartidor newRepartidor(@RequestBody Repartidor newRepartidor) {
        return repartidorRepo.save(newRepartidor);
    }

    // Single item
    @GetMapping("/repartidor/{id}")
    Repartidor one(@PathVariable Long id) {

        return repartidorRepo.findById(id)
                .orElseThrow(() -> new RepartidorNotFoundException(id));
    }

    /**
     * Recibe el Id del Repartidor y el envio que esté ha entregado en el
     * momento, cambiando el estado del envio a "Finalizado" y el repartidor a
     * disponible
     *
     * @param rep
     * @param repartidorId
     * @param envioId
     * @return
     */
    @PutMapping("/repartidor/{repartidorId}/envio/{envioId}/finalizado")
    Repartidor repartidorEntregaEnvio(@RequestBody Repartidor rep,
            @PathVariable Long repartidorId, @PathVariable Long envioId) {
        //NO CAMBIA EL ENVIO_ID de la tabla REPARTIDOR !!!!!!!
        envioRepo.findById(envioId)
                .orElseThrow(() -> new EnvioNotFoundException(envioId));
        repartidorRepo.findById(repartidorId)
                .map(repartidor -> {
                    //  repartidor.setDni(rep.getDni());
                    // repartidor.setNombre(rep.getNombre());
                    // repartidor.setApellidos(rep.getApellidos());
                    repartidor.setLatitud(rep.getLatitud());
                    repartidor.setLongitud(rep.getLongitud());
                    //Comprueba si el envio tiene ese repartidor asignado
                    //y si el envio está en estado "Pendiente"
                    if (envioRepo.findById(envioId).get().getRepartidor().getId().equals(repartidorId)
                            && ((envioRepo.findById(envioId).get().getEstado()
                                    .equals(Constantes.ENVIOPENDIENTE)))
                            || (envioRepo.findById(envioId).get().getEstado().equals(Constantes.ENVIOREPARTO))) {

                        Repartidor repTermina = repartidorRepo.findById(repartidorId).get();
                        // Envio envioFinalizado = envioRepo.findByRepartidorId(repartidorId);
                        Envio envioFinalizado = envioRepo.findById(envioId).get();
                        repTermina.setDisponible(true);
                        envioFinalizado.setEstado(Constantes.ENVIOFINALIZADO);
                        repTermina.setEnvioId(null);//!!!!!!!!!!
                        // envioFinalizado.setLatitudDestino(repartidor.getLatitud());
                        // envioFinalizado.setLongitudDestino(repartidor.getLongitud());
                        repartidorRepo.save(repTermina);
                        envioRepo.save(envioFinalizado);
                    }
                    //repartidor.setDisponible(true);
                    return repartidorRepo.save(repartidor);
                })
                .orElseThrow(() -> new RepartidorNotFoundException(repartidorId));
        return rep;
    }

    /**
     * Un repartidor actualiza su posición, si se encuentra cerca de un envio
     * pendiente y el repartidor está libre le es asignado el envio
     *
     * @param Repartidor rep
     * @param id
     * @return Repartidor repartidor
     */
    @PutMapping("/repartidor/posicion/{id}")
    Repartidor updatePosicionRepartidor(@RequestBody Repartidor rep, @PathVariable Long id) {

        return repartidorRepo.findById(id)
                .map(repartidor -> {
                    repartidor.setLatitud(rep.getLatitud());
                    repartidor.setLongitud(rep.getLongitud());
                    if (repartidor.isDisponible()) {
                        Envio envioAsignado = AsignarEnvioPendienteCercano(repartidorRepo.findById(id).get());
                        envioAsignado.setRepartidor(repartidorRepo.findById(id).get());

                        //envioRepo.save(envioAsignado);
                    }
                    return repartidorRepo.save(repartidor);
                })
                .orElseThrow(() -> new RepartidorNotFoundException(id));
    }

    @PutMapping("/repartidor/{id}")
    Repartidor updateRepartidor(@RequestBody Repartidor newRepartidor, @PathVariable Long id) {

        //Aquí puedes obtener un listado de los envios y escoger el que esté sin asignar aun
        // al repartidor, te pongo una respuesta estatica de ejemplo que contiene el primer envio del repositorio
        //return envioRepo.findAll().iterator().next();
        return repartidorRepo.findById(id)
                .map(repartidor -> {
                    repartidor.setNombre(newRepartidor.getNombre());
                    repartidor.setDni(newRepartidor.getDni());
                    repartidor.setApellidos(newRepartidor.getApellidos());
                    repartidor.setDisponible(repartidor.isDisponible());
                    repartidor.setLatitud(newRepartidor.getLatitud());
                    repartidor.setLongitud(newRepartidor.getLongitud());
                    repartidor.setEnvioId(newRepartidor.getEnvioId()); //!!!!!!!!!!!!!!!!!!!
                    return repartidorRepo.save(repartidor);
                })
                .orElseGet(() -> {
                    newRepartidor.setId(id);
                    return repartidorRepo.save(newRepartidor);
                });
    }

    //PROBAR!!!!!!!!!!!!!!
    @PutMapping("/repartidor/dis/{id}/true")
    Repartidor updateRepartidorEstado(@RequestBody Repartidor newRepartidor, @PathVariable Long id) {

        //Aquí puedes obtener un listado de los envios y escoger el que esté sin asignar aun
        // al repartidor, te pongo una respuesta estatica de ejemplo que contiene el primer envio del repositorio
        //return envioRepo.findAll().iterator().next();
        return repartidorRepo.findById(id)
                .map(repartidor -> {
                    repartidor.setDisponible(true);
                    //  repartidor.setDni(newRepartidor.getDni());
                    return repartidorRepo.save(repartidor);
                })
                .orElseGet(() -> {
                    newRepartidor.setId(id);
                    return repartidorRepo.save(newRepartidor);
                });
    }

    /**
     * Comprueba todos los envios sin repartidor que estan pendientes y los
     * asigna a los repartidores más cercanos a cada envio que se encuentran
     * disponibles.
     *
     * @return List<Envio> listaEnvios asignados
     *
     * @GetMapping("/repartidor/asignar") List<Envio> asignarEnvios() {
     * //Repartidor repartidorCercano; //Aquí puedes obtener un listado de los
     * envios y escoger el que esté sin asignar aun // al repartidor, te pongo
     * una respuesta estatica de ejemplo que contiene el primer envio del
     * repositorio //return envioRepo.findAll().iterator().next(); List<Envio>
     * listaEnviosSinRepartidor = envioRepo.findByRepartidorId(null); //
     * List<Repartidor>listRepartidorDisponible =
     * repartidorRepo.findByDisponible(true); for (Envio en :
     * listaEnviosSinRepartidor) { if
     * (en.getEstado().equals(Constantes.ENVIOPENDIENTE)) {
     * en.setRepartidor(calcularRepartidorDisponibleCercano(en.getLatitud(),
     * en.getLongitud())); en.getRepartidor().setDisponible(false); } } return
     * listaEnviosSinRepartidor;//Ya si tienen repartidor }
     */
    @DeleteMapping("/repartidor/{id}")
    void deleteRepartidor(@PathVariable Long id) {
        repartidorRepo.deleteById(id);
    }

    @GetMapping(path = "/repartidor/cercano/{latitud}&{longitud}")
    Repartidor repartidorCercano(@PathVariable double latitud, @PathVariable double longitud) {

        List<Repartidor> listaRepartidores = (List<Repartidor>) repartidorRepo.findAll();
        Repartidor reCercano = null;
        double distancia = 0.0;
        double distanciaMenor = distanciaCoord(latitud, longitud, listaRepartidores.get(0).getLatitud(), listaRepartidores.get(0).getLongitud());

        for (Repartidor repar : listaRepartidores) {
            distancia = distanciaCoord(latitud, longitud, repar.getLatitud(), repar.getLongitud());
            if (distancia <= distanciaMenor) {
                distanciaMenor = distanciaCoord(latitud, longitud, repar.getLatitud(), repar.getLongitud());
                reCercano = repar;
            }
        }
        return reCercano;
        //return listaRepartidores.get(0);
    }

    public static double distanciaCoord(double lat1, double lng1, double lat2, double lng2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        double distancia = Constantes.RADIOTIERRA * va2;

        return distancia;
    }

    /**
     * Asigna envio pendiente más cercano al repartidor si existe uno
     *
     * @param repartidor
     * @return envio
     */
    public Envio AsignarEnvioPendienteCercano(Repartidor repartidor) {
        double latitudRepartidor = repartidor.getLatitud();
        double longitudRepartidor = repartidor.getLongitud();
        List<Envio> listaEnviosPendientes = envioRepo.findByEstado(Constantes.ENVIOPENDIENTE);//(List<Repartidor>) repartidorRepo.findAll();
        Envio enCercano = null;
        double distancia = 0.0;
        double distanciaMenor = distanciaCoord(latitudRepartidor, longitudRepartidor, listaEnviosPendientes.get(0).getLatitud(), listaEnviosPendientes.get(0).getLongitud());

        for (Envio en : listaEnviosPendientes) {
            distancia = distanciaCoord(latitudRepartidor, longitudRepartidor, en.getLatitud(), en.getLongitud());
            if (distancia <= distanciaMenor) {
                distanciaMenor = distancia;
                enCercano = en;
            }
        }
        if (enCercano != null) {
            enCercano.setRepartidor(repartidor);
            repartidor.setDisponible(false);
            repartidor.setEnvioId(enCercano.getId());//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            enCercano.setEstado(Constantes.ENVIOREPARTO);
            envioRepo.save(enCercano);
            // repartidorRepo.save(repartidor); !!!!!!!!!!!!!!!!!!!!!!!!!!
        }
        //Mirar si se puede dar un mensaje si no hay paquetes para dar
        return enCercano;
    }

    public Repartidor calcularRepartidorDisponibleCercano(double latitudEnvio, double longitudEnvio) {
        List<Repartidor> listaRepartidores = repartidorRepo.findByDisponible(true);//(List<Repartidor>) repartidorRepo.findAll();
        Repartidor reCercano = null;
        double distancia = 0.0;
        double distanciaMenor = distanciaCoord(latitudEnvio, longitudEnvio, listaRepartidores.get(0).getLatitud(), listaRepartidores.get(0).getLongitud());

        for (Repartidor repar : listaRepartidores) {
            distancia = distanciaCoord(latitudEnvio, longitudEnvio, repar.getLatitud(), repar.getLongitud());
            if (distancia <= distanciaMenor) {
                distanciaMenor = distanciaCoord(latitudEnvio, longitudEnvio, repar.getLatitud(), repar.getLongitud());
                reCercano = repar;
            }
        }
        return reCercano;
    }
    
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
    @GetMapping("/repartidor/{id}/historial")
    List<Envio> historialEnviosEnviados(@PathVariable String dni, @PathVariable Long id) {

        Repartidor rpartidor = repartidorRepo.findById(id)
                .orElseThrow(() -> new RepartidorNotFoundException(id));
        List<Envio> envios = envioRepo.findByRepartidorDni(dni);

        return envios;
    }

    @DeleteMapping("/cliente/{id}")
    void deleteCliente(@PathVariable Long id) {
        repository.deleteById(id);
    }
    
    
}
/*
@PutMapping("/repartidor/{id}")
    Envio replaceEmployee(@RequestBody Repartidor newRepartidor, @PathVariable Long id) {

        //Aquí puedes obtener un listado de los envios y escoger el que esté sin asignar aun
        // al repartidor, te pongo una respuesta estatica de ejemplo que contiene el primer envio del repositorio
        return envioRepo.findAll().iterator().next();
        
        
        
//        return repartidorRepo.findById(id)
//                .map(repartidor -> {
//                    repartidor.setLatitud(newRepartidor.getLatitud());
//                    repartidor.setLongitud(newRepartidor.getLongitud());
//                    return repartidorRepo.save(repartidor);
//                })
//                .orElseGet(() -> {
//                    newRepartidor.setId(id);
//                    return repartidorRepo.save(newRepartidor);
//                });
    }
 */
