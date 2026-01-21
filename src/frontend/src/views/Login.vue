<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-500 to-primary-700 px-4">
    <div class="max-w-md w-full">
      <div class="bg-white rounded-2xl shadow-2xl p-8">
        <div class="text-center mb-8">
          <div class="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-full mb-4">
            <svg class="w-8 h-8 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path>
            </svg>
          </div>
          <h1 class="text-3xl font-bold text-gray-800">Sistema Ferretería</h1>
          <p class="text-gray-500 mt-2">Inicia sesión para continuar</p>
        </div>

        <form @submit.prevent="handleLogin" class="space-y-6">
          <div v-if="authStore.error" class="bg-red-50 border-l-4 border-red-500 p-4 rounded">
            <div class="flex items-center">
              <svg class="w-5 h-5 text-red-500 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
              </svg>
              <p class="text-sm text-red-700">{{ authStore.error }}</p>
            </div>
          </div>

          <div>
            <label for="username" class="block text-sm font-medium text-gray-700 mb-2">
              Usuario
            </label>
            <input
              id="username"
              v-model="credentials.username"
              type="text"
              required
              class="input-field"
              placeholder="Ingresa tu usuario"
              :disabled="authStore.loading"
            />
          </div>

          <div>
            <label for="password" class="block text-sm font-medium text-gray-700 mb-2">
              Contraseña
            </label>
            <input
              id="password"
              v-model="credentials.password"
              type="password"
              required
              class="input-field"
              placeholder="Ingresa tu contraseña"
              :disabled="authStore.loading"
            />
          </div>

          <button
            type="submit"
            class="w-full btn-primary py-3 text-lg font-semibold"
            :disabled="authStore.loading"
          >
            <span v-if="!authStore.loading">Iniciar Sesión</span>
            <span v-else class="flex items-center justify-center">
              <svg class="animate-spin h-5 w-5 mr-3" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Cargando...
            </span>
          </button>
        </form>

        <div class="mt-6 p-4 bg-gray-50 rounded-lg">
          <p class="text-xs text-gray-600 text-center">
            <strong>Usuario por defecto:</strong> admin<br>
            <strong>Contraseña:</strong> admin123
          </p>
        </div>
      </div>

      <p class="text-center text-white text-sm mt-6 opacity-90">
        Sistema de Gestión v1.0.0
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';

const router = useRouter();
const authStore = useAuthStore();

const credentials = ref({
  username: '',
  password: ''
});

async function handleLogin() {
  const result = await authStore.login(
    credentials.value.username,
    credentials.value.password
  );

  if (result.success) {
    router.push('/dashboard');
  }
}
</script>
