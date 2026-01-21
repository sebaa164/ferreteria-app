@echo off
echo ========================================
echo  FERRETERIA - Verificacion de Puertos
echo ========================================
echo.

echo Verificando puertos comunes que podria usar la aplicacion...
echo.

echo Puerto 8080 (servidor web):
netstat -an | findstr :8080

echo.
echo Puerto 3000 (servidor desarrollo):
netstat -an | findstr :3000

echo.
echo Puerto 5432 (base de datos):
netstat -an | findstr :5432

echo.
echo Puerto 3306 (MySQL):
netstat -an | findstr :3306

echo.
echo Procesos Java en ejecucion:
tasklist | findstr java

echo.
echo Proceso JavaFX (puede usar puertos dinamicos):
tasklist | findstr javaw

pause
