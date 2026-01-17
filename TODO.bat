@echo off
echo Iniciando Sistema Ferreteria...
set PATH=C:\Program Files\nodejs;%PATH%
cd c:\wamp64\www\ferreteria-app
start "Backend" cmd /k "node src/backend/server.js"
timeout /t 3
cd src\frontend
start "Frontend" cmd /k "npm run dev"
timeout /t 5
start http://localhost:5173
