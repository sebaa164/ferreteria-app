const fs = require('fs');
const path = require('path');

console.log('=== VERIFICACIÓN DE ESTRUCTURA ===\n');

const pathsToCheck = [
  'src/main/index.js',
  'src/main/preload.js',
  'src/main/database/connection.js',
  'src/frontend/dist/index.html',
  'src/backend/server.js',
  'src/backend/database/connection.js'
];

pathsToCheck.forEach(filePath => {
  const fullPath = path.join(__dirname, filePath);
  const exists = fs.existsSync(fullPath);
  console.log(`${exists ? '✅' : '❌'} ${filePath}`);
  if (!exists && filePath.includes('dist')) {
    console.log('   ⚠️  Ejecuta: npm run build:frontend');
  }
});

console.log('\n=== VERIFICACIÓN DE PACKAGE.JSON ===\n');

const mainPackage = require('./package.json');
console.log('📦 Main package:', mainPackage.name, mainPackage.version);
console.log('🎯 Main entry:', mainPackage.main);

if (fs.existsSync('./src/frontend/package.json')) {
  const frontendPackage = require('./src/frontend/package.json');
  console.log('🎨 Frontend package:', frontendPackage.name, frontendPackage.version);
}