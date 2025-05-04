<template>
  <div class="not-found">
    <h1 class="text-2xl font-bold mb-2">404 - Page Not Found</h1>
    <p class="text-gray-600 mb-6">The page you're looking for doesn't exist.</p>
    <router-link
        to="/"
        class="bg-gray-100 hover:bg-gray-200 text-gray-800 px-6 py-2 rounded-md transition-colors duration-200 text-sm font-medium whitespace-nowrap"
    >
      Go Home
    </router-link>
  </div>
</template>

<style scoped>
.not-found {
  @apply min-h-screen flex flex-col justify-center items-center p-8 text-center;
}
</style>