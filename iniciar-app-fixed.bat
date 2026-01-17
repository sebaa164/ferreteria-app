@echo off
echo Iniciando Sistema de Ferreteria...
echo.

REM Usar ruta completa de Node.js
set NODE_EXE="C:\Program Files\nodejs\node.exe"
set NPM_CMD="C:\Program Files\nodejs\npm.cmd"

REM Verificar Node.js
if not exist %NODE_EXE% (
    echo ERROR: Node.js no encontrado en C:\Program Files\nodejs\
    echo Por favor, reinstala Node.js desde https://nodejs.org/
    pause
    exit /b 1
)

echo Node.js encontrado: 
%NODE_EXE% --version

echo NPM encontrado:
%NPM_CMD% --version

REM Verificar puerto 3001
netstat -ano | findstr :3001 >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo ADVERTENCIA: El puerto 3001 ya esta en uso
    echo Cerrando procesos en el puerto 3001...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3001') do taskkill /F /PID %%a 2>nul
    timeout /t 2 >nul
)

REM Verificar puerto 5173
netstat -ano | findstr :5173 >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo ADVERTENCIA: El puerto 5173 ya esta en uso
    echo Cerrando procesos en el puerto 5173...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5173') do taskkill /F /PID %%a 2>nul
    timeout /t 2 >nul
)

echo.
echo Iniciando aplicacion en modo desarrollo...
echo Frontend: http://localhost:5173
echo Backend: http://localhost:3001
echo.
echo Presiona Ctrl+C para detener la aplicacion
echo.

%NPM_CMD% run dev

pause
