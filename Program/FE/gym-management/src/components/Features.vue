<template>
  <section class="features">
    <h2>Tại sao chọn FitLife Gym?</h2>
    <div class="features-grid">
      <div class="feature-item">
        <img src="/equipment.png" alt="Thiết bị hiện đại" />
        <h3>Thiết bị hiện đại</h3>
        <p>Trang thiết bị tối tân, đảm bảo hiệu quả tập luyện.</p>
      </div>
      <div class="feature-item">
        <img src="/trainers.png" alt="Huấn luyện viên chuyên nghiệp" />
        <h3>Huấn luyện viên chuyên nghiệp</h3>
        <p>Đội ngũ HLV tận tâm, hỗ trợ bạn từng bước.</p>
      </div>
      <div class="feature-item">
        <img src="/classes.png" alt="Lớp học đa dạng" />
        <h3>Lớp học đa dạng</h3>
        <p>Yoga, Zumba, Boxing và nhiều lớp học thú vị khác.</p>
      </div>
    </div>
  </section>
</template>

<script setup>
</script>

<style scoped>
.features {
  padding: 4rem 2rem;
  text-align: center;
}

.features h2 {
  font-size: 2.5rem;
  margin-bottom: 2rem;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-item img {
  height: 100px;
  margin-bottom: 1rem;
}

.feature-item h3 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}
</style>