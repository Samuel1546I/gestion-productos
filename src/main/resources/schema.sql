-- Script de creacion de la base de datos del Sistema de Gestion de Productos
CREATE TABLE IF NOT EXISTS productos (
                                         codigo      TEXT    PRIMARY KEY CHECK (length(trim(codigo)) > 0),
    nombre      TEXT    NOT NULL    CHECK (length(trim(nombre)) > 0),
    descripcion TEXT    NOT NULL DEFAULT '',
    precio      REAL    NOT NULL    CHECK (precio >= 0),
    cantidad    INTEGER NOT NULL    CHECK (cantidad >= 0)
    );