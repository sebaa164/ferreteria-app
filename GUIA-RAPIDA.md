# Sistema de Gestión para Ferretería - Guía Rápida

## 🚀 Inicio Rápido

### 1. Verificar estado
```bash
# Ejecutar este archivo para verificar puertos y carpetas
verificar-puertos.bat
```

### 2. Iniciar aplicación
```bash
# Ejecutar este archivo para iniciar todo automáticamente
iniciar-app.bat
```

### 3. Limpiar procesos (si algo falla)
```bash
# Ejecutar para cerrar todos los procesos
limpiar-puertos.bat
```

## 📋 Estructura creada

### ✅ Carpetas creadas:
- `C:\Users\tamar\.ferreteria-app-data\` - Base de datos SQLite
- Estructura del proyecto optimizada

### ✅ Archivos duplicados eliminados:
- package.json de bcrypt (raíz)
- Archivos de configuración duplicados
- Carpeta ferreteria-app vacía

### ✅ Scripts de utilidad:
- `iniciar-app.bat` - Inicia frontend y backend automáticamente
- `verificar-puertos.bat` - Verifica estado de puertos y procesos
- `limpiar-puertos.bat` - Cierra procesos en puertos 3001/5173

## 🔌 Puertos utilizados

- **3001**: Backend Express API
- **5173**: Frontend Vite (solo desarrollo)

## 📁 Datos de la aplicación

**Base de datos**: `C:\Users\tamar\.ferreteria-app-data\ferreteria.db`

## 🔐 Credenciales por defecto

- **Usuario**: admin
- **Contraseña**: admin123

## ⚡ Comandos manuales (si prefieres)

```bash
# Iniciar frontend
cd src/frontend && npm run dev

# Iniciar backend  
nodemon src/backend/server.js

# Iniciar todo junto
npm run dev
```

## 🐛 Solución de problemas

1. **Si los puertos están ocupados**: Ejecuta `limpiar-puertos.bat`
2. **Si Node.js no está instalado**: Descárgalo desde https://nodejs.org/
3. **Si la base de datos no se crea**: Verifica que la carpeta `.ferreteria-app-data` exista
4. **Si el frontend no carga**: Asegúrate de estar en `c:\wamp64\www\ferreteria-app`

## 📞 Soporte

Revisa el archivo `README.md` para más detalles técnicos.
