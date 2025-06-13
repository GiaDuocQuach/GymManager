<template>
  <footer class="footer">
    <p>FitLife Gym - Sức khỏe là vàng</p>
    <div class="social-links">
      <a href="#"><img src="/facebook.png" alt="Facebook" /></a>
      <a href="#"><img src="/instagram.png" alt="Instagram" /></a>
      <a href="#"><img src="/twitter.png" alt="Twitter" /></a>
    </div>
    <p>Liên hệ: info@fitlifegym.com | 0123 456 789</p>
  </footer>
</template>

<script setup>
// No script needed for the footer component
</script>

<style scoped>
.footer {
  background-color: #1a1a1a;
  color: white;
  text-align: center;
  padding: 2rem;
}

.social-links {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin: 1rem 0;
}

.social-links img {
  height: 30px;
}
</style>