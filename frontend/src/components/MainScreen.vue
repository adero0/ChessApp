<template>
    <div class="flex flex-col items-center justify-center min-h-screen">
      <h2 class="text-2xl font-bold mb-8">Zagraj:</h2>
      <div class="flex gap-10">
        <div class="flex flex-col items-center">
          <router-link to="/play" class="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded mb-2">
            Na gracza
          </router-link>
          <span>👤</span>
        </div>
        <div class="flex flex-col items-center">
          <router-link to="/playAI" class="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded mb-2">
            Na komputer
          </router-link>
          <span>🤖</span>
        </div>
      </div>
    </div>
</template>