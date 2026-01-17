@echo off
echo Iniciando Frontend - Solucion Definitiva...
echo.

REM Ir al directorio
cd C:\wamp64\www\ferreteria-app\src\frontend

REM Usar rutas sin espacios
set PATH=C:\Progra~1\nodejs;%PATH%

REM Verificar Node
node --version

REM Instalar si es necesario
if not exist node_modules\vite (
    echo Instalando Vite...
    npm install vite --save-dev
)

REM Iniciar servidor
echo Iniciando servidor en http://localhost:5173
npx vite

pause
