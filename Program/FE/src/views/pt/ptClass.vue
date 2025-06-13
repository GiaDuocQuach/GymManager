<script setup>
import SlidebarPT from '@/components/SlidebarPT.vue';
import axios from 'axios';
import { onMounted, ref } from 'vue';

const activeMenu = ref('ptclass');
const isLoading = ref(false);
const message = ref('');
const messageType = ref('');
const token = localStorage.getItem('token');
const userid = localStorage.getItem('userid');
const API_BASE_URL = 'http://localhost:3001/pt';
const classData = ref([]);

// Thêm biến để quản lý chức năng chỉnh sửa
const editingSession = ref(null);
const editFormData = ref({
  sessionid: '',
  begin_time: '',
  end_time: '',
  description: ''
});
const isSubmitting = ref(false);

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

// lấy dữ liệu lớp học
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
        
        if(response.data.status === 'success') {
                classData.value = response.data.data.map(session => ({
                    sessionid: session.sessionid,
                    customer_name: session.customer_name,
                    begin_time: session.begin_time,
                    end_time: session.end_time,
                    exercise_type: session.exercise_type,
                    description: session.description
                }));
        } else {
            showMessenger(response.data.message || 'Không có lịch học nào', 'error');
            classData.value = [];
        }
    } catch (error) {
        handleApiError(error, 'Lỗi khi lấy dữ liệu lớp học');
    } finally {
        isLoading.value = false;
    }
};

// Hàm mở form chỉnh sửa cho một buổi học
const startEditing = (session) => {
    editingSession.value = session.sessionid;
    editFormData.value = {
        sessionid: session.sessionid,
        begin_time: session.begin_time,
        end_time: session.end_time,
        description: ''
    };
};

// Hàm hủy chỉnh sửa
const cancelEditing = () => {
    editingSession.value = null;
    editFormData.value = {
        sessionid: '',
        begin_time: '',
        end_time: '',
        description: ''
    };
};

// Hàm lưu thông tin đã chỉnh sửa
const saveSessionChanges = async () => {
    try {
        isSubmitting.value = true;
        
        if(!token) {
            showMessenger('Vui lòng đăng nhập lại', 'error');
            return;
        }
        
        // Kiểm tra dữ liệu nhập
        if (!editFormData.value.begin_time || !editFormData.value.end_time) {
            showMessenger('Vui lòng nhập đầy đủ thời gian bắt đầu và kết thúc', 'error');
            return;
        }
        
        // Gửi yêu cầu cập nhật lên server
        const response = await axios.post(`${API_BASE_URL}/updateSession`,
            {
                userid,
                sessionid: editFormData.value.sessionid,
                beginAt:editFormData.value.begin_time.replace('T', ' '),
                endAt: editFormData.value.end_time.replace('T', ' '),
                description: editFormData.value.description
            },
            { headers: { token } }
        );
        if(response.data.status === 'success') {  
            showMessenger('Cập nhật thông tin buổi học thành công', 'success');
            cancelEditing();
            fetchClassData();
            
        } else {
            showMessenger(response.data.message || 'Cập nhật không thành công', 'error');
        }
    } catch (error) {
        handleApiError(error, 'Lỗi khi cập nhật thông tin buổi học');
    } finally {
        isSubmitting.value = false;
    }
};


onMounted(() => {
    fetchClassData();
});

</script>

<template>
  <div class="class-page">
    <SlidebarPT :activeMenu="activeMenu" />
    
    <div class="content-area">
      <!-- Header -->
      <div class="header-section">
        <h1 class="page-title">Lịch dạy</h1>
      </div>
      
      <!-- Notification -->
      <div v-if="message" :class="['message-alert', `alert-${messageType}`]">
        {{ message }}
      </div>
      
      <!-- Loading indicator -->
      <div v-if="isLoading" class="loading-container">
        <div class="spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>
      
      <!-- Main content -->
      <div v-else class="class-container">
        
        <!-- Class list -->
        <div v-if="classData.length > 0" class="class-list">
          <div class="table-responsive">
            <table class="class-table">
              <thead>
                <tr>
                  <th class="col-id">Mã lớp học</th>
                  <th class="col-customer">Khách hàng</th>
                  <th class="col-time">Thời gian bắt đầu</th>
                  <th class="col-time">Thời gian kết thúc</th>
                  <th class="col-type">Loại bài tập</th>
                  <th class="col-desc">Mô tả</th>
                  <th class="col-actions">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="session in classData" :key="session.sessionid" class="class-row">
                  <!-- Chế độ hiển thị -->
                  <template v-if="editingSession !== session.sessionid">
                    <td class="col-id">{{ session.sessionid }}</td>
                    <td class="col-customer">{{ session.customer_name }}</td>
                    <td class="col-time">{{ session.begin_time }}</td>
                    <td class="col-time">{{ session.end_time }}</td>
                    <td class="col-type">
                      <span class="exercise-badge">{{ session.exercise_type }}</span>
                    </td>
                    <td class="col-desc">{{ session.description || 'Không có mô tả' }}</td>
                    <td class="col-actions">
                      <div class="actions-wrapper">
                        <button class="action-btn edit-btn" @click="startEditing(session)" title="Chỉnh sửa">
                          <i class="fas fa-edit"></i>Sửa
                        </button>
                      </div>
                    </td>
                  </template>
                  
                  <!-- Chế độ chỉnh sửa -->
                  <template v-else>
                    <td class="col-id">{{ session.sessionid }}</td>
                    <td class="col-customer">{{ session.customer_name }}</td>
                    <td class="col-time edit-cell">
                      <input 
                        type="datetime-local" 
                        class="form-input" 
                        v-model="editFormData.begin_time"
                        value="session.begin_time"
                      />
                    </td>
                    <td class="col-time edit-cell">
                      <input 
                        type="datetime-local" 
                        class="form-input" 
                        v-model="editFormData.end_time"
                        value="session.end_time"
                      />
                    </td>
                    <td class="col-type">
                      <span class="exercise-badge">{{ session.exercise_type }}</span>
                    </td>
                    <td class="col-desc edit-cell">
                      <textarea 
                        class="form-textarea" 
                        v-model="editFormData.description" 
                        placeholder="Nhập mô tả"
                      ></textarea>
                    </td>
                    <td class="col-actions edit-cell">
                      <div class="edit-actions">
                        <button class="save-btn" @click="saveSessionChanges" :disabled="isSubmitting">
                          <i class="fas fa-check"></i> Lưu
                        </button>
                        <button class="cancel-btn" @click="cancelEditing">
                          <i class="fas fa-times"></i> Hủy
                        </button>
                      </div>
                    </td>
                  </template>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <!-- Empty state -->
        <div v-else class="empty-state">
          <div class="empty-icon">
            <i class="fas fa-calendar-alt"></i>
          </div>
          <h3 class="empty-title">Không có lịch học nào</h3>
          <p class="empty-desc">Bạn chưa có buổi học nào được lên lịch.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.class-page {
  display: flex;
  min-height: 100vh;
  background-color: #f8f9fa;
}

