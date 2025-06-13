<script setup>
import axios from 'axios';
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

// Biến lưu thông tin người dùng
const userInfo = ref(null);

// Hàm lấy thông tin người dùng
const getUserInfo = async () => {
  try {
    // Lấy JWT từ localStorage (hoặc nơi bạn lưu trữ)
    const token = localStorage.getItem('jwt');
    if (!token) {
      throw new Error('JWT không tồn tại');
    }

    // Gửi yêu cầu đến API
    const response = await axios.get('http://localhost:8081/user/getUser', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    // Lưu thông tin người dùng
    userInfo.value = response.data;
  } catch (error) {
    console.error('Lỗi khi lấy thông tin người dùng:', error);
  }
};

// Gọi hàm khi component được mount
onMounted(() => {
  getUserInfo();
});
</script>

<template>
  <div>
    <h1>Thông tin người dùng</h1>
    <div v-if="userInfo">
      <p>Tên: {{ userInfo.fullname }}</p>
      <p>Số điện thoại: {{ userInfo.phone }}</p>
      <p>Email: {{ userInfo.email }}</p>
      <p>Giới tính: {{ userInfo. }}</p>
      <p>Ngày sinh: {{ userInfo.birthday }}</p>
    </div>
    <div v-else>
      <p>Đang tải thông tin...</p>
    </div>
  </div>
</template>