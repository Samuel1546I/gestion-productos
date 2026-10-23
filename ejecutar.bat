@echo off
chcp 65001 > nul
cd /d "%~dp0"
if exist "gestion-productos.jar" (
    java -Dfile.encoding=UTF-8 -jar gestion-productos.jar
) else (
    java -Dfile.encoding=UTF-8 -jar target\gestion-productos.jar
)
pause