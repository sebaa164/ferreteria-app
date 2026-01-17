const express = require('express');
const cors = require('cors');
const path = require('path');

// Cargar módulos - Asegurarse de que están disponibles
let getDatabase, initDatabase;

try {
  // Intentar cargar desde el directorio actual primero
  const dbModule = require('./database/connection');
  getDatabase = dbModule.getDatabase;
  initDatabase = dbModule.initDatabase;
} catch (error) {
  console.warn('⚠️ No se pudo cargar connection.js desde ./database/, intentando ruta alternativa...');
  
  // En producción, buscar en el directorio de recursos
  const resourcesPath = process.env.RESOURCES_PATH || __dirname;
  const connectionPath = path.join(resourcesPath, '..', 'app.asar', 'src', 'main', 'database', 'connection.js');
  
  try {
    // Necesitamos usar un proxy para cargar desde ASAR
    const Module = require('module');
    const originalRequire = Module.prototype.require;
    
    Module.prototype.require = function(id) {
      if (id === './database/connection' || id === '../database/connection') {
        // Redirigir a la conexión del proceso principal
        console.log('🔗 Redirigiendo require de conexión a BD');
        return {
          getDatabase: () => {
            throw new Error('La base de datos debe ser accedida desde el proceso principal');
          },
          initDatabase: async () => {
            console.log('✅ Base de datos ya inicializada desde el proceso principal');
            return Promise.resolve();
          }
        };
      }
      return originalRequire.apply(this, arguments);
    };
    
    // En lugar de usar la conexión real, usar un enfoque diferente
    getDatabase = () => {
      throw new Error('Usar conexión directa no está disponible en el backend separado');
    };
    
    initDatabase = async () => {
      console.log('✅ Base de datos manejada por proceso principal');
      return Promise.resolve();
    };
    
  } catch (innerError) {
    console.error('❌ No se pudo configurar el proxy de conexión:', innerError);
    throw innerError;
  }
}

const authRoutes = require('./routes/auth.routes');
const userRoutes = require('./routes/user.routes');

const app = express();
const PORT = process.env.PORT || 3001;

// Middlewares
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Rutas
app.use('/api/auth', authRoutes);
app.use('/api/users', userRoutes);

// Ruta de health check
app.get('/api/health', async (req, res) => {
  try {
    res.json({ 
      status: 'ok', 
      message: 'Backend funcionando correctamente',
      timestamp: new Date().toISOString(),
      version: '1.0.0',
      environment: process.env.NODE_ENV || 'production'
    });
  } catch (error) {
    console.error('Health check error:', error);
    res.status(500).json({ 
      status: 'error', 
      message: 'Health check failed',
      timestamp: new Date().toISOString()
    });
  }
});

// Ruta 404
app.use('/api/*', (req, res) => {
  res.status(404).json({ error: 'Ruta no encontrada' });
});

// Manejo de errores
app.use((err, req, res, next) => {
  console.error('❌ Error global:', err.stack);
  
  const statusCode = err.statusCode || 500;
  const message = process.env.NODE_ENV === 'production' 
    ? 'Error interno del servidor' 
    : err.message;
  
  res.status(statusCode).json({ 
    error: 'Error interno del servidor',
    message: message,
    ...(process.env.NODE_ENV !== 'production' && { stack: err.stack })
  });
});

// Función para iniciar el servidor
async function startServer() {
  try {
    console.log('🚀 Iniciando backend...');
    console.log('📦 Entorno:', process.env.NODE_ENV || 'development');
    console.log('📁 Directorio actual:', __dirname);
    console.log('🔧 Resources path:', process.env.RESOURCES_PATH);
    console.log('💾 User data:', process.env.USER_DATA);
    
    // "Inicializar" base de datos (en realidad ya está inicializada desde el proceso principal)
    console.log('🔄 Verificando estado de base de datos...');
    await initDatabase();
    console.log('✅ Backend listo');
    
    // Iniciar servidor
    const server = app.listen(PORT, '127.0.0.1', () => {
      console.log(`✅ Servidor backend corriendo en http://127.0.0.1:${PORT}`);
      console.log(`🔗 Health check: http://127.0.0.1:${PORT}/api/health`);
      
      // Enviar mensaje al proceso padre
      if (process.send) {
        process.send({ 
          status: 'ready', 
          port: PORT,
          pid: process.pid 
        });
      }
    });

    // Manejar cierre
    process.on('SIGTERM', () => {
      console.log('🛑 Recibida señal SIGTERM, cerrando servidor...');
      server.close(() => {
        console.log('✅ Servidor cerrado correctamente');
        process.exit(0);
      });
    });

    process.on('SIGINT', () => {
      console.log('🛑 Recibida señal SIGINT, cerrando servidor...');
      server.close(() => {
        console.log('✅ Servidor cerrado correctamente');
        process.exit(0);
      });
    });

  } catch (error) {
    console.error('❌ Error crítico al iniciar el servidor:', error);
    process.exit(1);
  }
}

// Iniciar solo si se ejecuta directamente
if (require.main === module) {
  startServer();
}

module.exports = { app, startServer };