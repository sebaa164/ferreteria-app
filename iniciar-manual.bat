@echo off
echo Iniciando Sistema de Ferreteria (Modo Manual)...
echo.

REM Configurar PATH
set PATH=C:\Program Files\nodejs;%PATH%

REM Cambiar a directorio del proyecto
cd /d "c:\wamp64\www\ferreteria-app"

echo Reconstruyendo modulos nativos...
call npm rebuild better-sqlite3

echo.
echo Iniciando backend en puerto 3001...
start "Backend" cmd /k "cd /d c:\wamp64\www\ferreteria-app && set PATH=C:\Program Files\nodejs;%PATH% && node src/backend/server.js"

timeout /t 3

echo Iniciando frontend en puerto 5173...
start "Frontend" cmd /k "cd /d c:\wamp64\www\ferreteria-app\src\frontend && set PATH=C:\Program Files\nodejs;%PATH% && npm run dev"

timeout /t 5

echo.
echo Abriendo navegador...
start http://localhost:5173

echo.
echo Sistema iniciado!
echo - Backend: http://localhost:3001
echo - Frontend: http://localhost:5173
echo.
echo Presiona cualquier tecla para salir...
pause
