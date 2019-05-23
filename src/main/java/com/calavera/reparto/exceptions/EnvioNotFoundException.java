/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calavera.reparto.exceptions;

/**
 *
 */
public class EnvioNotFoundException extends RuntimeException {

    public EnvioNotFoundException(Long id) {
        super("Could not find envio " + id);
    }
}
