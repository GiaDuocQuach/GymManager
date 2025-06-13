<template>
  <div class="auth-buttons">
    <button @click="$emit('login')" class="btn login">Đăng nhập</button>
    <button @click="$emit('register')" class="btn register">Đăng ký</button>
  </div>
</template>

<script setup>
</script>

<style scoped>
.auth-buttons {
  display: flex;
  gap: 1rem;
}

.auth-buttons .btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
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