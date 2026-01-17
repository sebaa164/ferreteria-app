const express = require('express');
const router = express.Router();
const authMiddleware = require('../middleware/auth.middleware');
const { getDatabase } = require('../database/connection');

router.use(authMiddleware);

router.get('/', (req, res) => {
  if (req.user.role !== 'administrador') {
    return res.status(403).json({ error: 'Acceso denegado' });
  }

  const db = getDatabase();
  const users = db.prepare(
    'SELECT id, username, role, full_name, active, created_at FROM users'
  ).all();

  res.json({ users });
});

module.exports = router;