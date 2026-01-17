@echo off
echo Iniciando Backend del Sistema de Ferreteria...
echo.

REM Configurar PATH
set PATH=C:\Program Files\nodejs;%PATH%

REM Ir al directorio del proyecto
cd /d "c:\wamp64\www\ferreteria-app"

echo Directorio actual: %CD%
echo Node.js:
node --version

echo.
echo Reconstruyendo better-sqlite3...
call npm rebuild better-sqlite3

echo.
echo Iniciando servidor backend...
echo El backend estara disponible en: http://localhost:3001
echo Presiona Ctrl+C para detener
echo.

call node src/backend/server.js

pause
