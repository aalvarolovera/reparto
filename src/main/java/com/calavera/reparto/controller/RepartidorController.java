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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Álvaro Lovera Almagro
 */
@RestController
public class RepartidorController {

    private final RepartidorRepository repartidorRepo;
    private final EnvioRepository envioRepo;

    RepartidorController(RepartidorRepository rRepository, EnvioRepository eRepository) {
        this.repartidorRepo = rRepository;
        this.envioRepo = eRepository;
    }

    // @GetMapping("/repartidor")
    //@GetMapping(value="/repartidor", produces= MediaType.APPLICATION_JSON_VALUE, headers = "Accept=application/json")
    //@RequestMapping(value = "/repartidor", method = RequestMethod.GET,produces={MediaType.APPLICATION_JSON_UTF8_VALUE},headers = "Accept=application/json"
    //@GetMapping("/repartidor")
    //@ResponseBody
    @RequestMapping(value = "/repartidor", method = RequestMethod.GET, produces = "application/json")
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
     * Busca el envio en reparto que tiene asignado el repartidor actualmente
     * devolviendo el objeto envio. Si no tiene ningún reparto asignado devuleve
     * nulo
     *
     * @param id
     * @return envio
     */
    @GetMapping("/repartidor/{id}/MiEnvio")
    Envio getEnvioActualDeRepartidor(@PathVariable Long id) {
        repartidorRepo.findById(id)
                .orElseThrow(() -> new RepartidorNotFoundException(id));
        List<Envio> enviosReparto = envioRepo.findByEstado(Constantes.ENVIOREPARTO);
        for (Envio en : enviosReparto) {
            if (en.getRepartidor().getId().equals(id)) {
                return en;
            }
        }
        return null;
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
    @PutMapping("/repartidor/{repartidorId}/envio/{envioId}/finalizado0")
    Repartidor repartidorEntregaEnvio(@RequestBody Repartidor rep,
            @PathVariable Long repartidorId, @PathVariable Long envioId) {
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
     * Un repartidor actualiza su posición, y asigna los envios pendientes a los
     * repartidores disponibles y los devuelve en una lista
     *
     * @param Repartidor rep
     * @param id
     * @return Repartidor repartidor
     */
    @PutMapping("/repartidor/posicion/{id}")
    List<Envio> updatePosicionRepartidor(@RequestBody Repartidor rep, @PathVariable Long id) {

        return repartidorRepo.findById(id)
                .map(repartidor -> {
                    repartidor.setLatitud(rep.getLatitud());
                    repartidor.setLongitud(rep.getLongitud());
                    List<Envio> enviosAsignados = asignarEnviosPendientes();
                    //Asigna envios pendienttes a 
                    //repartidores disponibles
                    /*
                    if (repartidor.isDisponible()) {
                        Envio envioAsignado = AsignarEnvioPendienteCercano(repartidorRepo.findById(id).get());
                        envioAsignado.setRepartidor(repartidorRepo.findById(id).get());

                        //envioRepo.save(envioAsignado);
                    }
                     */
                    repartidorRepo.save(repartidor);
                    return enviosAsignados; // repartidorRepo.save(repartidor);
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

    /**
     * Cambia manualmente la disponibilidad del repartidor
     *
     * @param id
     * @return
     */
    @PutMapping("/repartidor/dis/{id}/{disponible}")
    Repartidor updateRepartidorDisponibilidad(@PathVariable Long id,
            @PathVariable boolean disponible) {

        return repartidorRepo.findById(id)
                .map(repartidor -> {
                    repartidor.setDisponible(disponible);
                    repartidor.setEnvioId(null);
                    //  repartidor.setDni(newRepartidor.getDni());
                    return repartidorRepo.save(repartidor);
                })
                .orElseThrow(() -> new RepartidorNotFoundException(id));
    }

    @DeleteMapping("/repartidor/{id}")
    void deleteRepartidor(@PathVariable Long id) {
        repartidorRepo.deleteById(id);
    }

    /**
     * Calcula la distancia entre dos puntos, recibe las coordenadas latitud y
     * longitud de cada.
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return distancia
     */
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
     *
     * Asigna los repartidores disponibles más cercanos a los envios con estado
     * "Pendiente"
     *
     * @return listaEnviosAsignados
     */
    public List<Envio> asignarEnviosPendientes() {
        List<Envio> listaEnviosPendientes = envioRepo.findByEstado(Constantes.ENVIOPENDIENTE);
        Repartidor reCercano = null;
        //double distancia = 0.0;
        //double distanciaMenor = distanciaCoord(latitudEnvio, longitudEnvio, listaRepartidores.get(0).getLatitud(), listaRepartidores.get(0).getLongitud());

        for (Envio envio : listaEnviosPendientes) {
            reCercano = calcularRepartidorDisponibleCercano(envio.getLatitud(), envio.getLongitud());

            reCercano.setEnvioId(envio.getId());
            reCercano.setDisponible(false);
            /*
            distancia = distanciaCoord(latitudEnvio, longitudEnvio, repar.getLatitud(), repar.getLongitud());
            if (distancia <= distanciaMenor) {
                distanciaMenor = distanciaCoord(latitudEnvio, longitudEnvio, repar.getLatitud(), repar.getLongitud());
                reCercano = repar;
            }
             */
        }
        return listaEnviosPendientes;
    }

    /**
     * Recibe la latidud y longitud del envio, y calcula el repartidor
     * disponible más cercano
     *
     * @param latitudEnvio
     * @param longitudEnvio
     * @return repartidorCerano
     */
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
     * Devuelve los todos los envios que tiene o ha tenido un Repartidor
     *
     * @param id
     * @return envios
     */
    @GetMapping("/repartidor/{id}/historial/envios")
    List<Envio> historialEnvios(@PathVariable Long id) {

        Repartidor repartidor = repartidorRepo.findById(id)
                .orElseThrow(() -> new RepartidorNotFoundException(id));
        List<Envio> envios = envioRepo.findByRepartidorId(id);

        return envios;
    }

    /**
     * Recomendable usar antes asignarEnviosPendientes
     *
     * Asigna envio pendiente más cercano al repartidor si existe uno
     *
     * @param repartidor
     * @return envio
     */
    public Envio asignarEnvioPendienteCercano(Repartidor repartidor) {
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

    /**
     * Recomendable usar antes asignarEnviosPendientes
     *
     * Calcula el repartidor más cercano con respecto a la latitud y logitud
     * introducidas
     *
     * @param latitud
     * @param longitud
     * @return repartidor
     */
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
/**
 * Cambia el estado del Envio a Perdido, y pone al Repartidor a disponible y 
 * su id de envio actual a nulo
 * 
 * @param repartidorId
 * @param envioId
 * @return envio
 */
    @PutMapping("/repartidor/{repartidorId}/envio/{envioId}/perdido")
    Envio perdidaEnvioRepartidor(@PathVariable Long repartidorId, @PathVariable Long envioId) {

       Envio en = envioRepo.findById(envioId)
                .orElseThrow(() -> new EnvioNotFoundException(envioId));  
        Repartidor rep = repartidorRepo.findById(repartidorId)
                .orElseThrow(() -> new RepartidorNotFoundException(repartidorId));
        if(en.getEstado().equals(Constantes.ENVIOREPARTO)){
            en.setEstado(Constantes.ENVIOPERDIDO);
            rep.setDisponible(true);
            rep.setEnvioId(null);    
        }
        return envioRepo.save(en);
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
