package com.umb.productos.dao;

import com.umb.productos.model.Producto;

import java.util.List;

public interface ProductoDAO {

    void insertar(Producto producto);

    Producto buscarPorCodigo(String codigo);

    List<Producto> buscarPorNombre(String texto);

    List<Producto> listarTodos();

    boolean actualizar(Producto producto);

    boolean eliminar(String codigo);
}