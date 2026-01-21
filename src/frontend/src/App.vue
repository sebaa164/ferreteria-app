<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth.store';

const authStore = useAuthStore();

onMounted(() => {
  authStore.checkAuth();
});
</script>

<style>
#app {
  width: 100%;
  min-height: 100vh;
}
</style>
