@echo off
echo Iniciando Sistema de Ferreteria...
echo.

REM Verificar si Node.js esta instalado
where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Node.js no esta instalado o no esta en el PATH
    echo Por favor, instala Node.js desde https://nodejs.org/
    pause
    exit /b 1
)

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

echo Iniciando aplicacion en modo desarrollo...
echo Frontend: http://localhost:5173
echo Backend: http://localhost:3001
echo.
echo Presiona Ctrl+C para detener la aplicacion
echo.

npm run dev

pause
