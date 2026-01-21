<template>
  <div class="min-h-screen bg-gray-100">
    <!-- Navbar -->
    <nav class="bg-white shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <div class="flex-shrink-0 flex items-center">
              <svg class="w-8 h-8 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path>
              </svg>
              <h1 class="ml-3 text-xl font-bold text-gray-800">Sistema Ferretería</h1>
            </div>
          </div>
          
          <div class="flex items-center space-x-4">
            <div class="flex items-center space-x-3">
              <div class="text-right">
                <p class="text-sm font-medium text-gray-700">{{ authStore.user?.full_name }}</p>
                <p class="text-xs text-gray-500 capitalize">{{ authStore.user?.role }}</p>
              </div>
              <div class="w-10 h-10 bg-primary-100 rounded-full flex items-center justify-center">
                <span class="text-primary-600 font-semibold text-sm">
                  {{ getUserInitials() }}
                </span>
              </div>
            </div>
            
            <button
              @click="handleLogout"
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-red-600 hover:bg-red-700 transition-colors"
            >
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
              </svg>
              Cerrar Sesión
            </button>
          </div>
        </div>
      </div>
    </nav>

    <!-- Contenido Principal -->
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Encabezado -->
      <div class="px-4 py-6 sm:px-0">
        <h2 class="text-3xl font-bold text-gray-900 mb-2">Dashboard</h2>
        <p class="text-gray-600">Bienvenido al sistema de gestión de tu ferretería</p>
      </div>

      <!-- Tarjetas de Estadísticas -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 px-4 sm:px-0 mb-8">
        <div class="card bg-gradient-to-br from-blue-500 to-blue-600 text-white">
          <div class="flex justify-between items-start">
            <div>
              <p class="text-blue-100 text-sm font-medium">Productos</p>
              <h3 class="text-3xl font-bold mt-2">0</h3>
            </div>
            <div class="bg-white bg-opacity-20 p-3 rounded-lg">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"></path>
              </svg>
            </div>
          </div>
          <p class="text-blue-100 text-sm mt-4">Total en inventario</p>
        </div>

        <div class="card bg-gradient-to-br from-green-500 to-green-600 text-white">
          <div class="flex justify-between items-start">
            <div>
              <p class="text-green-100 text-sm font-medium">Ventas Hoy</p>
              <h3 class="text-3xl font-bold mt-2">$0</h3>
            </div>
            <div class="bg-white bg-opacity-20 p-3 rounded-lg">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
          </div>
          <p class="text-green-100 text-sm mt-4">Total vendido</p>
        </div>

        <div class="card bg-gradient-to-br from-yellow-500 to-yellow-600 text-white">
          <div class="flex justify-between items-start">
            <div>
              <p class="text-yellow-100 text-sm font-medium">Stock Bajo</p>
              <h3 class="text-3xl font-bold mt-2">0</h3>
            </div>
            <div class="bg-white bg-opacity-20 p-3 rounded-lg">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
              </svg>
            </div>
          </div>
          <p class="text-yellow-100 text-sm mt-4">Requieren reposición</p>
        </div>

        <div class="card bg-gradient-to-br from-purple-500 to-purple-600 text-white">
          <div class="flex justify-between items-start">
            <div>
              <p class="text-purple-100 text-sm font-medium">Ventas Mes</p>
              <h3 class="text-3xl font-bold mt-2">$0</h3>
            </div>
            <div class="bg-white bg-opacity-20 p-3 rounded-lg">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
              </svg>
            </div>
          </div>
          <p class="text-purple-100 text-sm mt-4">Enero 2026</p>
        </div>
      </div>

      <!-- Acciones Rápidas -->
      <div class="px-4 sm:px-0">
        <div class="card">
          <h3 class="text-xl font-bold text-gray-900 mb-6">Acciones Rápidas</h3>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <button class="flex items-center justify-center p-6 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-all group">
              <div class="text-center">
                <svg class="w-12 h-12 mx-auto text-gray-400 group-hover:text-primary-600 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                </svg>
                <p class="mt-2 font-medium text-gray-700 group-hover:text-primary-700">Nueva Venta</p>
                <p class="text-sm text-gray-500 mt-1">Registrar venta</p>
              </div>
            </button>

            <button class="flex items-center justify-center p-6 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-all group">
              <div class="text-center">
                <svg class="w-12 h-12 mx-auto text-gray-400 group-hover:text-primary-600 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"></path>
                </svg>
                <p class="mt-2 font-medium text-gray-700 group-hover:text-primary-700">Productos</p>
                <p class="text-sm text-gray-500 mt-1">Gestionar inventario</p>
              </div>
            </button>

            <button class="flex items-center justify-center p-6 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-all group">
              <div class="text-center">
                <svg class="w-12 h-12 mx-auto text-gray-400 group-hover:text-primary-600 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
                <p class="mt-2 font-medium text-gray-700 group-hover:text-primary-700">Reportes</p>
                <p class="text-sm text-gray-500 mt-1">Ver estadísticas</p>
              </div>
            </button>
          </div>
        </div>
      </div>

      <!-- Mensaje de Estado -->
      <div class="mt-8 px-4 sm:px-0">
        <div class="bg-blue-50 border-l-4 border-blue-500 p-4 rounded">
          <div class="flex">
            <div class="flex-shrink-0">
              <svg class="h-5 w-5 text-blue-500" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"></path>
              </svg>
            </div>
            <div class="ml-3">
              <p class="text-sm text-blue-700">
                <strong>Sistema Inicializado:</strong> Dashboard funcional. Los módulos de productos, ventas y reportes se agregarán próximamente.
              </p>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';

const router = useRouter();
const authStore = useAuthStore();

function getUserInitials() {
  const name = authStore.user?.full_name || 'Usuario';
  const parts = name.split(' ');
  if (parts.length >= 2) {
    return (parts[0][0] + parts[1][0]).toUpperCase();
  }
  return name.substring(0, 2).toUpperCase();
}

async function handleLogout() {
  await authStore.logout();
  router.push('/login');
}
</script>