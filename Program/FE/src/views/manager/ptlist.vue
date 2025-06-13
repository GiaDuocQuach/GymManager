<script setup>
import { ref, onMounted } from 'vue';
import SlidebarManager from '@/components/SlidebarManager.vue';
import axios from 'axios';

const activeMenu = ref('ptlist');
const isLoading = ref(false);
const message = ref('');
const messageType = ref('');
const ptList = ref([]);

// Các rank PT
const ptRanks = [
  { value: 'Sơ cấp', color: '#22c55e' },   // Xanh lá
  { value: 'Trung cấp', color: '#3b82f6' }, // Xanh dương
  { value: 'Nâng cao', color: '#f59e0b' },    // Cam
  { value: 'Chuyên gia', color: '#ef4444' }       // Đỏ
];

// Show notification message
const showMessage = (text, type = 'success') => {
  message.value = text;
  messageType.value = type;

  // Auto-hide message after 5 seconds
  setTimeout(() => {
    message.value = '';
  }, 5000);
};

// Handle API errors
const handleApiError = (error, defaultMessage) => {
  console.error(defaultMessage, error);
  const errorMessage = error.response?.data?.message || error.message || 'Đã xảy ra lỗi không xác định';
  showMessage(`${defaultMessage}: ${errorMessage}`, 'error');
};

// Lấy danh sách PT từ API
const fetchPTList = async () => {
  try {
    isLoading.value = true;
    const token = localStorage.getItem('token');
    if (!token) {
      showMessage('Token không tồn tại. Vui lòng đăng nhập lại.', 'error');
      return;
    }

    const response = await axios.get('http://localhost:3001/admin/getAllPT', {
      headers: { token }
    });

    // Giả định: API trả về mảng PT trong response.data.list
    ptList.value = response.data.list.map(pt => ({
      id: pt.id,
      name: pt.name,
      email: pt.email,
      phone: pt.phone,
      rank: pt.rank
    }));
    
  } catch (error) {
    handleApiError(error, 'Lỗi khi lấy danh sách PT');
  } finally {
    isLoading.value = false;
  }
};

// Lấy màu hiển thị cho rank
const getRankColor = (rank) => {
  const foundRank = ptRanks.find(r => r.value === rank);
  return foundRank ? foundRank.color : '#6b7280'; // Màu mặc định nếu không tìm thấy
};

// Chuyển trang menu
const handleMenuClick = (menuId) => {
  activeMenu.value = menuId;
  console.log(`Menu clicked: ${menuId}`);
};

// Lấy dữ liệu khi component được mount
onMounted(() => {
  fetchPTList();
});
</script>

<template>
  <div class="app-container">
    <!-- Sidebar -->
    <SlidebarManager :activeMenu="activeMenu" :onMenuClick="handleMenuClick" />

    <!-- Main Content -->
    <div class="main-content">
      <div class="header-actions">
        <h1 class="title">Danh sách Huấn luyện viên (PT)</h1>
      </div>

      <!-- Hiển thị thông báo -->
      <div v-if="message" class="notification" :class="messageType">
        {{ message }}
      </div>

      <!-- Loading indicator -->
      <div v-if="isLoading" class="loading-indicator">
        <div class="loader"></div>
        <div>Đang tải dữ liệu...</div>
      </div>
        
      <!-- PT Table -->
      <div v-else class="table-container">
        <table class="pt-table">
          <thead>
            <tr>
              <th>Mã huấn luyện viên</th>
              <th>Họ tên</th>
              <th>Email</th>
              <th>Số điện thoại</th>
              <th>Cấp bậc</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(pt) in ptList" :key="pt.id">
              <td>{{ pt.id }}</td>
              <td>{{ pt.name }}</td>
              <td>{{ pt.email }}</td>
              <td>{{ pt.phone }}</td>
              <td>
                <span class="rank-badge" :style="{backgroundColor: getRankColor(pt.rank)}">
                  {{ pt.rank }}
                </span>
              </td>
              
            </tr>
            <!-- Hiển thị thông báo khi không có dữ liệu -->
            <tr v-if="ptList.length === 0">
              <td colspan="7" class="no-data">Không có PT nào được tìm thấy</td>
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
  padding: 24px;
  background-color: #f9fafb;
  min-height: 100vh;
  width: calc(100% - 250px);
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.add-button {
  padding: 8px 16px;
  background-color: #10b981;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.add-button:hover {
  background-color: #059669;
}

/* Notification styling */
.notification {
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 16px;
  font-size: 14px;
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.notification.success {
  background-color: #d1fae5;
  color: #065f46;
  border-left: 4px solid #10b981;
}

.notification.error {
  background-color: #fee2e2;
  color: #b91c1c;
  border-left: 4px solid #ef4444;
}

/* Loading indicator */
.loading-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #6b7280;
}

.loader {
  border: 4px solid #f3f4f6;
  border-top: 4px solid #4f46e5;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Table styling */
.table-container {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
}

.pt-table {
  width: 100%;
  border-collapse: collapse;
}

.pt-table thead {
  background-color: #f3f4f6;
}

.pt-table th {
  padding: 12px 16px;
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  border-bottom: 2px solid #e5e7eb;
}

.pt-table td {
  padding: 12px 16px;
  text-align: center;
  border-bottom: 1px solid #e5e7eb;
  font-size: 14px;
  color: #4b5563;
}

.pt-table tbody tr:hover {
  background-color: #f9fafb;
}

/* Hiển thị khi không có dữ liệu */
.no-data {
  text-align: center;
  padding: 20px;
  color: #6b7280;
  font-style: italic;
}

/* Avatar styling */
.avatar-cell {
  text-align: center;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-avatar {
  background-color: #4f46e5;
  color: white;
  font-weight: 600;
  font-size: 16px;
}

/* Rank badge styling */
.rank-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
  font-weight: 500;
}

/* Action buttons */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.view-button, .edit-button {
  padding: 6px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.view-button {
  background-color: #e0f2fe;
  color: #0369a1;
}

.view-button:hover {
  background-color: #bae6fd;
  color: #0284c7;
}

.edit-button {
  background-color: #e0f2fe;
  color: #0369a1;
}

.edit-button:hover {
  background-color: #bae6fd;
  color: #0284c7;
}


/* Responsive design */
@media (max-width: 1024px) {
  .pt-table {
    min-width: 800px;
  }
}
</style>