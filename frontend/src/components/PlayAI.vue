<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-gray-800 to-gray-900 flex items-center justify-center px-4">
    <div class="bot-selection animate-fade-in">
      <h2 class="text-3xl font-bold mb-6 text-white">Graj na komputer</h2>

      <div class="difficulty-container">
        <div
            v-for="difficulty in difficulties"
            :key="difficulty.value"
            class="difficulty-option"
            :class="{ selected: selectedDifficulty === difficulty.value }"
            @click="selectedDifficulty = difficulty.value"
        >
          <input type="radio" :id="difficulty.value" :value="difficulty.value" v-model="selectedDifficulty" hidden />
          <label :for="difficulty.value" class="difficulty-content">
            <span class="difficulty-name">{{ difficulty.label }}</span>
            <span class="difficulty-description">{{ difficulty.description }}</span>
          </label>
        </div>
      </div>

      <div class="selection-section">
        <h3 class="text-gray-300 text-lg mb-2">Wybierz kolor:</h3>
        <div class="color-container">
          <div
              v-for="color in colorOptions"
              :key="color.value"
              class="color-option"
              :class="{ selected: selectedColor === color.value }"
              @click="selectedColor = color.value"
          >
            <input type="radio" :id="color.value" :value="color.value" v-model="selectedColor" hidden />
            <label :for="color.value" class="color-content" :style="color.style">
              <span class="color-name">{{ color.label }}</span>
            </label>
          </div>
        </div>
      </div>

      <button class="play-button mt-6" @click="handlePlay">Zagraj</button>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { useAuthStore } from "../stores/auth.js";
import { useRouter } from "vue-router";

export default {
  data() {
    return {
      selectedColor: 'random',
      selectedDifficulty: null,
      difficulties: [
        { label: 'Łatwy', value: '2', description: 'Dobry dla początkujących' },
        { label: 'Średni', value: '4', description: 'Ciekawe wyzwanie dla amatorów' },
        { label: 'Trudny', value: '10', description: 'Trudne nawet dla zaawansowanych' },
        { label: 'Mistrz', value: '15', description: 'Tylko dla najlepszych' }
      ],
      colorOptions: [
        { label: '', value: 'WHITE', style: { backgroundColor: '#fff', color: '#000', border: '2px solid #ddd' }},
        { label: '', value: 'random', style: {
            background: 'linear-gradient(45deg, #fff 49.9%, #2c3e50 49.9%, #2c3e50 60%)',
            color: '#2c3e50'
          }},
        { label: '', value: 'BLACK', style: { backgroundColor: '#2c3e50', color: 'white' }}
      ]
    };
  },
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    return { authStore, router };
  },
  methods: {
    async handlePlay() {
      if (this.selectedColor === 'random') {
        this.selectedColor = Math.random() >= 0.5 ? 'WHITE' : 'BLACK';
      }
      const response = await axios.post(
          'http://localhost:8080/api/game/bot_game/join',
          this.selectedColor,
          {
            headers: {
              Authorization: `Bearer ${this.authStore.token}`,
              'Content-Type': 'text/plain'
            }
          }
      );
      this.$router.push(`/game/bot_game?gameId=${response.data.gameId}&difficulty=${this.selectedDifficulty}`);
    }
  }
};
</script>

<style scoped>
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in {
  animation: fadeIn 0.6s ease-out both;
}

.bot-selection {
  background: #1f2937; /* dark gray */
  border-radius: 1rem;
  padding: 2rem;
  max-width: 700px;
  width: 100%;
  color: #fff;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.4);
}

.difficulty-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-bottom: 2rem;
}

.difficulty-option {
  background-color: #374151;
  padding: 1.25rem;
  border-radius: 0.75rem;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 2px solid transparent;
}
.difficulty-option:hover {
  transform: translateY(-2px);
  border-color: #3b82f6;
}
.difficulty-option.selected {
  border-color: #3b82f6;
  background-color: #1e40af;
}
.difficulty-content {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.difficulty-name {
  font-weight: bold;
  font-size: 1.1rem;
  color: #ffffff;
}
.difficulty-description {
  color: #d1d5db;
  font-size: 0.9rem;
}

.color-container {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.color-option {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  cursor: pointer;
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border: 3px solid transparent;
}
.color-option.selected {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: scale(1.05);
}
.color-content {
  display: flex;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  transition: all 0.2s ease;
}
.color-option:hover:not(.selected) {
  transform: scale(1.03);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.play-button {
  background: #3b82f6;
  color: white;
  padding: 1rem 2rem;
  border: none;
  border-radius: 0.5rem;
  font-size: 1.1rem;
  cursor: pointer;
  transition: background 0.2s ease;
}
.play-button:hover {
  background: #2563eb;
}
.play-button:active {
  transform: scale(0.98);
}
</style>
