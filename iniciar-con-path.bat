@echo off
echo Configurando entorno para Sistema de Ferreteria...
echo.

REM Agregar Node.js al PATH temporalmente
set PATH=C:\Program Files\nodejs;%PATH%

REM Verificar Node.js
echo Node.js version:
node --version

echo NPM version:
npm --version

REM Reconstruir better-sqlite3
echo.
echo Reconstruyendo better-sqlite3...
npm rebuild better-sqlite3

REM Iniciar aplicacion
echo.
echo Iniciando Sistema de Ferreteria...
echo Frontend: http://localhost:5173
echo Backend: http://localhost:3001
echo.

npm run dev

pause
