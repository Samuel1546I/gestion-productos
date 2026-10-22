package com.umb.productos.dao;

import com.umb.productos.db.ConexionBD;
import com.umb.productos.exception.AccesoDatosException;
import com.umb.productos.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {

    private static final String COLUMNAS = "codigo, nombre, descripcion, precio, cantidad";

    private final ConexionBD conexionBD;

    public ProductoDAOImpl(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void insertar(Producto p) {
        String sql = "INSERT INTO productos (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?)";
        try (Connection c = conexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getCantidad());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (esCodigoDuplicado(e)) {
                throw new AccesoDatosException(
                        "Ya existe un producto con el código '" + p.getCodigo() + "'.", e);
            }
            throw new AccesoDatosException("No se pudo registrar el producto.", e);
        }
    }

    @Override
    public Producto buscarPorCodigo(String codigo) {
        String sql = "SELECT " + COLUMNAS + " FROM productos WHERE codigo = ?";
        try (Connection c = conexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo consultar el producto.", e);
        }
    }

    @Override
    public List<Producto> buscarPorNombre(String texto) {
        String sql = "SELECT " + COLUMNAS + " FROM productos WHERE nombre LIKE ? ORDER BY nombre";
        List<Producto> lista = new ArrayList<>();
        try (Connection c = conexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo buscar por nombre.", e);
        }
        return lista;
    }

    @Override
    public List<Producto> listarTodos() {
        String sql = "SELECT " + COLUMNAS + " FROM productos ORDER BY codigo";
        List<Producto> lista = new ArrayList<>();
        try (Connection c = conexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo listar los productos.", e);
        }
        return lista;
    }

    @Override
    public boolean actualizar(Producto p) {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, cantidad = ? "
                + "WHERE codigo = ?";
        try (Connection c = conexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getCantidad());
            ps.setString(5, p.getCodigo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo actualizar el producto.", e);
        }
    }

    @Override
    public boolean eliminar(String codigo) {
        String sql = "DELETE FROM productos WHERE codigo = ?";
        try (Connection c = conexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo eliminar el producto.", e);
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                rs.getInt("cantidad"));
    }

    private boolean esCodigoDuplicado(SQLException e) {
        String mensaje = e.getMessage();
        return mensaje != null && mensaje.contains("UNIQUE constraint failed");
    }
}