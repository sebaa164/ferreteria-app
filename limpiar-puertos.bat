@echo off
echo Limpiando Sistema de Ferreteria...
echo ==============================
echo.

echo Cerrando procesos en puerto 3001...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3001 2^>nul') do (
    echo Cerrando proceso PID: %%a
    taskkill /F /PID %%a 2>nul
)

echo Cerrando procesos en puerto 5173...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5173 2^>nul') do (
    echo Cerrando proceso PID: %%a
    taskkill /F /PID %%a 2>nul
)

echo Cerrando procesos Node.js...
taskkill /F /IM node.exe 2>nul

echo.
echo Limpieza completada!
echo.
pause
