@echo off
echo Verificando estado de los puertos para Sistema de Ferreteria
echo =====================================================
echo.

echo Verificando puerto 3001 (Backend Express)...
netstat -ano | findstr :3001
if %ERRORLEVEL% EQU 0 (
    echo [OCUPADO] Puerto 3001 esta en uso
) else (
    echo [LIBRE] Puerto 3001 disponible
)
echo.

echo Verificando puerto 5173 (Frontend Vite)...
netstat -ano | findstr :5173
if %ERRORLEVEL% EQU 0 (
    echo [OCUPADO] Puerto 5173 esta en uso
) else (
    echo [LIBRE] Puerto 5173 disponible
)
echo.

echo Verificando procesos Node.js activos...
tasklist | findstr node.exe
if %ERRORLEVEL% EQU 0 (
    echo [ACTIVOS] Hay procesos Node.js corriendo
) else (
    echo [NINGUNO] No hay procesos Node.js activos
)
echo.

echo Verificando carpeta de datos de la aplicacion...
if exist "C:\Users\tamar\.ferreteria-app-data" (
    echo [OK] Carpeta de datos existe: C:\Users\tamar\.ferreteria-app-data
    dir "C:\Users\tamar\.ferreteria-app-data" /b
) else (
    echo [FALTA] No existe la carpeta de datos
)
echo.

pause
