@echo off
echo Iniciando Servidor de Desarrollo...
echo.

REM Configurar variables
set NODE_EXE=C:\Program Files\nodejs\node.exe
set NPM_CMD=C:\Program Files\nodejs\npm.cmd

REM Ir al directorio del frontend
cd /d "c:\wamp64\www\ferreteria-app\src\frontend"

echo Directorio: %CD%
echo.

echo Verificando si existe node_modules...
if not exist node_modules (
    echo Instalando dependencias...
    call %NPM_CMD% install
)

echo.
echo Iniciando con npx...
call %NPM_CMD% run dev

pause
