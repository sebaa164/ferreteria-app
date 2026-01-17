const jwt = require('jsonwebtoken');

const JWT_SECRET = 'tu-clave-secreta-super-segura-cambiala-en-produccion';

function authMiddleware(req, res, next) {
  try {
    const token = req.headers.authorization?.split(' ')[1];

    if (!token) {
      return res.status(401).json({ error: 'No autorizado - Token no proporcionado' });
    }

    const decoded = jwt.verify(token, JWT_SECRET);
    req.user = decoded;
    next();
  } catch (error) {
    return res.status(401).json({ error: 'No autorizado - Token inválido' });
  }
}

module.exports = authMiddleware;
