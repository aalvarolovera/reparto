/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calavera.reparto.repositories;

/**
 *
 * @author Antonio
 */

import com.calavera.reparto.model.Repartidor;
import java.util.List;
// import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "repartidor", path = "repartidor")
public interface RepartidorRepository extends PagingAndSortingRepository<Repartidor, Long> { // , RepartidorRepositoryCustomDistancia

    Repartidor findByDni(@Param("dni") String dni);///repartidor/search/findByDni?dni=2
    List<Repartidor>findByDisponible(@Param("disponible")boolean disponible);
    List<Repartidor> findByNombre(@Param("nombre") String nombre);
    Repartidor findByEnvioId(@Param("envio_Id")Long envio_Id);
   // Page<Repartidor> findAll(Pageable pageable);
   // List<Repartidor> findAllByNombre(String nombre, Pageable pageable);
    //  Repartidor repartidorCercano(@Param("latitud") double latitud, @Param("longitud") double longitud);
    
   // Repartidor findClosest(@Param("latitud") double latitud,@Param("longitud")  double longitud);
    
}
