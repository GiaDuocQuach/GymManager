<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import SlidebarCustomer from '@/components/SlidebarCustomer.vue';

const activeMenu = ref('class');
const isLoading = ref(false);
const message = ref('');
const messageType = ref('');

const token = localStorage.getItem('token');
const userid = localStorage.getItem('userid');
const API_BASE_URL = 'http://localhost:3001/customer';

const classData = ref([]);


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

const fetchClassData = async () => {
    try {
        isLoading.value = true;
        if(!token) {
            showMessenger('Vui lòng đăng nhập lại', 'error');
            return;
        }
        
        const response = await axios.post(`${API_BASE_URL}/getSession`,
            { userid },
            { headers: { token } }
        );
        
        if(response.data.status === 'Thành công') {
                classData.value = response.data.data.map(session => ({
                    sessionid: session.sessionid,
                    trainer_name: session.trainer_name,
                    begin_time: session.begin_time,
                    end_time: session.end_time,
                    exercise_type: session.exercise_type,
                    description: session.description
                }));
        } else {
            showMessenger(data.message || 'Không có lịch học nào', 'error');
            classData.value = [];
        }
    } catch (error) {
        handleApiError(error, 'Lỗi khi lấy dữ liệu lớp học');
        classData.value = [];
    } finally {
        isLoading.value = false;
    }
};

onMounted(() => {
    fetchClassData();
});

const handleMenuClick = (menuId) => {
  activeMenu.value = menuId;
  console.log(`Menu clicked: ${menuId}`);
};

</script>

<template>
    <div class="app-container">
        <SlidebarCustomer :activeMenu="activeMenu" :onMenuClick="handleMenuClick"/>
        
        <div class="main-content">
            <h1 class="page-title">Lịch học của tôi</h1>
            
            <!-- Hiển thị thông báo -->
            <div v-if="message" class="notification" :class="messageType">
                {{ message }}
            </div>
            
            <!-- Loading indicator -->
            <div v-if="isLoading" class="loading-indicator">
                <div class="loader"></div>
                <div>Đang tải dữ liệu...</div>
            </div>
            
            <!-- Class Schedule Table -->
            <div v-else class="schedule-container">
                <div class="table-container">
                    <table v-if="classData.length > 0">
                        <thead>
                            <tr>
                                <th>Mã lớp học</th>
                                <th>Huấn luyện viên</th>
                                <th>Thời gian bắt đầu</th>
                                <th>Thời gian kết thúc</th>
                                <th>Loại bài tập</th>
                                <th>Chi tiết</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(session, index) in classData" :key="session.sessionid">
                                <td>{{ session.sessionid }}</td>
                                <td>{{ session.trainer_name }}</td>
                                <td>{{ session.begin_time }}</td>
                                <td>{{ session.end_time }}</td>
                                <td>{{ session.exercise_type }}</td>
                                <td>{{ session.description }}</td>
                            </tr>
                        </tbody>
                    </table>
                    
                    <!-- Hiển thị khi không có lịch học -->
                    <div v-else class="empty-state">
                        <div class="empty-icon">
                            <i class="fas fa-calendar-times"></i>
                        </div>
                        <h3>Chưa có lịch học nào</h3>
                        <p>Hiện tại bạn chưa có buổi học nào được lên lịch.</p>
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
    font-size: 24px;
    font-weight: 700;
    color: #111827;
    margin-bottom: 24px;
}

/* Notification styling */
.notification {
    padding: 12px 16px;
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

/* Schedule container styling */
.schedule-container {
    margin-top: 20px;
}

.table-container {
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    overflow: hidden;
}

table {
    width: 100%;
    border-collapse: collapse;
}

thead {
    background-color: #f3f4f6;
}

th {
    padding: 14px 16px;
    text-align: center;
    font-size: 14px;
    font-weight: 600;
    color: #374151;
    border-bottom: 1px solid #e5e7eb;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

td {
    padding: 16px;
    text-align: center;
    font-size: 14px;
    color: #4b5563;
    border-bottom: 1px solid #e5e7eb;
}

tr:hover {
    background-color: #f9fafb;
}

/* Empty state styling */
.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    text-align: center;
}

.empty-icon {
    font-size: 48px;
    color: #d1d5db;
    margin-bottom: 16px;
}

.empty-state h3 {
    font-size: 18px;
    font-weight: 600;
    color: #374151;
    margin-bottom: 8px;
}

.empty-state p {
    color: #6b7280;
    max-width: 400px;
}

/* Responsive styling */
@media (max-width: 768px) {
    .main-content {
        margin-left: 0;
        width: 100%;
        padding: 16px;
    }
    
    th, td {
        padding: 12px 8px;
        font-size: 13px;
    }
    
    .table-container {
        overflow-x: auto;
    }
}
</style>