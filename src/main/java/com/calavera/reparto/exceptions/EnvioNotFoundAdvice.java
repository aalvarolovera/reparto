/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calavera.reparto.exceptions;

/**
 *
 */

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class EnvioNotFoundAdvice {

  @ResponseBody
  @ExceptionHandler(EnvioNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String envioNotFoundHandler(EnvioNotFoundException ex) {
    return ex.getMessage();
  }
}