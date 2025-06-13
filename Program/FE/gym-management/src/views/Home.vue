<template>
  <div class="home-page">
    <Header />
    <Hero />
    <Features />
    <AuthButtons />
    <Footer />
  </div>
</template>

<script setup>
import Header from '../components/Header.vue';
import Hero from '../components/Hero.vue';
import Features from '../components/Features.vue';
import AuthButtons from '../components/AuthButtons.vue';
import Footer from '../components/Footer.vue';
</script>

<style scoped>
@import '../assets/styles/home.css';
</style>