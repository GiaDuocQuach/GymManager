<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import SlidebarPT from '@/components/SlidebarPT.vue';

const activeMenu = ref('infopt')
const isLoading = ref(false);
const message = ref('');
const messageType = ref('');
const token = localStorage.getItem('token');
const userid = localStorage.getItem('userid');
const API_BASE_URL = 'http://localhost:3001/pt';
const infoData = ref ({
    ptId: '',
    name: '',
    phone: '',
    email: '',
    age: '',
    gender: '',
    rank: '',
    avatar: null
});

const showMessenger = (text , type = 'success') => {
    message.value = text;
    messageType.value = type;

    setTimeout(() =>{
        message.value = '';
    }, 5000);
}

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
            infoData.value = {
                name: info.name,
                phone: info.phone,
                email: info.email,
                age: info.age,
                gender: info.gender,
                rank: info.rank,
                ptId: info.ptId
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

onMounted(() => {
    fetchPtData()
});

const handleMenuClick = (menuId) => {
  activeMenu.value = menuId;
  console.log(`Menu clicked: ${menuId}`);
};

</script>

<template>
  <div class="app-container">
    <SlidebarPT :activeMenu="activeMenu" @menuClick="handleMenuClick" />

    <div class="main-content">
      <h1 class="page-title">Thông tin cá nhân</h1>

      <!-- Thông báo -->
      <div v-if="message" class="notification" :class="messageType">
        {{ message }}
      </div>

      <!-- Loading indicator -->
      <div v-if="isLoading" class="loading-indicator">
        <div class="loader"></div>
        <div>Đang tải dữ liệu...</div>
      </div>

      <div v-else class="profile-container">
        <!-- Section: Thông tin cá nhân -->
        <div class="profile-section">

          <div class="profile-body">
            <!-- Avatar section -->
            <div class="avatar-section">
              <div class="avatar-container">
                <img v-if="infoData.avatar" :src="infoData.avatar" alt="Avatar" class="avatar" />
                <div v-else class="avatar default-avatar">
                  {{ infoData.name ? infoData.name.charAt(0).toUpperCase() : 'U' }}
                </div>
              </div>
            </div>

            <!-- Info section -->
            <div class="info-section">
              <div class="info-view">
                <div class="info-row">
                  <div class="info-label">Mã huấn luyện viên</div>
                  <div class="info-value">{{ infoData.ptId }}</div>
                </div>
                <div class="info-row">
                  <div class="info-label">Họ và tên</div>
                  <div class="info-value">{{ infoData.name }}</div>
                </div>
                <div class="info-row">
                  <div class="info-label">Email</div>
                  <div class="info-value">{{ infoData.email }}</div>
                </div>
                <div class="info-row">
                  <div class="info-label">Số điện thoại</div>
                  <div class="info-value">{{ infoData.phone }}</div>
                </div>
                <div class="info-row">
                  <div class="info-label">Tuổi</div>
                  <div class="info-value">{{ infoData.age }}</div>
                </div>
                <div class="info-row">
                  <div class="info-label">Giới tính</div>
                  <div class="info-value">{{ infoData.gender }}</div>
                </div>
                <div class="info-row">
                  <div class="info-label">Cấp bậc</div>
                  <div class="info-value">{{ infoData.rank }}</div>
                </div>
              </div>
            </div>
          </div>
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

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 32px;
}

/* Notification styling */
.notification {
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  font-size: 16px;
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
  height: 60vh;
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

/* Profile container */
.profile-container {
  max-width: 900px;
  margin: 0 auto;
}

/* Removed card styling and box-shadow */
.profile-section {
  padding-bottom: 40px;
}


/* Profile body */
.profile-body {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 40px;
}

/* Avatar section */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-container {
  position: relative;
  width: 180px; /* Increased size further */
  height: 180px; /* Increased size further */
  border-radius: 50%;
  overflow: hidden;
  background-color: #f3f4f6;
  margin-bottom: 16px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-avatar {
  width: 100%;
  height: 100%;
  background-color: #4f46e5;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 72px; /* Increased size */
  font-weight: 600;
}

/* Info section */
.info-section {
  flex: 1;
}

.info-view {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

.info-row {
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #e5e7eb;
}

.info-label {
  font-size: 18px; /* Increased size more */
  color: #6b7280;
  margin-bottom: 8px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.info-value {
  font-size: 24px; /* Increased size more */
  font-weight: 500;
  color: #1f2937;
}

/* Responsive */
@media (max-width: 768px) {
  .main-content {
    margin-left: 0;
    width: 100%;
    padding: 16px;
  }

  .profile-body {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .info-view {
    grid-template-columns: 1fr;
  }
  
  .avatar-section {
    margin-bottom: 24px;
  }
}
</style>