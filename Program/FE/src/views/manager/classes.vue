<script setup>
import Sidebar from '@/components/SlidebarManager.vue';
import axios from 'axios';
import { onMounted, ref } from 'vue';


// API base URL
const API_BASE_URL = 'http://localhost:3001/manager';

const activeMenu = ref('classes');
const classes = ref([]);
const message = ref('');
const messageType = ref('');
const showAddForm = ref(false);
const showUpdateForm = ref(false);
const isLoading = ref(false);

// Form data để thêm lớp học mới
const formData = ref({
  sessionid: '',
  cufirstname: '',
  culastname: '',
  ptfirstname: '',
  ptlastname: '',
  exerciseType: ''
});

// Hiển thị thông báo
const showMessage = (text, type = 'success') => {
  message.value = text;
  messageType.value = type;

  // Auto-hide sau 5 giây
  setTimeout(() => {
    message.value = '';
  }, 5000);
};

// Xử lý lỗi API
const handleApiError = (error, defaultMessage) => {
  console.error(defaultMessage, error);
  const errorMessage = error.response?.data?.message || error.message || 'Đã xảy ra lỗi không xác định';
  showMessage(`${defaultMessage}: ${errorMessage}`, 'error');
};

// Lấy token từ localStorage
const getToken = () => {
  const token = localStorage.getItem('token');
  if (!token) {
    showMessage('Token không tồn tại. Vui lòng đăng nhập lại.', 'error');
    return null;
  }
  return token;
};

// Reset form
const resetAddForm = () => {
  formData.value = {
    customerid: '',
    ptid: '',
    exerciseType: ''
  };
  message.value = '';
};

const resetUpdateForm = () => {
  formData.value = {
    sessionid: '',
    cufirstname: '',
    culastname: '',
    ptfirstname: '',
    ptlastname: '',
    exerciseType: ''
  };
};

// Bật/tắt form thêm
const toggleAddForm = () => {
  showAddForm.value = !showAddForm.value;
  if (!showAddForm.value) {
    resetAddForm();
  }
};

const toggleUpdateForm = () => {
  showUpdateForm.value = !showUpdateForm.value;
  if (!showUpdateForm.value) {
    resetUpdateForm();
  }
};

