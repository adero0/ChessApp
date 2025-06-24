<template>
  <nav class="bg-gray-900 border-b border-gray-700 shadow-sm">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
      <!-- Logo -->
      <div class="text-white text-2xl font-extrabold tracking-wide">
        <router-link v-if="authStore.isLoggedIn" to="/main" class="hover:text-emerald-400 transition">
          Szacher
        </router-link>
        <router-link v-if="!authStore.isLoggedIn" to="/" class="hover:text-emerald-400 transition">
          Szacher
        </router-link>
      </div>

      <!-- Powitanie -->
      <div class="hidden sm:block">
        <p v-if="authStore.isLoggedIn" class="text-gray-300 text-sm">
          Witaj, <span class="text-white font-medium">{{ authStore.username }}</span>!
        </p>
      </div>

      <!-- Linki / przyciski -->
      <div class="flex space-x-4 text-sm">
        <router-link
            v-if="!authStore.isLoggedIn"
            to="/login"
            class="text-gray-400 hover:text-white transition"
        >
          Zaloguj się
        </router-link>
        <router-link
            v-if="!authStore.isLoggedIn"
            to="/register"
            class="text-gray-400 hover:text-white transition"
        >
          Rejestracja
        </router-link>
        <button
            v-if="authStore.isLoggedIn"
            @click="logout"
            class="text-red-400 hover:text-red-300 transition"
        >
          Wyloguj się
        </button>
      </div>
    </div>
  </nav>
</template>

<script>
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';

export default {
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();

    const logout = () => {
      authStore.setUserData(null, null);
      router.push('/');
    };

    return {
      authStore,
      logout
    };
  }
};
</script>
