# 📋 Manual de Uso - Sistema Ferretería

## 🚀 INICIO RÁPIDO

### 1. Iniciar la Aplicación
```bash
# Opción 1: Usar el script automatizado
.\run.bat

# Opción 2: Manual con Maven
mvn clean javafx:run

# Opción 3: Usar el panel de control
.\control.bat
```

### 2. Acceso por Defecto
- **Usuario:** `admin`
- **Contraseña:** `admin123`

---

## 🔧 CONTROL DE PUERTOS Y PROCESOS

### Verificar Puertos en Uso
```bash
.\verificar-puertos.bat
```

### Limpiar Procesos Java (si algo se bloquea)
```bash
# Opción 1: Script automatizado
.\control.bat  (opción 4)

# Opción 2: Manual
taskkill /F /IM java.exe
```

### Verificar Base de Datos
```bash
.\control.bat  (opción 3)
```

---

## 🖥️ VISTAS DE LA APLICACIÓN

### 1. Login (Login.fxml)
- **Usuario:** admin / admin123
- Diseño moderno con branding izquierdo
- Formulario de acceso a la derecha

### 2. Dashboard Principal
- Navegación superior con logo
- Menú: Dashboard, Productos, Ventas, Reportes, Usuarios
- Info del usuario y logout

### 3. Vista de Productos (Products.fxml)
- **Tabla de inventario** con:
  - Código, Producto, Categoría, Marca
  - Stock, Precio, Stock Mínimo, Acciones
- **Filtros:**
  - Búsqueda por nombre/código/categoría
  - Filtro por categoría
  - Filtro por stock
- **Estadísticas en tiempo real:**
  - Total de productos
  - Stock bajo
  - Sin stock
  - Valor total del inventario

---

## 📊 BASE DE DATOS

### Ubicación
```
C:\Users\tu_usuario\.ferreteria-java-data\ferreteria.db
```

### Tablas Principales
- `users` - Usuarios del sistema
- `products` - Inventario de productos
- `sales` - Ventas realizadas
- `sale_items` - Detalles de ventas

### Productos de Ejemplo
- Herramientas (Bosch, Stanley, DeWalt)
- Electricidad (Phoenix, Schneider, Siemens)
- Fontanería (Roto, Tigre)
- Construcción (Loma Negra, Cerámica)
- Pintura (Sinteplast)
- Fijaciones (Cemaco)

---

## 🛠️ COMANDOS MANUALES

### Compilar y Ejecutar
```bash
# Limpiar y compilar
mvn clean compile

# Ejecutar aplicación JavaFX
mvn javafx:run

# Crear ejecutable .exe
mvn clean package -P windows
```

### Verificar Estado
```bash
# Ver procesos Java
tasklist | findstr java

# Ver puertos
netstat -an | findstr LISTENING

# Ver base de datos
sqlite3 "%USERPROFILE%\.ferreteria-java-data\ferreteria.db" ".tables"
```

---

## 🎯 FLUJO DE TRABAJO TÍPICO

1. **Iniciar aplicación** con `.\run.bat`
2. **Login** con credenciales admin/admin123
3. **Dashboard** - Vista general del sistema
4. **Productos** - Gestionar inventario
5. **Ventas** - Registrar ventas
6. **Reportes** - Ver estadísticas

---

## 🚨 SOLUCIÓN DE PROBLEMAS

### Si la aplicación no inicia:
1. Verificar Java 17 instalado: `java -version`
2. Verificar Maven: `mvn -version`
3. Limpiar procesos: `taskkill /F /IM java.exe`
4. Eliminar base de datos: `Remove-Item "$env:USERPROFILE\.ferreteria-java-data" -Recurse -Force`

### Si hay errores de base de datos:
1. Detener aplicación
2. Eliminar carpeta `.ferreteria-java-data`
3. Reiniciar aplicación (se creará automáticamente)

### Si la interfaz no responde:
1. Verificar si hay procesos Java bloqueados
2. Reiniciar con `.\control.bat` opción 4

---

## 📁 ESTRUCTURA IMPORTANTE

```
Sistema-Ferreteria-main/
├── src/main/resources/
│   ├── views/          # Vistas FXML
│   │   ├── Login.fxml
│   │   ├── Products.fxml
│   │   └── ...
│   └── styles/         # CSS
│       └── main.css
├── src/main/java/      # Controladores Java
├── database_setup.sql  # Script de base de datos
├── run.bat            # Script de inicio
├── control.bat        # Panel de control
└── verificar-puertos.bat # Verificación de puertos
```

---

## 💡 TIPS ÚTILES

- **Ctrl+C** en la terminal detiene la aplicación
- La base de datos se crea automáticamente al primer inicio
- Los cambios en los archivos .fxml requieren reiniciar la aplicación
- Usa `.\control.bat` para gestión fácil del sistema
