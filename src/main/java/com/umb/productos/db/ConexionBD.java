package com.umb.productos.db;

import com.umb.productos.exception.AccesoDatosException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class ConexionBD {

    private static final String PROP_RUTA = "productos.db";
    private static final String CARPETA_DATOS = "data";
    private static final String ARCHIVO_BD = "productos.db";
    private static final String RECURSO_SCHEMA = "/schema.sql";

    private final Path rutaArchivo;
    private final String url;

    public ConexionBD() {
        this(resolverRutaPorDefecto());
    }

    public ConexionBD(Path rutaArchivo) {
        this.rutaArchivo = rutaArchivo.toAbsolutePath();
        this.url = "jdbc:sqlite:" + this.rutaArchivo;
    }

    public Path getRutaArchivo() {
        return rutaArchivo;
    }

    public Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudo conectar con la base de datos en " + rutaArchivo
                            + ": " + e.getMessage(), e);
        }
    }

    public void inicializar() {
        try {
            Files.createDirectories(rutaArchivo.getParent());
        } catch (IOException e) {
            throw new AccesoDatosException(
                    "No se pudo crear la carpeta de datos " + rutaArchivo.getParent()
                            + ": " + e.getMessage(), e);
        }

        String[] sentencias = leerSentenciasSchema();
        try (Connection conexion = obtenerConexion();
             Statement st = conexion.createStatement()) {
            // DDL estatico leido de schema.sql: no incluye datos del usuario.
            for (String sentencia : sentencias) {
                st.execute(sentencia);
            }
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudo crear la estructura de la base de datos: " + e.getMessage(), e);
        }
    }

    private String[] leerSentenciasSchema() {
        try (InputStream in = ConexionBD.class.getResourceAsStream(RECURSO_SCHEMA)) {
            if (in == null) {
                throw new AccesoDatosException("No se encontro el recurso " + RECURSO_SCHEMA);
            }
            String contenido = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            // Quita comentarios de linea y separa por ';'
            String sinComentarios = Arrays.stream(contenido.split("\\R"))
                    .filter(linea -> !linea.trim().startsWith("--"))
                    .reduce("", (a, b) -> a + b + "\n");
            return Arrays.stream(sinComentarios.split(";"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
        } catch (IOException e) {
            throw new AccesoDatosException("No se pudo leer " + RECURSO_SCHEMA + ": " + e.getMessage(), e);
        }
    }

    private static Path resolverRutaPorDefecto() {
        // 1) Ruta forzada con -Dproductos.db=...
        String propiedad = System.getProperty(PROP_RUTA);
        if (propiedad != null && !propiedad.isBlank()) {
            return Paths.get(propiedad);
        }
        // 2) Junto al JAR (no se puede escribir dentro del JAR)
        try {
            CodeSource fuente = ConexionBD.class.getProtectionDomain().getCodeSource();
            if (fuente != null) {
                Path ubicacion = Paths.get(fuente.getLocation().toURI());
                if (Files.isRegularFile(ubicacion)) {
                    return ubicacion.getParent().resolve(CARPETA_DATOS).resolve(ARCHIVO_BD);
                }
            }
        } catch (URISyntaxException | SecurityException ignorada) {
            // se usa el respaldo
        }
        // 3) Respaldo: ./data desde el directorio de trabajo (ejecucion desde el IDE)
        return Paths.get(System.getProperty("user.dir"), CARPETA_DATOS, ARCHIVO_BD);
    }
}