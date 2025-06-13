<template>
  <section class="hero">
    <div class="hero-content">
      <h1>Chào mừng đến với FitLife Gym</h1>
      <p>Khám phá sức mạnh của bạn và đạt được mục tiêu sức khỏe với chúng tôi!</p>
      <button @click="$emit('cta-click')" class="btn cta">Bắt đầu ngay</button>
    </div>
  </section>
</template>

<script setup>
</script>

<style scoped>
.hero {
  background: url('/gym-bg.jpg') no-repeat center/cover;
  height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: white;
}

.hero-content {
  max-width: 600px;
}

.hero h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.hero p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
}

.hero .cta {
  background-color: #ff4500;
  color: white;
  padding: 1rem 2rem;
  border: none;
  border-radius: 5px;
  font-size: 1.2rem;
  cursor: pointer;
}
</style>