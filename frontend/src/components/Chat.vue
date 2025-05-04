<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center">
    <div class="bg-white p-6 rounded-lg shadow-md w-full max-w-lg">
      <h2 class="text-2xl font-bold mb-4 text-gray-800">Chat</h2>
      <div class="h-64 overflow-y-auto border p-4 mb-4 rounded bg-gray-50">
        <div v-for="message in messages" :key="message.timestamp" class="mb-2">
          <span class="font-semibold">{{ message.username }}:</span> {{ message.text }}
          <span class="text-xs text-gray-500">({{ message.timestamp }})</span>
        </div>
      </div>
      <form @submit.prevent="sendMessage" class="flex space-x-2">
        <input v-model="newMessage" placeholder="Type a message..." class="flex-1 p-2 border rounded" />
        <button type="submit" class="bg-blue-500 text-white p-2 rounded hover:bg-blue-600 transition">
          Send
        </button>
      </form>
      <button
          @click="leaveChat"
          class="mt-4 w-full bg-red-500 text-white p-2 rounded hover:bg-red-600 transition"
      >
        Leave Chat
      </button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { useAuthStore } from '../stores/auth';
import { useRouter, useRoute } from 'vue-router';

export default {
  data() {
    return {
      messages: [],
      newMessage: '',
      chatKey: '',
      pollInterval: null
    };
  },
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    const route = useRoute();
    return { authStore, router, route };
  },
  mounted() {
    this.chatKey = this.route.query.chatKey;
    if (!this.chatKey) {
      this.router.push('/play');
      return;
    }
    this.fetchMessages();
    this.startPolling();
  },
  methods: {
    async fetchMessages() {
      try {
        const response = await axios.get(`http://localhost:8080/api/chat/messages/${this.chatKey}`, {
          headers: { Authorization: `Bearer ${this.authStore.token}` }
        });
        this.messages = response.data;
      } catch (error) {
        console.error('Failed to fetch messages:', error);
      }
    },
    async sendMessage() {
      if (!this.newMessage.trim()) return;
      try {
        await axios.post('http://localhost:8080/api/chat/messages', {
          chatKey: this.chatKey,
          text: this.newMessage.trim()
        }, {
          headers: {
            Authorization: `Bearer ${this.authStore.token}`,
            'Content-Type': 'application/json'
          }
        });
        this.newMessage = '';
        await this.fetchMessages(); // Immediate refresh
      } catch (error) {
        console.error('Failed to send message:', error);
      }
    },
    async leaveChat() {
      try {
        await axios.post('http://localhost:8080/api/chat/leave', {}, {
          headers: { Authorization: `Bearer ${this.authStore.token}` }
        });
        this.stopPolling();
        await this.router.push('/play');
      } catch (error) {
        console.error('Failed to leave chat:', error);
      }
    },
    startPolling() {
      this.stopPolling(); // Clear any existing interval
      this.pollInterval = setInterval(() => {
        this.fetchMessages();
      }, 1000); // Faster polling
    },
    stopPolling() {
      if (this.pollInterval) {
        clearInterval(this.pollInterval);
        this.pollInterval = null;
      }
    }
  },
  beforeUnmount() {
    this.stopPolling();
  }
};
</script>