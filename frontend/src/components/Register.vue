<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center">
    <form @submit.prevent="register" class="bg-white p-6 rounded-lg shadow-md w-full max-w-sm">
      <h2 class="text-2xl font-bold mb-4 text-gray-800">Register</h2>
      <input v-model="username" placeholder="Nazwa użytkownika" class="w-full p-2 mb-4 border rounded" />
      <input v-model="password" type="password" placeholder="Hasło" class="w-full p-2 mb-4 border rounded" />
      <input v-model="repeat_password" type="password" placeholder="Powtórz hasło" class="w-full p-2 mb-4 border rounded" />
      <button type="submit" class="w-full bg-green-500 text-white p-2 rounded hover:bg-green-600 transition">
        Zarejestruj
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
        duration: 1000 * 10,
        dismissible: true,
      })
    },
    async register() {
      try {
        if(this.password !== this.repeat_password){
          let message = 'Passwords do not match!'
          this.openErrorToast(message)
          throw new Error(message)
        }
        await axios.post('http://localhost:8080/api/auth/register', {
          username: this.username,
          password: this.password
        });
        this.$router.push('/login');

      } catch (error) {
        if (error.response && error.response.status === 400) {
          const backendMessage = error.response.data;
          this.openErrorToast(backendMessage);
        } else {
          console.log('Registration failed, please try again', error)
        }
      }
    }
  }
};
</script>