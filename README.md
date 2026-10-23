# Sistema de Gestión de Productos (Java + JDBC + SQLite)

Aplicación de consola para registrar, consultar, actualizar y eliminar productos, con persistencia en SQLite mediante JDBC. Taller de Persistencia de datos en Java, Universidad Manuela Beltrán.

## Requisitos
- Para ejecutar el `.exe`: solo Windows (incluye su propio Java).
- Para compilar o ejecutar el JAR: JDK 21 y Maven.

## Ejecutar el `.exe` (recomendado)
1. Descarga `GestionProductos.zip` desde la sección **Releases** de este repositorio.
2. Descomprímelo y haz doble clic en `GestionProductos\GestionProductos.exe`.

## Ejecutar el JAR
1. Compila: `mvn package` (o Maven → Lifecycle → package en IntelliJ).
2. Ejecuta `ejecutar.bat`, o: `java -jar target\gestion-productos.jar`

## Generar el `.exe`
Después de `mvn package`, ejecuta `crear-exe.bat`. El resultado queda en `dist\GestionProductos\`.

## Base de datos
- Motor: SQLite, creada automáticamente al primer arranque.
- Script de creación: `src/main/resources/schema.sql`
- Ubicación del archivo: carpeta `data\productos.db`, junto al JAR (o dentro de la carpeta `app` del `.exe`). Los datos persisten entre ejecuciones.

## Estructura
- `model`: clase `Producto`
- `dao`: acceso a datos con JDBC y `PreparedStatement`
- `db`: conexión e inicialización de la BD
- `service`: validaciones y reglas de negocio
- `ui`: menú de consola
- `exception`: excepción propia de acceso a datos

## Pruebas
`mvn test` ejecuta las pruebas JUnit del DAO (incluye la prueba de persistencia).
