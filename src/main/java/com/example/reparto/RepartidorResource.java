/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.reparto;

import com.example.reparto.model.Constantes;
import com.example.reparto.model.Envio;
import com.example.reparto.model.Repartidor;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
/*
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;


@Path("/repartidores")
public class RepartidorResource {
    private static final Map<Integer, Repartidor> repartidorDB = new ConcurrentHashMap<>();
    private static final AtomicInteger idCounter = new AtomicInteger();

    @POST
    @Consumes("application/json")
    public Response createRepartidor(Repartidor r) {
        r.setId(idCounter.incrementAndGet());
        repartidorDB.put(r.getId(), r);
        System.out.println("Repartidor guardado: " + r.getNombre() + " DNI: "+r.getDni());
        return Response.created(URI.create("/repartidores/" + r.getId())).build();

    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Repartidor getRepartidor(@PathParam("id") int id) {
        final Repartidor r = repartidorDB.get(id);
        if (r == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return r;
    }

    @GET
    @Path("")
    @Produces("application/json")
    public List<Repartidor> getAllRepartidor(@QueryParam("id") int id,
            @QueryParam("nombre") String nombre, @QueryParam("apellidos") String apellidos,
            @QueryParam("dni") String dni, @QueryParam("latitud") double latitud, 
            @QueryParam("longitud") double longitud) {
        System.out.println("Nombre: " + nombre + "DNI: " + dni +" Apellidos: " + apellidos
        + " Latitud: " +latitud +" Longitud: " +longitud );
        
//Solamente se comprueba que se han recibido los parametros /personas?nombre=valornombre&apellido=valorapellido

        if (repartidorDB.isEmpty()) {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
        return new ArrayList(repartidorDB.values());
    }

    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public Response updateRepartidor(@PathParam("id") int id, Repartidor r) {
        Repartidor actual = repartidorDB.get(id);
        if (actual == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        actual.setNombre(r.getNombre());
        actual.setApellidos(r.getApellidos());
        actual.setDni(r.getDni());
        actual.setLatitud(r.getLatitud());
        actual.setLongitud(r.getLongitud());
        
        System.out.println(repartidorDB.get(id).getNombre());
        return Response.accepted().build();
    }
    
    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public Response updateEnvio(@PathParam("id") int idEnv1, Envio idEnv2,
            @PathParam("latitud") double latEnv1,
            @PathParam("longitud") double longEnv1, @PathParam("latitud") double latEnv2,
            @PathParam("longitud2") double longEnv2) {
        
        
        distanciaCoord(latEnv1, longEnv1, latEnv2, longEnv2);
        
        
        
        return Response.accepted().build();
    }

    @DELETE
    @Path("{id}")
    @Consumes("application/json")
     public Response deleteRepartidor(@PathParam("id") int id) {
         Repartidor actual = repartidorDB.get(id);
        if (actual == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        repartidorDB.remove(id);

        System.out.println(actual.getId()+actual.getNombre()+" ha sido borrado");
        return Response.accepted().build();
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
}
*/