.content-area {
  flex: 1;
  padding: 24px;
  margin-left: 240px; /* Adjust based on sidebar width */
}

/* Header */
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}


/* Message alert */
.message-alert {
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  font-size: 15px;
  animation: fadeIn 0.3s ease;
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

/* Loading */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 4px solid #3498db;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Class container */
.class-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* Class list */
.class-list {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.table-responsive {
  overflow-x: auto;
  width: 100%;
}

.class-table {
  width: 100%;
  border-collapse: collapse;
}

.class-table th,
.class-table td {
  padding: 16px;
  text-align: left;
}

.class-table th {
  background-color: #f8fafc;
  color: #64748b;
  font-weight: 600;
  font-size: 14px;
  border-bottom: 1px solid #e2e8f0;
}

.class-row {
  border-bottom: 1px solid #e2e8f0;
  transition: background-color 0.2s;
}

.class-row:last-child {
  border-bottom: none;
}

.class-row:hover {
  background-color: #f8fafc;
}

.col-id {
  width: 80px;
  color: #64748b;
  font-weight: 600;
}

.col-customer {
  font-weight: 500;
}

.col-time {
  width: 180px;
  color: #1e293b;
}

.col-type {
  width: 150px;
}

.exercise-badge {
  display: inline-block;
  padding: 6px 12px;
  background-color: #e0f2fe;
  color: #0284c7;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.col-desc {
  max-width: 300px;
  color: #64748b;
}

.col-actions {
  width: 120px;
}

.actions-wrapper {
  display: flex;
  gap: 7px;
}

.action-btn {
  padding: 8px 12px;
  border-radius: 6px;
  border: none;
  background-color: rgb(105, 105, 236);
  color:white;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.edit-btn:hover {
  background-color: #e0f2fe;
  color: #0284c7;
}

/* Edit mode styles */
.edit-cell {
  padding: 8px;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.form-textarea {
  min-height: 80px;
  resize: vertical;
}

.edit-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.save-btn,
.cancel-btn {
  padding: 8px 12px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.save-btn {
  background-color: #10b981;
  color: white;
}

.save-btn:hover {
  background-color: #059669;
}

.save-btn:disabled {
  background-color: #94a3b8;
  cursor: not-allowed;
}

.cancel-btn {
  background-color: #f1f5f9;
  color: #64748b;
}

.cancel-btn:hover {
  background-color: #e2e8f0;
  color: #475569;
}

/* Empty state */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  text-align: center;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  width: 80px;
  height: 80px;
  background-color: #f1f5f9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.empty-icon i {
  font-size: 32px;
  color: #94a3b8;
}

.empty-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.empty-desc {
  font-size: 15px;
  color: #64748b;
  margin: 0;
}

/* Responsive styles */
@media (max-width: 768px) {
  .content-area {
    margin-left: 0;
    padding: 16px;
  }
  
  .header-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
</style>

<script>
// Hàm định dạng ngày giờ để hiển thị
function formatDisplayDateTime(dateTimeStr) {
  if (!dateTimeStr) return 'N/A';
  
  try {
    const date = new Date(dateTimeStr);
    if (isNaN(date)) return 'N/A';
    
    // Format: DD/MM/YYYY HH:MM
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${day}/${month}/${year} ${hours}:${minutes}`;
  } catch (e) {
    console.error('Error formatting date:', e);
    return 'N/A';
  }
}
</script>