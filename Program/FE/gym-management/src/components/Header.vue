<template>
  <header class="header">
    <div class="logo">
      <img src="/logo.png" alt="FitLife Gym Logo" />
    </div>
    <nav class="nav">
      <ul>
        <li><a href="#home">Trang chủ</a></li>
        <li><a href="#classes">Lớp học</a></li>
        <li><a href="#trainers">Huấn luyện viên</a></li>
        <li><a href="#pricing">Bảng giá</a></li>
        <li><a href="#contact">Liên hệ</a></li>
      </ul>
    </nav>
    <div class="auth-buttons">
      <button @click="$emit('goToLogin')" class="btn login">Đăng nhập</button>
      <button @click="$emit('goToRegister')" class="btn register">Đăng ký</button>
    </div>
  </header>
</template>

<script setup>
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #1a1a1a;
  color: white;
}

.logo img {
  height: 50px;
}

.nav ul {
  display: flex;
  list-style: none;
  gap: 1.5rem;
}

.nav a {
  color: white;
  text-decoration: none;
  font-weight: bold;
}

.auth-buttons .btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.auth-buttons .login {
  background-color: #007bff;
  color: white;
}

.auth-buttons .register {
  background-color: #ff4500;
  color: white;
}
</style>