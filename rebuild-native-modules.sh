#!/bin/bash
echo "=== RECONSTRUYENDO MÓDULOS NATIVOS PARA ELECTRON ==="

# Eliminar node_modules existentes de módulos nativos
echo "1. Limpiando módulos nativos existentes..."
rm -rf node_modules/better-sqlite3
rm -rf node_modules/bcrypt

# Reinstalar módulos nativos
echo "2. Reinstalando módulos nativos..."
npm install better-sqlite3@11.6.0
npm install bcrypt@5.1.1

# Reconstruir para Electron usando electron-rebuild
echo "3. Reconstruyendo para Electron v28.3.3..."
npx electron-rebuild -f

# Verificar la reconstrucción
echo "4. Verificando módulos reconstruidos..."
if [ -f "node_modules/better-sqlite3/build/Release/better_sqlite3.node" ]; then
  echo "✅ better-sqlite3 reconstruido correctamente"
else
  echo "⚠️  better-sqlite3: buscando archivo alternativo..."
  find node_modules/better-sqlite3 -name "*.node" -type f
fi

echo "Buscando archivo bcrypt..."
find node_modules/bcrypt -name "*.node" -type f

# Verificar si bcrypt está en una ruta diferente
BCRYPT_PATH=$(find node_modules/bcrypt -name "bcrypt_lib.node" -type f 2>/dev/null || find node_modules/bcrypt -name "*.node" -type f | head -1)
if [ -n "$BCRYPT_PATH" ] && [ -f "$BCRYPT_PATH" ]; then
  echo "✅ bcrypt encontrado en: $BCRYPT_PATH"
else
  echo "⚠️  bcrypt no encontrado, intentando reconstrucción manual..."
  cd node_modules/bcrypt
  npm run install 2>/dev/null || node-gyp rebuild 2>/dev/null || echo "Falló reconstrucción manual"
  cd ../..
fi

echo "✅ Proceso completado"