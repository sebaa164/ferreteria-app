<<<<<<< HEAD
# Sistema de Gestión para Ferretería

Sistema de punto de venta para ferretería con interfaz gráfica moderna. Incluye autenticación de usuarios, dashboard de estadísticas, gestión de productos e inventario.

## Stack Tecnológico

| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| Java | 17+ | Lenguaje principal |
| JavaFX | 21.0.1 | Framework de interfaz gráfica |
| AtlantaFX | 2.0.1 | Tema moderno para JavaFX |
| SQLite | 3.45.1 | Base de datos embebida |
| Maven | 3.9+ | Gestión de dependencias y build |
| BCrypt | 0.4 | Hash seguro de contraseñas |

## Requisitos del Sistema

### Para desarrollo

| Dependencia | Versión | Descarga |
|-------------|---------|----------|
| JDK | 17+ | [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=17) |
| Maven | 3.9+ | [Apache Maven](https://maven.apache.org/download.cgi) |

### Para generar instaladores

| Dependencia | Plataforma | Descarga |
|-------------|------------|----------|
| WiX Toolset | Windows | [WiX Releases](https://wixtoolset.org/releases/) o `winget install WiXToolset.WiXToolset` |
| dpkg-deb | Linux | Incluido en la mayoría de distribuciones |

### Configuración del PATH

Las dependencias pueden instalarse en cualquier ubicación, pero deben estar accesibles desde la terminal:

**Windows:**
1. Agregar `JAVA_HOME` apuntando a la carpeta del JDK (ej: `C:\Program Files\Eclipse Adoptium\jdk-17`)
2. Agregar al `PATH`:
   - `%JAVA_HOME%\bin`
   - Carpeta `bin` de Maven (ej: `C:\Program Files\Maven\bin`)

**Linux:**
```bash
export JAVA_HOME=/usr/lib/jvm/java-17
export PATH=$JAVA_HOME/bin:$PATH
```

Para verificar la instalación:
```bash
java -version    # Debe mostrar version 17+
mvn -version     # Debe mostrar version 3.9+
```

## Instalación y Ejecución

### Clonar el repositorio
```bash
git clone https://github.com/Areyuna09/Sistema-Ferreteria.git
cd Sistema-Ferreteria
```

### Ejecutar en modo desarrollo
```bash
mvn javafx:run
```

### Compilar JAR
```bash
mvn clean package -DskipTests
```

## Generar Instaladores

### Windows (.exe)
```bash
# Ejecutar el script
build-exe.bat
```
El instalador se genera en: `target/dist/Ferreteria-1.0.0.exe`

### Linux (.deb)
```bash
# Dar permisos y ejecutar
chmod +x build-linux.sh
./build-linux.sh
```
El paquete se genera en: `target/dist/ferreteria_1.0.0_amd64.deb`

Instalar con:
```bash
sudo dpkg -i target/dist/ferreteria_1.0.0_amd64.deb
```

## Estructura del Proyecto

```
src/main/java/com/ferreteria/
├── Main.java                    # Punto de entrada JavaFX
├── Launcher.java                # Launcher para JAR ejecutable
├── application/
│   └── usecases/                # Casos de uso de la aplicación
├── domain/
│   ├── entities/                # Entidades del dominio
│   ├── exceptions/              # Excepciones personalizadas
│   └── repositories/            # Interfaces de repositorios
└── infrastructure/
    ├── persistence/             # Implementación SQLite
    └── ui/                      # Controladores JavaFX

src/main/resources/
├── views/                       # Archivos FXML
├── styles/                      # CSS personalizado
└── icons/                       # Iconos de la aplicación
```

## Credenciales por Defecto

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| admin | admin123 | Administrador |

## Arquitectura

El proyecto sigue los principios de **Clean Architecture**:

- **Domain:** Entidades y reglas de negocio independientes del framework
- **Application:** Casos de uso que orquestan la lógica de negocio
- **Infrastructure:** Implementaciones concretas (UI, persistencia)

## Licencia

© 2025 - Todos los derechos reservados
=======
# 🏪 Sistema de Gestión para Ferretería

Sistema completo de punto de venta y gestión de inventario desarrollado con Electron, Vue 3 y Node.js.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Requisitos del Sistema](#requisitos-del-sistema)
- [Instalación para Desarrollo](#instalación-para-desarrollo)
- [Uso](#uso)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Compilación](#compilación)
- [Tecnologías](#tecnologías)
- [Solución de Problemas](#solución-de-problemas)

---

## ✨ Características

- ✅ **Autenticación de usuarios** con JWT
- ✅ **Dashboard interactivo** con estadísticas
- ✅ **Base de datos SQLite** local y persistente
- ✅ **Backend Express** integrado
- ✅ **Interfaz moderna** con Vue 3 y Tailwind CSS
- ✅ **Ejecutable standalone** (no requiere instalación de dependencias)
- ✅ **Multiplataforma** (Linux y Windows)

### 🚀 Funcionalidades Planificadas

- [ ] Gestión de productos con variantes
- [ ] Control de stock en tiempo real
- [ ] Registro de ventas (efectivo/transferencia)
- [ ] Reportes diarios y mensuales
- [ ] Exportación a PDF/Excel
- [ ] Alertas de stock mínimo
- [ ] Lector de códigos de barras
- [ ] Impresión de tickets

---

## 💻 Requisitos del Sistema

### Para Uso (Solo Ejecutable)

**Linux:**
- Sistema operativo Linux de 64 bits
- No requiere instalación de dependencias

**Windows:**
- Windows 10 o superior (64 bits)
- No requiere instalación de dependencias

### Para Desarrollo

- **Node.js**: v18 o superior
- **NPM**: v9 o superior
- **Git**: Para clonar el repositorio
- **Docker**: (Opcional) Para compilar ejecutable de Windows desde Linux

---

## 🛠️ Instalación para Desarrollo

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd ferreteria-app
```

### 2. Instalar dependencias principales

```bash
npm install
```

Este comando instalará:
- Electron 28
- Express 4.18.2
- Better-sqlite3 11.6.0
- Bcrypt 5.1.1
- JSON Web Token
- Cors
- Nodemon (desarrollo)
- Concurrently (desarrollo)

### 3. Instalar dependencias del frontend

```bash
cd src/frontend
npm install
cd ../..
```

### 4. Reconstruir módulos nativos

```bash
npm rebuild better-sqlite3
```

> **Importante:** Este paso es necesario porque `better-sqlite3` es un módulo nativo que debe compilarse para tu versión de Node.js.

### 5. Ejecutar la aplicación

```bash
npm run dev
```

Esto iniciará el frontend y backend. Si la ventana de Electron no se abre automáticamente, ejecuta en otra terminal:

```bash
npx electron .
```

Deberías ver:
- Frontend corriendo en `http://localhost:5173`
- Backend corriendo en `http://localhost:3001`
- Aplicación Electron abierta

---

## 🎯 Uso

### Credenciales por Defecto

```
Usuario: admin
Contraseña: admin123
Rol: administrador
```

### Iniciar en Modo Desarrollo

```bash
npm run dev
```

Esto iniciará:
1. **Frontend (Vite)**: Puerto 5173
2. **Backend (Express)**: Puerto 3001
3. **Electron**: Ventana de la aplicación

### Detener la Aplicación

Presiona `Ctrl+C` en la terminal

---

## 📁 Estructura del Proyecto

```
ferreteria-app/
│
├── src/
│   ├── main/                          # Proceso principal de Electron
│   │   ├── index.js                  # Configuración de Electron
│   │   ├── preload.js                # Script de seguridad
│   │   └── database/
│   │       └── connection.js         # Conexión a SQLite
│   │
│   ├── backend/                       # API Backend
│   │   ├── server.js                 # Servidor Express
│   │   ├── database/
│   │   │   └── connection.js         # Conexión BD (backend)
│   │   ├── routes/
│   │   │   ├── auth.routes.js        # Rutas de autenticación
│   │   │   └── user.routes.js        # Rutas de usuarios
│   │   ├── controllers/
│   │   │   └── auth.controller.js    # Lógica de autenticación
│   │   └── middleware/
│   │       └── auth.middleware.js    # Verificación de tokens
│   │
│   └── frontend/                      # Frontend Vue
│       ├── src/
│       │   ├── main.js               # Entrada de Vue
│       │   ├── App.vue               # Componente raíz
│       │   ├── router/
│       │   │   └── index.js          # Configuración de rutas
│       │   ├── stores/
│       │   │   └── auth.store.js     # Estado de autenticación
│       │   ├── views/
│       │   │   ├── Login.vue         # Pantalla de login
│       │   │   └── Dashboard.vue     # Panel principal
│       │   ├── components/           # Componentes reutilizables
│       │   └── assets/
│       │       └── styles/
│       │           └── main.css      # Estilos Tailwind
│       ├── index.html
│       ├── vite.config.js
│       ├── tailwind.config.js
│       └── package.json
│
├── build/                            # Ejecutables generados
├── node_modules/                     # Dependencias principales
├── package.json                      # Configuración principal
└── README.md                         # Este archivo
```

### Descripción de Archivos Clave

#### **src/main/index.js**
- Configura la ventana de Electron
- Inicializa la base de datos
- Inicia el backend en modo producción
- Maneja el ciclo de vida de la aplicación

#### **src/main/database/connection.js**
- Crea y gestiona la conexión a SQLite
- Inicializa las tablas
- Crea el usuario admin por defecto
- Ubicación BD: `~/.ferreteria-app-data/ferreteria.db`

#### **src/backend/server.js**
- Servidor Express en el puerto 3001
- Define las rutas de la API
- Maneja CORS para comunicación con el frontend
- Gestiona errores globales

#### **src/backend/controllers/auth.controller.js**
- Maneja login (valida usuario/contraseña)
- Genera tokens JWT
- Verifica tokens para rutas protegidas
- Hash de contraseñas con bcrypt

#### **src/frontend/src/main.js**
- Inicializa Vue 3
- Configura Vue Router
- Configura Pinia (gestión de estado)
- Monta la aplicación en `#app`

#### **src/frontend/src/router/index.js**
- Define las rutas (`/login`, `/dashboard`)
- Protege rutas que requieren autenticación
- Usa `createWebHashHistory()` para compatibilidad con Electron

#### **src/frontend/src/stores/auth.store.js**
- Gestiona el estado de autenticación
- Realiza llamadas a la API de login
- Almacena el token en localStorage
- Verifica sesiones activas

#### **package.json (raíz)**
- Define dependencias del proyecto
- Scripts de desarrollo y compilación
- Configuración de electron-builder
- Metadatos de la aplicación

#### **src/frontend/package.json**
- Dependencias del frontend (Vue, Vite, etc.)
- Scripts de desarrollo y build
- Configuración de Vite

---

## 🔨 Compilación

### Linux (AppImage)

```bash
npm run package:linux
```

**Salida:** `build/Sistema Ferretería-1.0.0.AppImage` (~150-200 MB)

**Ejecutar:**
```bash
chmod +x "build/Sistema Ferretería-1.0.0.AppImage"
"./build/Sistema Ferretería-1.0.0.AppImage"
```

### Windows (Portable)

Desde Linux usando Docker:

```bash
docker run --rm -ti \
  -v ${PWD}:/project \
  electronuserland/builder:wine \
  /bin/bash -c "cd /project && npm run package:windows"
```

**Salida:** `build/Sistema Ferretería 1.0.0.exe` (~150-200 MB)

**Nota:** El ejecutable es portable, no requiere instalación.

### Limpiar builds anteriores

```bash
rm -rf build
rm -rf src/frontend/dist
```

---

## 🔧 Tecnologías

### Frontend
- **Vue 3** - Framework JavaScript progresivo
- **Vue Router 4** - Enrutamiento SPA
- **Pinia** - Gestión de estado
- **Tailwind CSS 3** - Framework de utilidades CSS
- **Vite** - Build tool ultra rápido
- **Axios** - Cliente HTTP

### Backend
- **Express 4** - Framework web para Node.js
- **Better-SQLite3** - Base de datos SQL embebida
- **JSON Web Token** - Autenticación basada en tokens
- **Bcrypt** - Hash seguro de contraseñas
- **CORS** - Control de acceso entre orígenes

### Desktop
- **Electron 28** - Framework para aplicaciones de escritorio

---

## 🐛 Solución de Problemas

### Error: "Cannot find module 'better-sqlite3'" o "NODE_MODULE_VERSION"

Este error ocurre cuando `better-sqlite3` está compilado para una versión diferente de Node.js.

**Solución:**
```bash
npm rebuild better-sqlite3
```

**Si el error menciona NODE_MODULE_VERSION:**
```bash
# Limpiar y reconstruir
rm -rf node_modules/better-sqlite3/build
npm rebuild better-sqlite3
```

### Error: "Puerto 3001 en uso"

Matar el proceso:
```bash
fuser -k 3001/tcp
# o
killall node
```

### Error: "Frontend no carga en el ejecutable"

Verificar que se compiló correctamente:
```bash
ls -la src/frontend/dist/
cat src/frontend/dist/index.html
```

Debe tener rutas relativas (`./assets/...`)

### Base de datos corrupta

Eliminar y reiniciar:
```bash
rm -rf ~/.ferreteria-app-data/ferreteria.db
# Reiniciar la aplicación
```

Se creará nuevamente con el usuario admin por defecto.

### Error al compilar para Windows desde Linux

Verificar que Docker esté corriendo:
```bash
sudo systemctl start docker
sudo systemctl status docker
```

Agregar usuario al grupo docker:
```bash
sudo usermod -aG docker $USER
# Cerrar sesión y volver a entrar
```

### Ejecutable muy grande

El tamaño (~150-200 MB) es normal porque incluye:
- Electron runtime
- Chromium
- Node.js
- Todos los node_modules
- Frontend compilado

Para reducir el tamaño, se puede:
1. Usar `asar` más agresivamente
2. Excluir dev dependencies
3. Usar tree-shaking más agresivo

### Error: "dragEvent is not defined"

Ya resuelto. El router usa `createWebHashHistory()` en lugar de `createWebHistory()`.

---

## 📊 Base de Datos

### Ubicación

**Linux:** `~/.ferreteria-app-data/ferreteria.db`  
**Windows:** `C:\Users\<Usuario>\AppData\Roaming\ferreteria-app\ferreteria.db`

### Tablas Actuales

#### **users**
```sql
CREATE TABLE users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,        -- Hash bcrypt
  role VARCHAR(20) DEFAULT 'vendedor',
  full_name VARCHAR(100),
  active BOOLEAN DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
)
```

### Respaldo de la Base de Datos

```bash
# Copiar el archivo
cp ~/.ferreteria-app-data/ferreteria.db ~/backup-ferreteria-$(date +%Y%m%d).db
```

---

## 🔐 Seguridad

- ✅ Contraseñas hasheadas con bcrypt (salt rounds: 10)
- ✅ Autenticación JWT con expiración de 8 horas
- ✅ Context isolation en Electron
- ✅ No hay nodeIntegration en el renderer
- ✅ CORS configurado solo para localhost
- ⚠️ Cambiar JWT_SECRET en producción

---

## 📝 Próximos Pasos para Desarrollo

### Prioridad Alta
1. Módulo de Productos
   - CRUD completo
   - Gestión de variantes (medidas, colores, etc.)
   - Ubicación física en el local
   - Stock mínimo con alertas

2. Módulo de Ventas
   - Carrito de compras
   - Métodos de pago (efectivo/transferencia)
   - Generación de tickets
   - Historial de ventas

### Prioridad Media
3. Reportes
   - Ventas diarias/mensuales
   - Productos más vendidos
   - Stock actual
   - Exportación a PDF/Excel

4. Mejoras UX
   - Lector de códigos de barras
   - Búsqueda rápida de productos
   - Atajos de teclado
   - Modo oscuro

### Prioridad Baja
5. Características Avanzadas
   - Gestión de proveedores
   - Órdenes de compra
   - Múltiples usuarios
   - Backup automático

---

## 👥 Contribuir

1. Fork el proyecto
2. Crear una rama (`git checkout -b feature/nueva-funcionalidad`)
3. Commit los cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear un Pull Request

---

## 📄 Licencia

MIT License - Ver archivo LICENSE para más detalles

---

## 📞 Soporte

Para problemas o consultas:
- **Email**: dev@ferreteria.com
- **Issues**: GitHub Issues del repositorio

---

## 🙏 Agradecimientos

Desarrollado para facilitar la gestión diaria de ferreterías pequeñas y medianas.

**Versión:** 1.0.0  
**Fecha:** Enero 2026  
**Estado:** Beta - Login y Dashboard funcionales
>>>>>>> fea66c06f47bb9def1c7182488943f233513cb2b
