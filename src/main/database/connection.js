const Database = require('better-sqlite3');
const path = require('path');
const os = require('os');
const fs = require('fs');
const bcrypt = require('bcrypt');

let db = null;

function getDatabase() {
  if (db) return db;

  const dataDir = path.join(os.homedir(), '.ferreteria-app-data');
  
  if (!fs.existsSync(dataDir)) {
    fs.mkdirSync(dataDir, { recursive: true });
  }

  const dbPath = path.join(dataDir, 'ferreteria.db');

  try {
    db = new Database(dbPath);
    console.log('Base de datos conectada:', dbPath);
    
    db.pragma('journal_mode = WAL');
    db.pragma('synchronous = NORMAL');
  } catch (err) {
    console.error('Error al abrir la base de datos:', err);
    throw err;
  }

  return db;
}

async function initDatabase() {
  const db = getDatabase();

  const createUsersTable = `
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      username VARCHAR(50) UNIQUE NOT NULL,
      password VARCHAR(255) NOT NULL,
      role VARCHAR(20) DEFAULT 'vendedor',
      full_name VARCHAR(100),
      active BOOLEAN DEFAULT 1,
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
    )
  `;

  try {
    db.exec(createUsersTable);

    const existingUser = db.prepare('SELECT * FROM users WHERE username = ?').get('admin');

    if (!existingUser) {
      const hashedPassword = await bcrypt.hash('admin123', 10);
      
      db.prepare(
        'INSERT INTO users (username, password, role, full_name) VALUES (?, ?, ?, ?)'
      ).run('admin', hashedPassword, 'administrador', 'Administrador');
      
      console.log('Usuario admin creado correctamente');
    } else {
      console.log('Base de datos ya inicializada');
    }
  } catch (err) {
    console.error('Error al inicializar base de datos:', err);
    throw err;
  }
}

module.exports = {
  getDatabase,
  initDatabase
};