<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center">
    <div class="bg-white p-6 rounded-lg shadow-md w-full max-w-md text-center">
      <h2 class="text-2xl font-bold mb-4 text-gray-800">Szukaj przeciwnika: </h2>
      <button
          v-if="status !== 'paired'"
          @click="joinQueue"
          class="bg-blue-500 text-white p-2 rounded hover:bg-blue-600 transition w-full"
          :disabled="status === 'waiting'"
      >
        {{ status === 'waiting' ? 'Czekanie na mecz...' : 'Dołącz do kolejki' }}
      </button>
      <p v-if="status === 'waiting'" class="mt-4 text-gray-600">Poczekaj na znaleznie przeciwnika...</p>
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
      userId: null,
    };
  },
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    return { authStore, router };
  },
  methods: {
    async joinQueue() {
      try {
        const response = await axios.post('http://localhost:8080/api/game/queue/join', {}, {
          headers: { Authorization: `Bearer ${this.authStore.token}` }
        });
        if (response.data.status === 'paired') {
          this.status = 'paired';
          clearInterval(this.pollInterval);
          console.log("OPPONENT FOUND ESSA")
          await this.router.push(`/game?gameId=${response.data.gameId}`);
        } else if (response.data.status === 'waiting' && !this.pollInterval) {
          this.status = 'waiting'
          this.startPolling();
          console.log("i do be polling")
        }
      } catch (error) {
        console.error('Failed to join queue:', error);
        console.log('We failed in join')
        this.status = 'idle';
      }
    },
    async checkQueueStatus() {
      try {
        const response = await axios.post('http://localhost:8080/api/game/queue/status', {}, {
          headers: { Authorization: `Bearer ${this.authStore.token}` }
        });
        if (response.data.status === 'paired') {
          this.status = 'paired';
          clearInterval(this.pollInterval);
          console.log("OPPONENT FOUND ESSA")
          await this.router.push(`/game?gameId=${response.data.gameId}`);
        } else if (response.data.status === 'waiting') {
        // Just keep polling, no need to restart
      }
      } catch (error) {
        console.error('Queue check failed:', error);
        console.log('Fail in checking status')
        this.status = 'idle';
        clearInterval(this.pollInterval);
      }
    },


    startPolling() {
      this.pollInterval = setInterval(() => {
        this.checkQueueStatus();
      }, 1000); // Poll every 1 second for faster response
    },
    beforeUnmount() {
      clearInterval(this.pollInterval);
    }
  }
};
</script>