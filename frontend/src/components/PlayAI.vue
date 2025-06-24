<template>
  <div class="bot-selection">
    <h2>Graj na komputer</h2>
    <div class="difficulty-container">
      <div
          v-for="difficulty in difficulties"
          :key="difficulty.value"
          class="difficulty-option"
          :class="{ selected: selectedDifficulty === difficulty.value }"
          @click="selectedDifficulty = difficulty.value"
      >
        <input
            type="radio"
            :id="difficulty.value"
            :value="difficulty.value"
            v-model="selectedDifficulty"
            hidden
        >
        <label :for="difficulty.value" class="difficulty-content">
          <span class="difficulty-name">{{ difficulty.label }}</span>
          <span class="difficulty-description">{{ difficulty.description }}</span>
        </label>
      </div>
    </div>
    <div class="selection-section">
      <h3>Wybierz kolor: </h3>
      <div class="color-container">
        <div
            v-for="color in colorOptions"
            :key="color.value"
            class="color-option"
            :class="{ selected: selectedColor === color.value }"
            @click="selectedColor = color.value"
        >
          <input
              type="radio"
              :id="color.value"
              :value="color.value"
              v-model="selectedColor"
              hidden
          >
          <label :for="color.value" class="color-content" :style="color.style">
            <span class="color-name">{{ color.label }}</span>
          </label>
        </div>
      </div>
    </div>
    <button class="play-button" @click="handlePlay">Play Game</button>
<!--    <label>{{selectedColor}}</label>-->
  </div>
</template>

<script>
import axios from "axios";
import {useAuthStore} from "../stores/auth.js";
import {useRouter} from "vue-router";

export default {
  data() {
    return {
      selectedColor: 'random',
      selectedDifficulty: null,
      difficulties: [
        {
          label: 'Łatwy',
          value: '2',
          description: 'Dobry dla początkujących'
        },
        {
          label: 'Średni',
          value: '4',
          description: 'Ciekawe wyzwanie dla amatorów'
        },
        {
          label: 'Trudny',
          value: '10',
          description: 'Trudne nawet dla zaawanosowanych'
        },
        {
          label: 'Mistrz',
          value: '15',
          description: 'Tylko dla najlepszych'
        }
      ],
      colorOptions: [
        {
          label: '',
          value: 'WHITE',
          style: { backgroundColor: '#fff', color: '#000', border: '2px solid #ddd' }
        },
        {
          label: '',
          value: 'random',
          style: {
            background: 'linear-gradient(45deg, #fff 49.9%, #2c3e50 49.9%, #2c3e50 60%)',
            color: '#2c3e50'
          }
        },
        {
          label: '',
          value: 'BLACK',
          style: { backgroundColor: '#2c3e50', color: 'white' }
        },

      ],
    }
  },
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    return { authStore, router };
  },
  methods: {
    async handlePlay() {
      if(this.selectedColor === 'random') {
        this.selectedColor = Math.random() >= 0.5 ? 'WHITE' : 'BLACK'
      }
      console.log("your selected color is ", this.selectedColor);
      const response = await axios.post('http://localhost:8080/api/game/bot_game/join', this.selectedColor, {
        headers: { Authorization: `Bearer ${this.authStore.token}`,
          'Content-Type': 'text/plain'
        }
      });
      this.$router.push(`/game/bot_game?gameId=${response.data.gameId}&difficulty=${this.selectedDifficulty}`);
    }
  }
}
</script>

<style scoped>
.selection-section {
  margin-bottom: 2rem;
}

.selection-section h3 {
  color: #34495e;
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

.color-container {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.color-option {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  cursor: pointer;
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border: 3px solid transparent;
}

.color-option.selected {
  border-color: #2980b9;
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

.color-name {
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}
.bot-selection {
  max-width: 600px;
  margin: 2rem auto;
  padding: 2rem;
  text-align: center;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  color: #2c3e50;
  margin-bottom: 2rem;
}

.difficulty-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.difficulty-option {
  padding: 1.5rem;
  border: 2px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.difficulty-option:hover {
  border-color: #3498db;
  transform: translateY(-2px);
}

.difficulty-option.selected {
  border-color: #2980b9;
  background-color: #f5f9fd;
}

.difficulty-content {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.difficulty-name {
  font-weight: bold;
  color: #2c3e50;
  font-size: 1.1rem;
}

.difficulty-description {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.play-button {
  background: #2980b9;
  color: white;
  padding: 1rem 2rem;
  border: none;
  border-radius: 4px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: background 0.2s ease;
}

.play-button:hover {
  background: #3498db;
}

.play-button:active {
  transform: scale(0.98);
}
</style>