// Thêm lớp học mới
const submitClass = async () => {
  try {
    // Validate form
    if (!formData.value.customerid || !formData.value.ptid ||
      !formData.value.exerciseType) {
      showMessage('Vui lòng điền đầy đủ thông tin buổi học', 'error');
      return;
    }

    isLoading.value = true;
    const token = getToken();
    if (!token) return;

    // Debug: Log what we're sending
    console.log('Adding class with data:', formData.value);

    const response = await axios.post(
      `${API_BASE_URL}/addExercise`,
      {
        customerid: formData.value.customerid,
        ptid: formData.value.ptid,
        exerciseType: formData.value.exerciseType
      },
      {
        headers: { token }
      }
    );

    // Handle successful response
    if (response.data && response.data.status === 'Thêm buổi tập thành công') {
      showMessage('Thêm buổi tập thành công!');
      resetAddForm();
      fetchClasses();
    } else {
      showMessage(response.data?.message || 'Thêm buổi tập không thành công', 'error');
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi thêm buổi học');
  } finally {
    isLoading.value = false;
  }
};

const updateClass = async () => {
  try {
    // Validate form
    if (!formData.value.sessionid || !formData.value.cufirstname || !formData.value.culastname ||
      !formData.value.ptfirstname || !formData.value.ptlastname ||
      !formData.value.exerciseType) {
      showMessage('Vui lòng điền đầy đủ thông tin buổi học', 'error');
      return;
    }

    isLoading.value = true;
    const token = getToken();
    if (!token) return;

    // Debug: Log what we're sending
    console.log('Updating class with data:', formData.value);

    const response = await axios.post(
      `${API_BASE_URL}/updateExercise`,
      {
        sessionid: formData.value.sessionid,
        cufirstname: formData.value.cufirstname,
        culastname: formData.value.culastname,
        ptfirstname: formData.value.ptfirstname,
        ptlastname: formData.value.ptlastname,
        exerciseType: formData.value.exerciseType
      },
      {
        headers: { token }
      }
    );

    // Handle successful response
    if (response.data && response.data.status === 'Sửa buổi tập thành công') {
      showMessage('Sửa buổi tập thành công');
      resetUpdateForm();
      fetchClasses();
    } else {
      showMessage(response.data?.message || 'Sửa buổi tập không thành công', 'error');
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi cập nhật buổi học');
  } finally {
    isLoading.value = false;
  }
};

// Fetch dữ liệu từ API khi component được mount
const fetchClasses = async () => {
  try {
    isLoading.value = true;
    // Lấy token từ localStorage
    const token = getToken();
    if (!token) return;

    // Gửi request đến server với token trong header
    const response = await axios.get(`${API_BASE_URL}/getExercise`, {
      headers: {
        token: token
      }
    });

    // Xử lý dữ liệu trả về từ server
    if (response.data && response.data.list) {
      classes.value = response.data.list.map(classe => ({
        sessionid: classe.sessionid,
        customer_name: classe.customer_name,
        trainer_name: classe.trainer_name,
        begin_time: classe.begin_time,
        end_time: classe.end_time,
        exercise_type: classe.exercise_type,
        description: classe.description
      }));
    } else {
      console.warn('Dữ liệu trả về không hợp lệ:', response.data);
      classes.value = [];
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi lấy danh sách lớp học');
    classes.value = [];
  } finally {
    isLoading.value = false;
  }
};

// Methods
const handleMenuClick = (menuId) => {
  activeMenu.value = menuId;
  console.log(`Menu clicked: ${menuId}`);
};

// Initialize data when component is mounted
onMounted(() => {
  fetchClasses();
});

</script>

<template>
  <div class="app-container">
    <!-- Sidebar -->
    <Sidebar :activeMenu="activeMenu" :onMenuClick="handleMenuClick" />

    <!-- Main Content -->
    <div class="main-content">
      <div class="header-actions">
        <h1 class="title">Danh sách buổi học của khách hàng</h1>

        <div class="formAction">
          <button @click="toggleAddForm" class="add-button">
            <span v-if="!showAddForm">+ Thêm buổi học mới</span>
            <span v-else>Ẩn form</span>
          </button>

          <button @click="toggleUpdateForm" class="update-button">
            <span v-if="!showUpdateForm"> Cập nhật buổi học</span>
            <span v-else>Ẩn form</span>
          </button>
        </div>
      </div>
      <!-- Hiển thị thông báo -->
      <div v-if="message" class="notification" :class="messageType">
        {{ message }}
      </div>

      <!-- Add Class Form -->
      <div v-if="showAddForm" class="form-container">
        <h2 class="form-title">Thêm buổi học mới</h2>

        <form @submit.prevent="submitClass" class="add-form">

          <div class="form-group">
            <label for="customerid">Mã khách hàng</label>
            <input type="text" id="customerid" v-model="formData.customerid" placeholder="Nhập mã khách hàng"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="ptid">Mã huấn luyện viên</label>
            <input type="text" id="ptid" v-model="formData.ptid" placeholder="Nhập mã huấn luyện viên"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="exerciseType">Loại bài tập</label>
            <input type="text" id="exerciseType" v-model="formData.exerciseType" placeholder="Nhập loại bài tập"
              class="form-control" required />
          </div>

          <div class="form-actions">
            <button type="button" @click="resetAddForm" class="reset-button">Đặt lại</button>
            <button type="submit" class="submit-button" :disabled="isLoading">
              <span v-if="isLoading">Đang thêm...</span>
              <span v-else>Thêm buổi học</span>
            </button>
          </div>
        </form>
      </div>
      <!-- form update-->
      <div v-if="showUpdateForm" class="form-container">
        <h2 class="form-title">Sửa buổi học</h2>

        <form @submit.prevent="updateClass" class="update-form">

          <div class="form-group">
            <label for="sessionid">Mã lớp học</label>
            <input type="text" id="sessionid" v-model="formData.sessionid" placeholder="Nhập mã lớp học"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="cufirstname">Họ khách hàng</label>
            <input type="text" id="cufirstname" v-model="formData.cufirstname" placeholder="Nhập họ khách hàng"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="culastname">Tên khách hàng</label>
            <input type="text" id="culastname" v-model="formData.culastname" placeholder="Nhập tên khách hàng"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="ptfirstname">Họ huấn luyện viên</label>
            <input type="text" id="ptfirstname" v-model="formData.ptfirstname" placeholder="Nhập họ huấn luyện viên"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="ptlastname">Tên huấn luyện viên</label>
            <input type="text" id="ptlastname" v-model="formData.ptlastname" placeholder="Nhập tên huấn luyện viên"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="exerciseType">Loại bài tập</label>
            <input type="text" id="exerciseType" v-model="formData.exerciseType" placeholder="Nhập loại bài tập"
              class="form-control" required />
          </div>

          <div class="form-actions">
            <button type="button" @click="resetUpdateForm" class="reset-button">Đặt lại</button>
            <button type="submit" class="submit-button" :disabled="isLoading">
              <span v-if="isLoading">Đang cập nhật...</span>
              <span v-else>Cập nhật</span>
            </button>
          </div>
        </form>
      </div>
      <!-- Loading indicator -->
      <div v-if="isLoading" class="loading-indicator">
        <div class="loader"></div>
        <div>Đang tải dữ liệu...</div>
      </div>

      <!-- Classes Table -->
      <div class="table-container" v-else>
        <table>
          <thead>
            <tr>
              <th>Mã lớp học</th>
              <th>Tên khách hàng</th>
              <th>Tên huấn luyện</th>
              <th>Thời gian bắt đầu</th>
              <th>Thời gian kết thúc</th>
              <th>Tên bài học</th>
              <th>Chi tiết</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(classe, index) in classes" :key="classe.sessionid">
              <td>{{ classe.sessionid }}</td>
              <td>{{ classe.customer_name }}</td>
              <td>{{ classe.trainer_name }}</td>
              <td>{{ classe.begin_time }}</td>
              <td>{{ classe.end_time }}</td>
              <td>{{ classe.exercise_type }}</td>
              <td>{{ classe.description }}</td>
            </tr>

            <!-- Hiển thị thông báo khi không có dữ liệu -->
            <tr v-if="classes.length === 0">
              <td colspan="6" class="no-data">Không có buổi học nào được tìm thấy</td>
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

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
  margin: 0;
}

.formAction {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 5px;
  margin-bottom: 20px;
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

.update-button {
  padding: 8px 16px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.update-button:hover {
  background-color: #2563eb;
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

/* Form container */
.form-container {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
  animation: slideDown 0.3s ease-in-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin-top: 0;
  margin-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 12px;
}

.add-form {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.update-form {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #4b5563;
  margin-bottom: 6px;
}

.form-control {
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
}

.form-actions {
  grid-column: span 2;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}

.reset-button {
  padding: 10px 20px;
  background-color: #f3f4f6;
  color: #4b5563;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.reset-button:hover {
  background-color: #e5e7eb;
}

.submit-button {
  padding: 10px 20px;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.submit-button:hover:not(:disabled) {
  background-color: #4338ca;
}

.submit-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
  overflow-x: auto;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 0;
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

.no-data {
  text-align: center;
  font-style: italic;
  color: #6b7280;
}

/* Responsive styling */
@media (max-width: 768px) {
  .main-content {
    margin-left: 0;
    padding: 15px;
  }

  .add-form {
    grid-template-columns: 1fr;
  }

  .form-actions {
    grid-column: 1;
  }
}
</style>