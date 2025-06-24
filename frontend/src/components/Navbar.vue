<template>
  <nav class="bg-gray-800 p-4 shadow-md">
    <div class="max-w-7xl mx-auto flex justify-between items-center">
      <div class="text-white text-2xl font-bold">
        <router-link v-if="authStore.isLoggedIn" to="/main">Szacher</router-link>
        <router-link v-if="!authStore.isLoggedIn" to="/">Szacher</router-link>
      </div>
      <div><p v-if="authStore.isLoggedIn" class="text-white"> Witaj, {{authStore.username}}!</p></div>
      <div class="space-x-4">

        <router-link v-if="!authStore.isLoggedIn" to="/login" class="text-gray-300 hover:text-white transition">
          Zaloguj się
        </router-link>
        <router-link v-if="!authStore.isLoggedIn" to="/register" class="text-gray-300 hover:text-white transition">
          Zarejestruj się
        </router-link>
<!--        <router-link v-if="authStore.isLoggedIn" to="/game" class="text-gray-300 hover:text-white transition"> Graj </router-link>-->
        <button v-if="authStore.isLoggedIn" @click="logout" class="text-gray-300 hover:text-white transition">
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
    const router = useRouter(); // Get router instance

    const logout = () => {
      authStore.setUserData(null,null); // Clear token
      router.push('/'); // Redirect to guest page
    };

    return {
      authStore,
      logout
    };
  }
};
</script>
