const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const { getDatabase } = require('../database/connection');

const JWT_SECRET = 'tu-clave-secreta-super-segura-cambiala-en-produccion';

const authController = {
  async login(req, res) {
    try {
      const { username, password } = req.body;

      if (!username || !password) {
        return res.status(400).json({ 
          error: 'Usuario y contraseña son requeridos' 
        });
      }

      const db = getDatabase();
      
      const user = db.prepare(
        'SELECT * FROM users WHERE username = ? AND active = 1'
      ).get(username);

      if (!user) {
        return res.status(401).json({ 
          error: 'Usuario o contraseña incorrectos' 
        });
      }

      const validPassword = await bcrypt.compare(password, user.password);

      if (!validPassword) {
        return res.status(401).json({ 
          error: 'Usuario o contraseña incorrectos' 
        });
      }

      const token = jwt.sign(
        { 
          id: user.id, 
          username: user.username, 
          role: user.role 
        },
        JWT_SECRET,
        { expiresIn: '8h' }
      );

      const { password: _, ...userWithoutPassword } = user;

      res.json({
        message: 'Login exitoso',
        token,
        user: userWithoutPassword
      });
    } catch (error) {
      console.error('Error en login:', error);
      res.status(500).json({ error: 'Error en el servidor' });
    }
  },

  logout(req, res) {
    res.json({ message: 'Logout exitoso' });
  },

  verifyToken(req, res) {
    try {
      const token = req.headers.authorization?.split(' ')[1];

      if (!token) {
        return res.status(401).json({ error: 'No autorizado' });
      }

      const decoded = jwt.verify(token, JWT_SECRET);
      const db = getDatabase();
      
      const user = db.prepare(
        'SELECT id, username, role, full_name FROM users WHERE id = ? AND active = 1'
      ).get(decoded.id);

      if (!user) {
        return res.status(401).json({ error: 'No autorizado' });
      }

      res.json({ valid: true, user });
    } catch (error) {
      res.status(401).json({ error: 'Token inválido' });
    }
  }
};

module.exports = authController;