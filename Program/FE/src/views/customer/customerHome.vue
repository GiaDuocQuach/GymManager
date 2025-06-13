<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';
import SlidebarCustomer from '@/components/SlidebarCustomer.vue';

const activeMenu = ref('dashboard');
const isLoading = ref(false);
const currentDate = ref(new Date());

const customerData = ref({
  customerId: '',
  name: '',
  email: '',
  phone: '',
  age: '',
  gender: '',
  avatar: null
});

const token = localStorage.getItem('token');
const userid = localStorage.getItem('userid');

// Hiển thị thông báo
const message = ref('');
const messageType = ref('');

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

// Fetch customer data
const fetchCustomerData = async () => {
  try {
    isLoading.value = true;
    if (!token) {
      showMessage('Vui lòng đăng nhập lại', 'error');
      return;
    }
    const customerResponse = await axios.post(
      'http://localhost:3001/customer/getCustomer', 
      { userid}, 
      { headers: { token } }
    );

    // Xử lý dữ liệu khách hàng
    const info = customerResponse.data;
    if (info.status === 'success') {
      customerData.value = {
        customerId: info.customerId,
        name: info.name,
        email: info.email,
        phone: info.phone,
        age: info.age,
        gender: info.gender
      };
    } else {
      showMessage(info.message || 'Không thể lấy thông tin khách hàng', 'error');
    }
    
  } catch (error) {
    handleApiError(error, 'Không thể tải thông tin khách hàng');
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchCustomerData();
  

  setInterval(() => {
    currentDate.value = new Date();
  }, 60000);
});

const handleMenuClick = (menuId) => {
  activeMenu.value = menuId;
  console.log(`Menu clicked: ${menuId}`);
};

</script>

<template>
  <div class="app-container">
    <!-- Sidebar -->
    <SlidebarCustomer :activeMenu="activeMenu" :onMenuClick = "handleMenuClick" />
    
    <!-- Main Content -->
    <div class="main-content">
      <!-- Hiển thị thông báo -->
      <div v-if="message" class="notification" :class="messageType">
        {{ message }}
      </div>
    
      <!-- Loading Indicator -->
      <div v-if="isLoading" class="loading-container">
        <div class="loader"></div>
        <p>Đang tải dữ liệu...</p>
      </div>
      
      <!-- Simplified Dashboard Content -->
      <div v-else class="dashboard">
        <!-- Welcome Section -->
        <div class="welcome-section">
          <div class="date-time">
            <div class="current-time">{{ currentDate.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) }}</div>
            <div class="current-date">{{ currentDate.toLocaleDateString('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }) }}</div>
          </div>
          <h1 class="welcome-text">
            Xin chào, <span class="customer-name">{{ customerData.name }}</span>!
          </h1>
          <p class="welcome-message">
            Chào mừng bạn quay trở lại.
          </p>
        </div>
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

/* Notification styling */
.notification {
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 20px;
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
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80vh;
}

.loader {
  border: 4px solid #f3f4f6;
  border-top: 4px solid #4f46e5;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Dashboard layout */
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* Welcome Section */
.welcome-section {
  background-color: white;
  border-radius: 12px;
  padding: 40px;
  position: relative;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  background-image: linear-gradient(135deg, #f5f7fa 0%, #e4e7f0 100%);
  overflow: hidden;
  text-align: center;
}

.welcome-section::before {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  width: 20%;
  height: 100%;
  background-image: url('https://raw.githubusercontent.com/your-repo/images/main/gym-pattern.svg');
  opacity: 0.1;
  background-size: cover;
}

.welcome-text {
  font-size: 36px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 16px;
}

.customer-name {
  color: #4f46e5;
}

.welcome-message {
  font-size: 18px;
  color: #4b5563;
  max-width: 600px;
  margin: 0 auto;
}

.date-time {
  margin-bottom: 30px;
  text-align: center;
}

.current-time {
  font-size: 48px;
  font-weight: 700;
  color: #1f2937;
}

.current-date {
  font-size: 18px;
  color: #6b7280;
  margin-top: 8px;
}

/* Responsive */
@media (max-width: 768px) {
  .main-content {
    margin-left: 0;
    width: 100%;
    padding: 16px;
  }

  .welcome-section {
    padding: 20px;
  }

  .current-time {
    font-size: 32px;
  }

  .current-date {
    font-size: 14px;
  }

  .welcome-text {
    font-size: 28px;
  }

  .welcome-message {
    font-size: 16px;
  }
}
</style>