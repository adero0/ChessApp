<template>
  <div class="not-found bg-gradient-to-br from-slate-900 via-gray-800 to-gray-900 text-white">
    <div class="text-center max-w-md mx-auto p-8 animate-fade-in">
      <div class="text-6xl mb-4"></div>
      <h1 class="text-4xl font-bold mb-2">404 - Nie znaleziono strony</h1>
      <p class="text-gray-300 mb-6">
        Ups! Wygląda na to, że ta strona nie istnieje.
      </p>
      <router-link
          to="/"
          class="inline-block bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-md transition-colors duration-200 text-sm font-medium"
      >
        Wróć do strony głównej
      </router-link>
    </div>
  </div>
</template>

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
  animation: fadeIn 0.5s ease-out both;
}

.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
