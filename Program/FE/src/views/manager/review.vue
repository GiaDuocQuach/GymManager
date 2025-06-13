<script setup>
import Sidebar from '@/components/SlidebarManager.vue'; // Import Sidebar component
import axios from 'axios';
import { onMounted, ref } from 'vue';

// State
const activeMenu = ref('review');
const reviews = ref([]);

// Hàm format ngày (ví dụ: 27/04/2025)
function formatDate(dateString) {
  const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
  return new Date(dateString).toLocaleDateString('vi-VN', options);
}

// Fetch dữ liệu từ API khi component được mount
onMounted(async () => {
  try {
    // Lấy token từ localStorage (hoặc sessionStorage/cookie)
    const token = localStorage.getItem('token'); // Thay đổi nếu token được lưu ở nơi khác

    if (!token) {
      console.error('Token không tồn tại. Vui lòng đăng nhập.');
      return;
    }

    // Gửi request đến server với token trong header
    const response = await axios.get('http://localhost:3001/manager/getReviews', {
      headers: {
        token:  token // Gửi token trong header
      }
    });

    // Xử lý dữ liệu trả về từ server
    reviews.value = response.data.data.map(review => ({
      customerName: review.customer,
      post: review.text,
      createdAt: formatDate(review.date)
    }));
  } catch (error) {
    console.error('Lỗi khi lấy danh sách review:', error);
  }
});

// Methods
const handleMenuClick = (menuId) => {
  activeMenu.value = menuId;
  console.log(`Menu clicked: ${menuId}`);
};
</script>

<template>
  <div class="app-container">
    <!-- Sidebar -->
    <Sidebar :activeMenu="activeMenu" :onMenuClick="handleMenuClick" />

    <!-- Main Content -->
    <div class="main-content">
      <h1 class="title">Danh sách đánh giá của khách hàng</h1>

      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>Tên khách hàng</th>
              <th>Nội dung đánh giá</th>
              <th>Ngày đăng</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(review, index) in reviews" :key="index">
              <td>{{ review.customerName }}</td>
              <td>{{ review.post }}</td>
              <td>{{ review.createdAt }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
}

.main-content {
  margin-left: 250px;
  padding: 20px;
  background-color: #f9fafb;
  min-height: 100vh;
  width: 100%;
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #1f2937;
}

.table-container {
  overflow-x: auto;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

thead {
  background-color: #f3f4f6;
}

th {
  text-align: center;
  padding: 12px;
  font-size: 14px;
  font-weight: bold;
  color: #374151;
  border-bottom: 2px solid #e5e7eb;
}

td {
  text-align: center;
  padding: 12px;
  font-size: 14px;
  color: #4b5563;
  border-bottom: 1px solid #e5e7eb;
}

tr:hover {
  background-color: #f9fafb;
}
/* Thiết lập độ rộng cố định cho từng cột */
th:nth-child(1), td:nth-child(1) { 
  width: 33.33%; 
}
th:nth-child(2), td:nth-child(2) { 
  width: 33.33%; 
}
th:nth-child(3), td:nth-child(3) { 
  width: 33.33%; 
}
</style>