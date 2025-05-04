<template>
  <div>
    <Navbar />
    <router-view class="mt-4" />
  </div>
</template>

<script>
import Navbar from './components/Navbar.vue';

export default {
  components: { Navbar }
};
</script>
