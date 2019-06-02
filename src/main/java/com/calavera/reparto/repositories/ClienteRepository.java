/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calavera.reparto.repositories;

/**
 *
 * @author Álvaro Lovera Almagro
 */

import com.calavera.reparto.model.Cliente;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "cliente", path = "cliente")
public interface ClienteRepository extends PagingAndSortingRepository<Cliente, Long> {

    Cliente findByDni(@Param("dni") String dni);
    List<Cliente> findByNombre(@Param("dni") String dni);
    List<Cliente> findByApellidos(@Param("apellidos") String apellidos);
 //   List<Cliente> findByIdClienteOrigen(@Param("idClienteOreigen")Long idClienteOrigen);
}
