<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center">
    <form @submit.prevent="login" class="bg-white p-6 rounded-lg shadow-md w-full max-w-sm">
      <h2 class="text-2xl font-bold mb-4 text-gray-800">Login</h2>
      <input v-model="username" placeholder="Nazwa użytkownika" class="w-full p-2 mb-4 border rounded" />
      <input v-model="password" type="password" placeholder="Hasło" class="w-full p-2 mb-4 border rounded" />
      <button type="submit" class="w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600 transition">
        Zaloguj
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
        duration: 1000 * 10,
        dismissible: true,
      })
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
        this.openErrorToast('Username or password is incorrect')
        console.error('Login failed', error);
      }
    }
  }
};
</script>