package com.umb.productos.exception;


public class AccesoDatosException extends RuntimeException {

    public AccesoDatosException(String mensaje) {
        super(mensaje);
    }

    public AccesoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}