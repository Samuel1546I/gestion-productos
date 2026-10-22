package com.umb.productos;

import com.umb.productos.dao.ProductoDAOImpl;
import com.umb.productos.db.ConexionBD;
import com.umb.productos.exception.AccesoDatosException;
import com.umb.productos.service.ProductoService;
import com.umb.productos.ui.MenuConsola;

public class Main {
    public static void main(String[] args) {
        try {
            ConexionBD bd = new ConexionBD();
            bd.inicializar();
            System.out.println("Base de datos: " + bd.getRutaArchivo());

            ProductoService service = new ProductoService(new ProductoDAOImpl(bd));
            new MenuConsola(service).iniciar();
        } catch (AccesoDatosException e) {
            System.err.println("No se pudo iniciar la base de datos: " + e.getMessage());
            System.exit(1);
        }
    }
}