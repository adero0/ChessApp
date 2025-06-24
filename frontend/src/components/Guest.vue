<template>
  <div class="min-h-screen flex flex-col items-center justify-center bg-gradient-to-br from-slate-900 via-gray-900 to-slate-800 px-4 text-white text-center">
    <h1 class="text-5xl sm:text-6xl font-extrabold mb-6 tracking-tight animate-fade-in">
      Witamy w <span class="text-emerald-400">Szacher</span> ♟️
    </h1>
    <p class="text-lg sm:text-xl text-gray-300 max-w-xl mb-10 animate-fade-in delay-200">
      Zagraj w szachy online — z innym graczem lub z silnikiem. Załóż konto i rozpocznij rozgrywkę w kilka sekund.
    </p>
    <div class="flex gap-6 animate-fade-in delay-300">
      <router-link
          to="/login"
          class="bg-emerald-500 hover:bg-emerald-600 text-white font-semibold px-6 py-3 rounded-xl shadow-md transition-transform transform hover:scale-105"
      >
        Zaloguj się
      </router-link>
      <router-link
          to="/register"
          class="bg-purple-500 hover:bg-purple-600 text-white font-semibold px-6 py-3 rounded-xl shadow-md transition-transform transform hover:scale-105"
      >
        Rejestracja
      </router-link>
    </div>
  </div>
</template>

<style scoped>
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.animate-fade-in {
  animation: fadeIn 0.8s ease-out both;
}
.delay-200 {
  animation-delay: 0.2s;
}
.delay-300 {
  animation-delay: 0.3s;
}
</style>
