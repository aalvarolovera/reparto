/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calavera.reparto.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author admin
 */
@Entity
public class Envio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String estado;
    private String detalles;
    private String fecha;
    private String direccionOrigen;
    private String direccionDestino;
    private double latitud;
    private double longitud; 
 //   private double latitudDestino;
  //  private double longitudDestino;
    @ManyToOne
    private Repartidor repartidor;
    @ManyToOne
    private Cliente cliente;
    private Long idClienteOrigen;
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public String getDireccionOrigen() {
        return direccionOrigen;
    }

    public void setDireccionOrigen(String direccionOrigen) {
        this.direccionOrigen = direccionOrigen;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitudOrigen) {
        this.latitud = latitudOrigen;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitudOrigen) {
        this.longitud = longitudOrigen;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Long getIdClienteOrigen() {
        return idClienteOrigen;
    }

    public void setIdClienteOrigen(Long idClienteOrigen) {
        this.idClienteOrigen = idClienteOrigen;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
/*
    public double getLatitudDestino() {
        return latitudDestino;
    }

    public void setLatitudDestino(double latitudDestino) {
        this.latitudDestino = latitudDestino;
    }

    public double getLongitudDestino() {
        return longitudDestino;
    }

    public void setLongitudDestino(double longitudDestino) {
        this.longitudDestino = longitudDestino;
    }
*/
}
