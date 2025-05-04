<template>
  <div class="max-w-2xl mx-auto p-4">
    <h1 class="text-3xl font-bold text-gray-800 mb-4">Todos</h1>
    <router-link to="/play" class="inline-block mb-4 bg-green-500 text-white p-2 rounded hover:bg-green-600 transition">
      Play
    </router-link>
    <ul class="space-y-2">
      <li
          @click="deleteTodo(todo.id)"
          v-for="todo in todos"
          :key="todo.id"
          class="p-3 bg-white rounded shadow hover:bg-gray-100 transition cursor-pointer"
      >
        {{ todo.title }}
      </li>
    </ul>
    <form @submit.prevent="addTodo" class="mt-4 flex space-x-2">
      <input v-model="newTodo" placeholder="New Todo" class="flex-1 p-2 border rounded" />
      <button type="submit" class="bg-blue-500 text-white p-2 rounded hover:bg-blue-600 transition">
        Add
      </button>
    </form>
  </div>
</template>
<!-- Script unchanged except add useRouter to setup() if needed -->
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