<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-slate-900 px-4">
    <form
        @submit.prevent="register"
        class="bg-gray-800 p-8 rounded-2xl shadow-2xl w-full max-w-sm text-white animate-fade-in"
    >
      <h2 class="text-3xl font-bold mb-6 text-center text-green-400 tracking-tight">
        Rejestracja
      </h2>

      <input
          v-model="username"
          placeholder="Nazwa użytkownika"
          class="w-full mb-4 px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 placeholder-gray-400 text-white focus:outline-none focus:ring-2 focus:ring-green-500"
      />

      <input
          v-model="password"
          type="password"
          placeholder="Hasło"
          class="w-full mb-4 px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 placeholder-gray-400 text-white focus:outline-none focus:ring-2 focus:ring-green-500"
      />

      <input
          v-model="repeat_password"
          type="password"
          placeholder="Powtórz hasło"
          class="w-full mb-6 px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 placeholder-gray-400 text-white focus:outline-none focus:ring-2 focus:ring-green-500"
      />

      <button
          type="submit"
          class="w-full bg-green-500 hover:bg-green-600 text-white font-semibold py-3 rounded-lg transition-transform transform hover:scale-105 shadow-md"
      >
        Zarejestruj się
      </button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      username: '',
      password: '',
      repeat_password: '',
    };
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
    async register() {
      try {
        if (this.password !== this.repeat_password) {
          const message = 'Hasła nie są takie same!';
          this.openErrorToast(message);
          throw new Error(message);
        }

        await axios.post('http://localhost:8080/api/auth/register', {
          username: this.username,
          password: this.password,
        });

        this.$router.push('/login');
      } catch (error) {
        if (error.response && error.response.status === 400) {
          this.openErrorToast(error.response.data);
        } else {
          console.log('Rejestracja nie powiodła się', error);
        }
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
