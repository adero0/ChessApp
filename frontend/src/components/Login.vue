<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-slate-900 px-4">
    <form
        @submit.prevent="login"
        class="bg-gray-800 p-8 rounded-2xl shadow-2xl w-full max-w-sm text-white animate-fade-in"
    >
      <h2 class="text-3xl font-bold mb-6 text-center text-emerald-400 tracking-tight">
        Logowanie
      </h2>

      <input
          v-model="username"
          placeholder="Nazwa użytkownika"
          class="w-full mb-4 px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 placeholder-gray-400 text-white focus:outline-none focus:ring-2 focus:ring-emerald-500"
      />

      <input
          v-model="password"
          type="password"
          placeholder="Hasło"
          class="w-full mb-6 px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 placeholder-gray-400 text-white focus:outline-none focus:ring-2 focus:ring-emerald-500"
      />

      <button
          type="submit"
          class="w-full bg-emerald-500 hover:bg-emerald-600 text-white font-semibold py-3 rounded-lg transition-transform transform hover:scale-105 shadow-md"
      >
        Zaloguj się
      </button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';
import { useAuthStore } from '../stores/auth';

export default {
  data() {
    return {
      username: '',
      password: ''
    };
  },
  setup() {
    return { authStore: useAuthStore() };
  },
  methods: {
    openErrorToast(message) {
      this.$toast.open({
        message: message,
        type: "error",
        duration: 10000,
        dismissible: true,
      });
    },
    async login() {
      try {
        const response = await axios.post('http://localhost:8080/api/auth/login', {
          username: this.username,
          password: this.password
        });
        this.authStore.setUserData(
            response.data.token,
            response.data.username
        );
        this.$router.push('/main');
      } catch (error) {
        this.openErrorToast('Nieprawidłowa nazwa użytkownika lub hasło.');
        console.error('Login failed', error);
      }
    }
  }
};
</script>

<style scoped>
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in {
  animation: fadeIn 0.6s ease-out both;
}
</style>
