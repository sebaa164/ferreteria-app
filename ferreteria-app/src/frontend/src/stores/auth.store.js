import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import axios from 'axios';

const API_URL = 'http://localhost:3001/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null);
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'));
  const loading = ref(false);
  const error = ref(null);

  const isAuthenticated = computed(() => !!token.value);
  const isAdmin = computed(() => user.value?.role === 'administrador');

  // Configurar axios con el token
  const setAuthToken = (newToken) => {
    if (newToken) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
      localStorage.setItem('token', newToken);
    } else {
      delete axios.defaults.headers.common['Authorization'];
      localStorage.removeItem('token');
    }
  };

  async function login(username, password) {
    try {
      loading.value = true;
      error.value = null;

      const response = await axios.post(`${API_URL}/auth/login`, {
        username,
        password
      });

      token.value = response.data.token;
      user.value = response.data.user;

      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.user));
      
      setAuthToken(response.data.token);

      return { success: true };
    } catch (err) {
      error.value = err.response?.data?.error || 'Error al iniciar sesión';
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  }

  async function logout() {
    token.value = null;
    user.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setAuthToken(null);
  }

  async function checkAuth() {
    const savedToken = localStorage.getItem('token');
    
    if (!savedToken) {
      return false;
    }

    try {
      setAuthToken(savedToken);
      
      const response = await axios.get(`${API_URL}/auth/verify`);
      
      if (response.data.valid) {
        user.value = response.data.user;
        localStorage.setItem('user', JSON.stringify(response.data.user));
        return true;
      } else {
        logout();
        return false;
      }
    } catch (err) {
      logout();
      return false;
    }
  }

  return {
    token,
    user,
    loading,
    error,
    isAuthenticated,
    isAdmin,
    login,
    logout,
    checkAuth
  };
});