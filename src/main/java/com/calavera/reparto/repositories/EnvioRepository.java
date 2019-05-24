/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calavera.reparto.repositories;

import com.calavera.reparto.model.Envio;
import com.calavera.reparto.model.Repartidor;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 *
 * @author admin
 */
@RepositoryRestResource(collectionResourceRel = "envio", path = "envio")
public interface EnvioRepository extends PagingAndSortingRepository<Envio, Long>{
    
    List<Envio> findByEstado(@Param("estado") String estado);
    List<Envio> findByRepartidorId(@Param("id")Long repartidorId);
    List<Envio> findByRepartidorDni(@Param("dni")String repartidorDni);
    List<Envio> findByClienteId(@Param("id")Long clienteId);
    List<Envio> findByIdClienteOrigen(@Param("id")Long idClienteOrigen);
   // List<Envio> findByRepartidor(@Param("Repartidor") Long id);
    // Envio findNearest(@Param ("latitud") Double latitud,@Param ("longitud")Double longitud);
}
