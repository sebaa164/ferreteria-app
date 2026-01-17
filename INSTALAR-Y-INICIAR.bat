@echo off
echo Instalando dependencias del Sistema Ferreteria...
echo.

REM Configurar PATH de forma permanente para esta sesión
set "PATH=C:\Program Files\nodejs;%PATH%"

REM Verificar Node.js
echo Node.js version:
node --version
echo NPM version:
npm --version

REM Ir al directorio principal
cd /d "c:\wamp64\www\ferreteria-app"

echo.
echo === Instalando dependencias principales ===
call npm install --force

echo.
echo === Instalando dependencias del frontend ===
cd src\frontend
call npm install --force

echo.
echo === Iniciando frontend ===
call npm run dev

pause
