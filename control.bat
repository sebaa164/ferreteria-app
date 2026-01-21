@echo off
echo ========================================
echo  FERRETERIA - Control de Puertos y Vistas
echo ========================================
echo.

echo [1] Ver puertos en uso
echo [2] Iniciar aplicacion (JavaFX)
echo [3] Ver estado de la base de datos
echo [4] Limpiar puertos (matar procesos Java)
echo [5] Salir
echo.
set /p option="Selecciona una opcion: "

if "%option%"=="1" (
    echo.
    echo === PUERTOS EN USO ===
    netstat -an | findstr LISTENING
    echo.
    echo === PROCESOS JAVA ===
    tasklist | findstr java
    pause
)

if "%option%"=="2" (
    echo.
    echo Iniciando aplicacion JavaFX...
    cd /d "c:\wamp64\www\Sistema-Ferreteria-main\Sistema-Ferreteria-main"
    mvn javafx:run
)

if "%option%"=="3" (
    echo.
    echo === ESTADO DE BASE DE DATOS ===
    echo Ubicacion: %USERPROFILE%\.ferreteria-java-data\ferreteria.db
    if exist "%USERPROFILE%\.ferreteria-java-data\ferreteria.db" (
        echo Base de datos encontrada
        echo.
        echo Tablas:
        sqlite3 "%USERPROFILE%\.ferreteria-java-data\ferreteria.db" ".tables"
        echo.
        echo Productos:
        sqlite3 "%USERPROFILE%\.ferreteria-java-data\ferreteria.db" "SELECT COUNT(*) FROM products;"
    ) else (
        echo Base de datos no encontrada
    )
    pause
)

if "%option%"=="4" (
    echo.
    echo Deteniendo procesos Java...
    taskkill /F /IM java.exe
    echo Puertos limpiados
    pause
)

if "%option%"=="5" (
    exit
)

call control.bat
