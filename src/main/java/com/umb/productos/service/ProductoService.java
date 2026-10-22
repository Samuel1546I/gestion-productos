package com.umb.productos.service;

import com.umb.productos.dao.ProductoDAO;
import com.umb.productos.model.Producto;

import java.util.List;
import java.util.regex.Pattern;

public class ProductoService {

    private static final Pattern PATRON_DECIMAL = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final Pattern PATRON_ENTERO = Pattern.compile("-?\\d+");

    private final ProductoDAO dao;

    public ProductoService(ProductoDAO dao) {
        this.dao = dao;
    }

    // ---------- Operaciones del CRUD ----------

    public Producto registrar(String codigo, String nombre, String descripcion,
                              String precioTxt, String cantidadTxt) {
        String cod = validarTextoObligatorio(codigo, "El código");
        String nom = validarTextoObligatorio(nombre, "El nombre");
        String desc = descripcion == null ? "" : descripcion.trim();
        double precio = parsearPrecio(precioTxt);
        int cantidad = parsearCantidad(cantidadTxt);

        if (dao.buscarPorCodigo(cod) != null) {
            throw new IllegalArgumentException("Ya existe un producto con el código '" + cod + "'.");
        }
        Producto p = new Producto(cod, nom, desc, precio, cantidad);
        dao.insertar(p);
        return p;
    }

    public List<Producto> listar() {
        return dao.listarTodos();
    }

    public Producto buscarPorCodigo(String codigo) {
        String cod = validarTextoObligatorio(codigo, "El código");
        Producto p = dao.buscarPorCodigo(cod);
        if (p == null) {
            throw new IllegalArgumentException("No existe un producto con el código '" + cod + "'.");
        }
        return p;
    }

    public List<Producto> buscarPorNombre(String texto) {
        String t = validarTextoObligatorio(texto, "El texto de búsqueda");
        return dao.buscarPorNombre(t);
    }

    public Producto actualizarPrecio(String codigo, String precioTxt) {
        Producto p = buscarPorCodigo(codigo);
        p.setPrecio(parsearPrecio(precioTxt));
        guardarCambios(p);
        return p;
    }

    public Producto actualizarDescripcion(String codigo, String descripcion) {
        Producto p = buscarPorCodigo(codigo);
        p.setDescripcion(descripcion == null ? "" : descripcion.trim());
        guardarCambios(p);
        return p;
    }

    public Producto actualizarCantidad(String codigo, String cantidadTxt) {
        Producto p = buscarPorCodigo(codigo);
        p.setCantidad(parsearCantidad(cantidadTxt));
        guardarCambios(p);
        return p;
    }

    public void eliminar(String codigo) {
        String cod = validarTextoObligatorio(codigo, "El código");
        if (!dao.eliminar(cod)) {
            throw new IllegalArgumentException("No existe un producto con el código '" + cod + "'.");
        }
    }

    // ---------- Validaciones ----------

    private void guardarCambios(Producto p) {
        if (!dao.actualizar(p)) {
            throw new IllegalArgumentException("No existe un producto con el código '" + p.getCodigo() + "'.");
        }
    }

    private String validarTextoObligatorio(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return texto.trim();
    }

    double parsearPrecio(String texto) {
        String t = validarTextoObligatorio(texto, "El precio");
        String normalizado = t.replace(',', '.');
        if (!PATRON_DECIMAL.matcher(normalizado).matches()) {
            throw new IllegalArgumentException("El precio '" + t + "' no es un número válido. "
                    + "Ejemplos: 1500 o 1500,50 (un solo separador decimal, sin puntos de miles).");
        }
        double valor = Double.parseDouble(normalizado);
        if (Double.isInfinite(valor) || Double.isNaN(valor)) {
            throw new IllegalArgumentException("El precio '" + t + "' es demasiado grande.");
        }
        if (valor < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        return valor == 0 ? 0.0 : valor; // evita guardar -0.0
    }

    int parsearCantidad(String texto) {
        String t = validarTextoObligatorio(texto, "La cantidad");
        if (!PATRON_ENTERO.matcher(t).matches()) {
            throw new IllegalArgumentException("La cantidad '" + t + "' debe ser un número entero, sin decimales.");
        }
        int valor;
        try {
            valor = Integer.parseInt(t);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La cantidad '" + t + "' es demasiado grande.");
        }
        if (valor < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        return valor;
    }
}