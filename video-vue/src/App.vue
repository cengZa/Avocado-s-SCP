<template>
  <div id="app">
    <router-view />
     <FloatingAI/>
  </div>
</template>


<script>
import FloatingAI from './components/FloatingAI.vue';
export default {
  name: 'App',
  components: {
    FloatingAI
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
