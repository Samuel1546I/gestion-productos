package com.umb.productos.ui;

import com.umb.productos.exception.AccesoDatosException;
import com.umb.productos.model.Producto;
import com.umb.productos.service.ProductoService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MenuConsola {

    private final ProductoService service;
    private final Scanner scanner = new Scanner(System.in);

    public MenuConsola(ProductoService service) {
        this.service = service;
    }

    public void iniciar() {
        boolean salir = false;
        try {
            while (!salir) {
                mostrarMenu();
                String opcion = leer("Elige una opción: ");
                try {
                    switch (opcion) {
                        case "1" -> registrar();
                        case "2" -> listar();
                        case "3" -> buscar();
                        case "4" -> actualizar();
                        case "5" -> eliminar();
                        case "0" -> salir = true;
                        default -> System.out.println("Opción no válida. Escribe un número del 0 al 5.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (AccesoDatosException e) {
                    System.out.println("Error de base de datos: " + e.getMessage());
                }
            }
            System.out.println("Hasta luego.");
        } catch (NoSuchElementException e) {
            // La entrada se cerró (por ejemplo Ctrl + D o Ctrl + Z): salimos sin caernos.
            System.out.println();
            System.out.println("Entrada finalizada. Hasta luego.");
        }
    }

    // ---------- Opciones del menú ----------

    private void mostrarMenu() {
        System.out.println();
        System.out.println("===== GESTIÓN DE PRODUCTOS =====");
        System.out.println("1. Registrar producto");
        System.out.println("2. Listar todos los productos");
        System.out.println("3. Buscar producto");
        System.out.println("4. Actualizar producto");
        System.out.println("5. Eliminar producto");
        System.out.println("0. Salir");
    }

    private void registrar() {
        System.out.println("--- Registrar producto ---");
        String codigo = leer("Código: ");
        String nombre = leer("Nombre: ");
        String descripcion = leer("Descripción (puede quedar vacía): ");
        String precio = leer("Precio (ej. 1500 o 1500,50): ");
        String cantidad = leer("Cantidad (entero): ");
        Producto p = service.registrar(codigo, nombre, descripcion, precio, cantidad);
        System.out.println("Producto registrado: " + p);
    }

    private void listar() {
        System.out.println("--- Lista de productos ---");
        mostrarLista(service.listar());
    }

    private void buscar() {
        System.out.println("--- Buscar producto ---");
        System.out.println("1. Por código");
        System.out.println("2. Por nombre");
        String opcion = leer("Elige una opción: ");
        switch (opcion) {
            case "1" -> {
                String codigo = leer("Código: ");
                System.out.println(service.buscarPorCodigo(codigo));
            }
            case "2" -> {
                String texto = leer("Nombre (o parte del nombre): ");
                mostrarLista(service.buscarPorNombre(texto));
            }
            default -> System.out.println("Opción no válida.");
        }
    }

    private void actualizar() {
        System.out.println("--- Actualizar producto ---");
        String codigo = leer("Código del producto: ");
        Producto actual = service.buscarPorCodigo(codigo);
        System.out.println("Producto actual: " + actual);
        System.out.println("¿Qué quieres cambiar?");
        System.out.println("1. Precio");
        System.out.println("2. Descripción");
        System.out.println("3. Cantidad");
        String opcion = leer("Elige una opción: ");
        Producto nuevo;
        switch (opcion) {
            case "1" -> nuevo = service.actualizarPrecio(codigo,
                    leer("Nuevo precio (ej. 1500 o 1500,50): "));
            case "2" -> nuevo = service.actualizarDescripcion(codigo,
                    leer("Nueva descripción: "));
            case "3" -> nuevo = service.actualizarCantidad(codigo,
                    leer("Nueva cantidad (entero): "));
            default -> {
                System.out.println("Opción no válida. No se cambió nada.");
                return;
            }
        }
        System.out.println("Producto actualizado: " + nuevo);
    }

    private void eliminar() {
        System.out.println("--- Eliminar producto ---");
        String codigo = leer("Código del producto: ");
        Producto p = service.buscarPorCodigo(codigo);
        System.out.println("Vas a eliminar: " + p);
        String respuesta = leer("¿Seguro que quieres eliminarlo? (S/N): ").toLowerCase();
        if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
            service.eliminar(codigo);
            System.out.println("Producto eliminado.");
        } else {
            System.out.println("Eliminación cancelada.");
        }
    }

    // ---------- Utilidades ----------

    private void mostrarLista(List<Producto> productos) {
        if (productos.isEmpty()) {
            System.out.println("No se encontraron productos.");
            return;
        }
        for (Producto p : productos) {
            System.out.println(p);
        }
        System.out.println("Total: " + productos.size());
    }

    private String leer(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }
}