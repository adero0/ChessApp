<template>
  <div class="flex flex-col items-center justify-center min-h-screen bg-gradient-to-br from-slate-800 via-slate-700 to-slate-800 text-white px-4">
    <h2 class="text-4xl font-extrabold mb-10 tracking-tight text-center">
      Wybierz tryb gry ♟️
    </h2>
    <div class="flex gap-12 flex-wrap justify-center">
      <!-- Na gracza -->
      <div
          class="group bg-gray-800 rounded-2xl shadow-xl p-6 flex flex-col items-center transition-transform transform hover:scale-105 hover:shadow-2xl duration-300 ease-in-out w-64"
      >
        <router-link
            to="/play"
            class="bg-emerald-500 hover:bg-emerald-600 text-white font-semibold px-6 py-3 rounded-xl transition duration-300 ease-in-out"
        >
          Na gracza
        </router-link>
        <span class="text-5xl mt-4 group-hover:scale-110 transition-transform duration-300">👤</span>
      </div>

      <!-- Na komputer -->
      <div
          class="group bg-gray-800 rounded-2xl shadow-xl p-6 flex flex-col items-center transition-transform transform hover:scale-105 hover:shadow-2xl duration-300 ease-in-out w-64"
      >
        <router-link
            to="/playAI"
            class="bg-purple-500 hover:bg-purple-600 text-white font-semibold px-6 py-3 rounded-xl transition duration-300 ease-in-out"
        >
          Na komputer
        </router-link>
        <span class="text-5xl mt-4 group-hover:scale-110 transition-transform duration-300">🤖</span>
      </div>
    </div>
  </div>
</template>

