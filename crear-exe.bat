@echo off
cd /d "%~dp0"
if not exist "target\gestion-productos.jar" (
    echo Primero genera el JAR: Maven - Lifecycle - package
    pause
    exit /b 1
)
if exist build-exe rmdir /s /q build-exe
if exist dist rmdir /s /q dist
mkdir build-exe
copy "target\gestion-productos.jar" build-exe\ > nul
jpackage --type app-image --name GestionProductos --input build-exe --main-jar gestion-productos.jar --main-class com.umb.productos.Main --win-console --add-modules java.sql --java-options "-Dfile.encoding=UTF-8" --dest dist
echo.
echo Listo. El ejecutable queda en dist\GestionProductos\GestionProductos.exe
pause