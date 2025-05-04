<template>
  <nav class="bg-gray-800 p-4 shadow-md">
    <div class="max-w-7xl mx-auto flex justify-between items-center">
      <div class="text-white text-2xl font-bold">
        <router-link to="/">Todo App</router-link>
      </div>
      <div><p v-if="authStore.isLoggedIn" class="text-white"> Hi, {{authStore.username}}!</p></div>
      <div class="space-x-4">

        <router-link v-if="!authStore.isLoggedIn" to="/login" class="text-gray-300 hover:text-white transition">
          Login
        </router-link>
        <router-link v-if="!authStore.isLoggedIn" to="/register" class="text-gray-300 hover:text-white transition">
          Register
        </router-link>
        <router-link v-if="authStore.isLoggedIn" to="/todos" class="text-gray-300 hover:text-white transition">
          Todos
        </router-link>
        <router-link to="/test" class="text-gray-300 hover:text-white transition"> Test </router-link>
        <button v-if="authStore.isLoggedIn" @click="logout" class="text-gray-300 hover:text-white transition">
          Logout
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