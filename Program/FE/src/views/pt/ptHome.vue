<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import SlidebarPT from '@/components/SlidebarPT.vue';

const activeMenu = ref('ptHome');
const isLoading = ref(false);
const message = ref('');
const messageType = ref('');
const token = localStorage.getItem('token');
const userid = localStorage.getItem('userid');
const API_BASE_URL = 'http://localhost:3001/pt';
const currentTime = ref('');
const currentDate = ref('');

const ptData = ref({
    name: ''
});

const showMessenger = (text, type = 'success') => {
    message.value = text;
    messageType.value = type;

    // Tự động ẩn sau 5 giây
    setTimeout(() => {
        message.value = '';
    }, 5000);
};

const handleApiError = (error, defaultMessage) => {
    console.error(defaultMessage, error);
    const errorMessage = error.response?.data?.message || error.message || 'Đã xảy ra lỗi không xác định';
    showMessenger(`${defaultMessage}: ${errorMessage}`, 'error');
};

const fetchPtData = async () => {
    try {
        isLoading.value = true;
        if(!token) {
            showMessenger('Vui lòng đăng nhập lại', 'error');
            return;
        }
        
        const response = await axios.post(`${API_BASE_URL}/getInfoPt`,
            { userid },
            { headers: { token } }
        );
        const info = response.data;
        if(info.status === 'success') {
            ptData.value = {
                name: info.name
            };
        } else {
            showMessenger(data.message || 'Không có dữ liệu huấn luyện viên', 'error');
        }
    } catch (error) {
        handleApiError(error, 'Lỗi khi lấy dữ liệu huấn luyện viên');
    } finally {
        isLoading.value = false;
    }
};

const updateCurrentTime = () => {
    const now = new Date();
    
    // Format date: Thứ Hai, 23 tháng 5, 2025
    const weekdays = ['Chủ Nhật', 'Thứ Hai', 'Thứ Ba', 'Thứ Tư', 'Thứ Năm', 'Thứ Sáu', 'Thứ Bảy'];
    const day = weekdays[now.getDay()];
    const date = now.getDate();
    const month = now.getMonth() + 1;
    const year = now.getFullYear();
    currentDate.value = `${day}, ${date} tháng ${month}, ${year}`;
    
    // Format time: 15:30:45
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    currentTime.value = `${hours}:${minutes}:${seconds}`;
};

onMounted(() => {
    fetchPtData();
    updateCurrentTime();
    
    setInterval(updateCurrentTime, 1000);
});

const handleMenuClick = (menuId) => {
  activeMenu.value = menuId;
  console.log(`Menu clicked: ${menuId}`);
};
</script>

<template>
  <div class="pt-dashboard">
    <SlidebarPT :activeMenu="activeMenu" @menuClick="handleMenuClick"/>
    
    <div class="content-area">
      <!-- Thông báo -->
      <div v-if="message" :class="['message-alert', `alert-${messageType}`]">
        {{ message }}
      </div>
      
      <!-- Loading indicator -->
      <div v-if="isLoading" class="loading-container">
        <div class="spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>
      
      <div v-else>
        <!-- Welcome Banner -->
        <div class="welcome-banner">
          <div class="welcome-content">
            <div class="welcome-text">
              <h1>Xin chào, <span class="highlighted">{{ptData.name}}</span>!</h1>
              <p class="welcome-message">Chúc bạn một ngày làm việc hiệu quả</p>
            </div>
            
            <div class="time-display">
              <div class="date">{{ currentDate }}</div>
              <div class="time">{{ currentTime }}</div>
            </div>
          </div>
        </div>
        
        <!-- Dashboard Content -->
        <div class="dashboard-content">
          <div class="empty-dashboard">
            <i class="fas fa-dumbbell"></i>
            <h2>Sẵn sàng bắt đầu ngày mới</h2>
            <p>Sử dụng menu bên trái để quản lý lịch tập, khách hàng và các hoạt động khác</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.content-area {
  flex: 1;
  padding: 25px;
  margin-left: 240px; /* Adjust based on your sidebar width */
}

/* Message Alert */
.message-alert {
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 15px;
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

.alert-success {
  background-color: #d1fae5;
  color: #065f46;
  border-left: 4px solid #10b981;
}

.alert-error {
  background-color: #fee2e2;
  color: #b91c1c;
  border-left: 4px solid #ef4444;
}

/* Loading Container */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 4px solid #3498db;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Welcome Banner */
.welcome-banner {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.welcome-text h1 {
  font-size: 2rem;
  color: #1e293b;
  margin: 0 0 8px 0;
  font-weight: 700;
}

.welcome-message {
  color: #64748b;
  font-size: 1.1rem;
  margin: 0;
}

.highlighted {
  color: #3498db;
}

.time-display {
  text-align: right;
}

.date {
  font-size: 1rem;
  color: #64748b;
  margin-bottom: 5px;
}

.time {
  font-size: 2.2rem;
  font-weight: 700;
  color: #1e293b;
  font-variant-numeric: tabular-nums;
}

/* Dashboard Content */
.dashboard-content {
  margin-top: 40px;
}

.empty-dashboard {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 80px 0;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.empty-dashboard i {
  font-size: 5rem;
  color: #3498db;
  margin-bottom: 20px;
  opacity: 0.7;
}

.empty-dashboard h2 {
  font-size: 1.5rem;
  color: #1e293b;
  margin-bottom: 10px;
}

.empty-dashboard p {
  color: #64748b;
  max-width: 500px;
  line-height: 1.6;
}

/* Responsive styles */
@media (max-width: 768px) {
  .content-area {
    margin-left: 0;
    padding: 15px;
  }
  
  .welcome-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .time-display {
    text-align: left;
    width: 100%;
    margin-top: 15px;
  }
  
  .welcome-text h1 {
    font-size: 1.5rem;
  }
  
  .time {
    font-size: 1.8rem;
  }
}
</style>