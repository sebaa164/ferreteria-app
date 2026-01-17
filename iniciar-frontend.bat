@echo off
echo Iniciando Frontend del Sistema de Ferreteria...
echo.

REM Configurar PATH
set PATH=C:\Program Files\nodejs;%PATH%

REM Ir al directorio del frontend
cd /d "c:\wamp64\www\ferreteria-app\src\frontend"

echo Directorio actual: %CD%
echo Node.js:
node --version
echo NPM:
npm --version

echo.
echo Instalando dependencias del frontend...
call npm install

echo.
echo Iniciando servidor de desarrollo...
echo El frontend estara disponible en: http://localhost:5173
echo Presiona Ctrl+C para detener
echo.

call npm run dev

pause
