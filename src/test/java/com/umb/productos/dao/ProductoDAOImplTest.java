package com.umb.productos.dao;

import com.umb.productos.db.ConexionBD;
import com.umb.productos.exception.AccesoDatosException;
import com.umb.productos.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoDAOImplTest {

    @TempDir
    Path tempDir;

    private Path rutaBD;
    private ProductoDAOImpl dao;

    @BeforeEach
    void preparar() {
        rutaBD = tempDir.resolve("test.db");
        ConexionBD bd = new ConexionBD(rutaBD);
        bd.inicializar();
        dao = new ProductoDAOImpl(bd);
    }

    private Producto teclado() {
        return new Producto("P001", "Teclado", "Teclado USB", 45000.5, 10);
    }

    @Test
    void insertaYBuscaPorCodigo() {
        dao.insertar(teclado());
        Producto p = dao.buscarPorCodigo("P001");
        assertNotNull(p);
        assertEquals("Teclado", p.getNombre());
        assertEquals("Teclado USB", p.getDescripcion());
        assertEquals(45000.5, p.getPrecio(), 0.001);
        assertEquals(10, p.getCantidad());
    }

    @Test
    void codigoDuplicadoLanzaExcepcion() {
        dao.insertar(teclado());
        assertThrows(AccesoDatosException.class, () -> dao.insertar(teclado()));
    }

    @Test
    void buscarCodigoInexistenteDevuelveNull() {
        assertNull(dao.buscarPorCodigo("NO-EXISTE"));
    }

    @Test
    void buscarPorNombreEncuentraCoincidenciasParciales() {
        dao.insertar(teclado());
        dao.insertar(new Producto("P002", "Mouse", "", 20000, 5));
        List<Producto> resultado = dao.buscarPorNombre("clad");
        assertEquals(1, resultado.size());
        assertEquals("P001", resultado.get(0).getCodigo());
    }

    @Test
    void actualizaProductoExistente() {
        dao.insertar(teclado());
        Producto p = dao.buscarPorCodigo("P001");
        p.setPrecio(50000);
        p.setCantidad(7);
        assertTrue(dao.actualizar(p));
        Producto actualizado = dao.buscarPorCodigo("P001");
        assertEquals(50000, actualizado.getPrecio(), 0.001);
        assertEquals(7, actualizado.getCantidad());
    }

    @Test
    void actualizarInexistenteDevuelveFalse() {
        assertFalse(dao.actualizar(teclado()));
    }

    @Test
    void eliminaProductoExistente() {
        dao.insertar(teclado());
        assertTrue(dao.eliminar("P001"));
        assertNull(dao.buscarPorCodigo("P001"));
    }

    @Test
    void eliminarInexistenteDevuelveFalse() {
        assertFalse(dao.eliminar("NO-EXISTE"));
    }

    @Test
    void baseDeDatosRechazaPrecioNegativo() {
        Producto malo = new Producto("P003", "Malo", "", -1, 1);
        assertThrows(AccesoDatosException.class, () -> dao.insertar(malo));
    }

    @Test
    void losDatosPersistenAlReabrirLaConexion() {
        dao.insertar(teclado());

        // Simula cerrar y volver a abrir la aplicación: objetos nuevos sobre el mismo archivo.
        ConexionBD bdNueva = new ConexionBD(rutaBD);
        bdNueva.inicializar();
        ProductoDAOImpl daoNuevo = new ProductoDAOImpl(bdNueva);

        Producto p = daoNuevo.buscarPorCodigo("P001");
        assertNotNull(p);
        assertEquals("Teclado", p.getNombre());
    }
}