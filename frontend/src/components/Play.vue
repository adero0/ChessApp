<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-gray-800 to-gray-900 flex items-center justify-center px-4">
    <div class="bg-gray-800 text-white p-8 rounded-2xl shadow-2xl w-full max-w-md text-center animate-fade-in">
      <h2 class="text-3xl font-bold mb-6 text-green-400">Szukaj przeciwnika</h2>

      <button
          v-if="status !== 'paired'"
          @click="joinQueue"
          class="bg-blue-500 hover:bg-blue-600 text-white font-semibold py-3 px-6 rounded-lg w-full transition-transform transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="status === 'waiting'"
      >
        {{ status === 'waiting' ? 'Czekanie na mecz...' : 'Dołącz do kolejki' }}
      </button>

      <div v-if="status === 'waiting'" class="mt-6 flex flex-col items-center">
        <div class="loader mb-4"></div>
        <p class="text-gray-400">Szukam przeciwnika… proszę czekać.</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';

export default {
  data() {
    return {
      status: 'idle',
      pollInterval: null,
    };
  },
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    return {authStore, router};
  },
  methods: {
    async joinQueue() {
      try {
        const response = await axios.post('http://localhost:8080/api/game/queue/join', {}, {
          headers: {Authorization: `Bearer ${this.authStore.token}`}
        });

        if (response.data.status === 'paired') {
          this.status = 'paired';
          clearInterval(this.pollInterval);
          await this.router.push(`/game?gameId=${response.data.gameId}`);
        } else if (response.data.status === 'waiting' && !this.pollInterval) {
          this.status = 'waiting';
          this.startPolling();
        }
      } catch (error) {
        console.error('Failed to join queue:', error);
        this.status = 'idle';
      }
    },

    async checkQueueStatus() {
      try {
        const response = await axios.post('http://localhost:8080/api/game/queue/status', {}, {
          headers: {Authorization: `Bearer ${this.authStore.token}`}
        });

        if (response.data.status === 'paired') {
          this.status = 'paired';
          clearInterval(this.pollInterval);
          await this.router.push(`/game?gameId=${response.data.gameId}`);
        }
      } catch (error) {
        console.error('Queue check failed:', error);
        this.status = 'idle';
        clearInterval(this.pollInterval);
      }
    },

    startPolling() {
      this.pollInterval = setInterval(() => {
        this.checkQueueStatus();
      }, 1000);
    },

    beforeUnmount() {
      clearInterval(this.pollInterval);
    }
  }
};
</script>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fadeIn 0.6s ease-out both;
}

.loader {
  border: 4px solid rgba(255, 255, 255, 0.1);
  border-top: 4px solid #3b82f6; /* Tailwind's blue-500 */
  border-radius: 9999px;
  width: 40px;
  height: 40px;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
