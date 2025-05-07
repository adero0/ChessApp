<template>
    <div class="flex flex-col items-center justify-center min-h-screen">
      <h2 class="text-2xl font-bold mb-8">Zagraj:</h2>
      <div class="flex gap-10">
        <div class="flex flex-col items-center">
          <router-link to="/play" class="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded mb-2">
            Na gracza
          </router-link>
          <span>😎</span>
        </div>
        <div class="flex flex-col items-center">
          <router-link to="/play" class="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded mb-2">
            Na komputer
          </router-link>
          <span>🤖</span>
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
      todos: [],
      newTodo: ''
    };
  },
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    return { authStore, router };
  },
  mounted() {
    this.fetchTodos();
  },
  methods: {
    async fetchTodos() {
      try {
        const response = await axios.get('http://localhost:8080/api/todos', {
          headers: { Authorization: `Bearer ${this.authStore.token}` }
        });
        this.todos = response.data;
      } catch (error) {
        console.error('Failed to fetch todos:', error);
      }
    },
    async addTodo() {
      try {
        await axios.post('http://localhost:8080/api/todos',
            {title: this.newTodo},
            {
              headers: {
                Authorization: `Bearer ${this.authStore.token}`,
                'Content-Type': 'application/json'
              }
            }
        );
        this.newTodo = '';
        await this.fetchTodos();
      } catch (error) {
        console.error('Failed to add todo:', error);
      }
    },
    async deleteTodo(todoId) {
      try {
        await axios.delete(`http://localhost:8080/api/todos/${todoId}`, {
          headers: {Authorization: `Bearer ${this.authStore.token}`}
        });
        await this.fetchTodos();
      } catch (error) {
        console.error('Failed to delete todo:', error);
      }
    }
  }
};
</script>