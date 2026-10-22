package com.umb.productos;

import com.umb.productos.db.ConexionBD;
import com.umb.productos.exception.AccesoDatosException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            ConexionBD bd = new ConexionBD();
            bd.inicializar();
            System.out.println("Base de datos lista en: " + bd.getRutaArchivo());

            try (Connection c = bd.obtenerConexion();
                 PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM productos");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                System.out.println("Productos registrados: " + rs.getInt(1));
            }
        } catch (AccesoDatosException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}