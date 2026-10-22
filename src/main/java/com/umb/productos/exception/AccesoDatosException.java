package com.umb.productos.exception;

/**
 * Error al acceder a la base de datos. Es unchecked para que las capas
 * superiores no dependan de java.sql.
 */
public class AccesoDatosException extends RuntimeException {

    public AccesoDatosException(String mensaje) {
        super(mensaje);
    }

    public AccesoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}