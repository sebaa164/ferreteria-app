const { app, BrowserWindow } = require('electron');
const path = require('path');
const { initDatabase } = require('./database/connection');
const { fork } = require('child_process');
const fs = require('fs');

let mainWindow;
let backendProcess;
let isBackendStarted = false;

function getBackendPath() {
  const isDev = !app.isPackaged;
  
  if (isDev) {
    return path.join(__dirname, '../backend/server.js');
  }
  
  // En producción, buscar en extraResources (desempaquetado)
  const backendPath = path.join(
    process.resourcesPath,
    'backend',
    'server.js'
  );
  
  console.log('🔍 Buscando backend en:', backendPath);
  
  if (fs.existsSync(backendPath)) {
    console.log('✅ Backend encontrado');
    return backendPath;
  }
  
  throw new Error(`No se pudo encontrar el backend en: ${backendPath}`);
}

function startBackend() {
  if (isBackendStarted) {
    console.log('Backend ya está iniciado');
    return;
  }
  
  try {
    const backendPath = getBackendPath();
    console.log('🚀 Iniciando backend desde:', backendPath);
    
    backendProcess = fork(backendPath, [], {
      stdio: ['pipe', 'pipe', 'pipe', 'ipc'],
      env: { 
        ...process.env, 
        NODE_ENV: 'production',
        PORT: 3001,
        // Pasar rutas importantes al backend
        APP_PATH: app.getAppPath(),
        RESOURCES_PATH: process.resourcesPath,
        USER_DATA: app.getPath('userData')
      },
      cwd: path.dirname(backendPath)
    });

    backendProcess.stdout.on('data', (data) => {
      console.log(`📡 Backend: ${data}`);
    });

    backendProcess.stderr.on('data', (data) => {
      console.error(`🔴 Backend error: ${data}`);
    });

    backendProcess.on('error', (err) => {
      console.error('❌ Error al iniciar backend:', err);
    });

    backendProcess.on('exit', (code, signal) => {
      console.log(`🔴 Backend cerrado - Código: ${code}, Señal: ${signal}`);
      isBackendStarted = false;
    });

    backendProcess.on('message', (message) => {
      console.log('📨 Mensaje del backend:', message);
    });
    
    isBackendStarted = true;
    console.log('✅ Backend iniciado con PID:', backendProcess.pid);
    
  } catch (err) {
    console.error('❌ Error crítico al iniciar backend:', err);
    throw err;
  }
}

async function waitForBackend(maxAttempts = 30) {
  console.log('⏳ Esperando que el backend esté listo...');
  
  for (let i = 0; i < maxAttempts; i++) {
    try {
      const http = require('http');
      await new Promise((resolve, reject) => {
        const req = http.get({
          hostname: '127.0.0.1',
          port: 3001,
          path: '/api/health',
          timeout: 2000
        }, (res) => {
          if (res.statusCode === 200) {
            console.log('✅ Backend listo!');
            resolve();
          } else {
            reject(new Error(`Status: ${res.statusCode}`));
          }
        });
        req.on('error', reject);
        req.on('timeout', () => {
          req.destroy();
          reject(new Error('Timeout'));
        });
      });
      return true;
    } catch (err) {
      console.log(`⏳ Esperando backend... (${i + 1}/${maxAttempts})`);
      await new Promise(resolve => setTimeout(resolve, 1000));
    }
  }
  throw new Error('Backend no se inició a tiempo');
}

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 800,
    minWidth: 1024,
    minHeight: 600,
    show: false,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      nodeIntegration: false,
      contextIsolation: true,
      webSecurity: !app.isPackaged
    }
  });

  mainWindow.once('ready-to-show', () => {
    mainWindow.show();
    if (!app.isPackaged) {
      mainWindow.webContents.openDevTools();
    }
  });

  const isDev = !app.isPackaged;

  if (isDev) {
    mainWindow.loadURL('http://localhost:5173');
  } else {
    const indexPath = path.join(__dirname, '..', 'frontend', 'dist', 'index.html');
    console.log('📁 Cargando desde:', indexPath);
    
    if (fs.existsSync(indexPath)) {
      console.log('✅ Archivo encontrado');
      mainWindow.loadFile(indexPath);
    } else {
      const errorHtml = `
        <html>
          <body style="padding: 20px; font-family: Arial;">
            <h1>❌ Error de aplicación</h1>
            <p>No se pudo cargar la interfaz.</p>
            <p>Ruta buscada: ${indexPath}</p>
            <p>Verifica la construcción del frontend.</p>
          </body>
        </html>
      `;
      mainWindow.loadURL(`data:text/html,${encodeURIComponent(errorHtml)}`);
    }
  }

  mainWindow.on('closed', () => {
    mainWindow = null;
  });
}

app.whenReady().then(async () => {
  console.log('🚀 App iniciando...');
  console.log('📦 isPackaged:', app.isPackaged);
  console.log('📁 App path:', app.getAppPath());
  console.log('💾 User data:', app.getPath('userData'));
  console.log('🔧 Resources path:', process.resourcesPath);
  
  try {
    // Iniciar backend SOLO en producción
    if (app.isPackaged) {
      // Inicializar base de datos solo en producción (evita conflicto de versiones Node/Electron)
      console.log('🔄 Inicializando base de datos...');
      await initDatabase();
      console.log('✅ Base de datos inicializada');
      if (!isBackendStarted) {
        console.log('🚀 Iniciando backend en modo producción...');
        startBackend();
        await waitForBackend();
        console.log('✅ Backend completamente iniciado');
      }
    } else {
      console.log('⚡ Modo desarrollo: backend se inicia automáticamente');
    }
    
    createWindow();
  } catch (err) {
    console.error('❌ Error crítico al iniciar:', err);
    console.error(err.stack);
    
    // Mostrar ventana de error
    const errorWindow = new BrowserWindow({
      width: 600,
      height: 400,
      show: true,
      webPreferences: {
        nodeIntegration: true
      }
    });
    
    const errorHtml = `
      <html>
        <body style="padding: 20px; font-family: Arial;">
          <h1>❌ Error al iniciar la aplicación</h1>
          <p><strong>Error:</strong> ${err.message}</p>
          <p><strong>Stack:</strong></p>
          <pre style="background: #f0f0f0; padding: 10px; overflow: auto;">${err.stack}</pre>
          <p>Verifica los logs para más detalles.</p>
        </body>
      </html>
    `;
    
    errorWindow.loadURL(`data:text/html,${encodeURIComponent(errorHtml)}`);
  }

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on('window-all-closed', () => {
  console.log('🔴 Cerrando aplicación...');
  if (backendProcess) {
    console.log('🛑 Deteniendo backend...');
    backendProcess.kill('SIGTERM');
  }
  
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('before-quit', () => {
  console.log('🛑 Before-quit: deteniendo procesos...');
  if (backendProcess) {
    console.log('🛑 Deteniendo backend (before-quit)...');
    backendProcess.kill('SIGTERM');
  }
});