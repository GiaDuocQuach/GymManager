<script setup>
import Slidebar from '@/components/SlidebarManager.vue'; // Import Slidebar component
import axios from 'axios';
import { onMounted, ref } from 'vue';

const activeMenu = ref('extend');
const API_BASE_URL = 'http://localhost:3001/manager'; // Base URL for API
const message = ref('');
const messageType = ref('success'); // 'success' or 'error'
const isLoading = ref(false);
const showAddForm = ref(false); // Trạng thái hiển thị form thêm mới
const Packages = ref([]); // Danh sách gói tập
const editingItem = ref(null); // Lưu trữ thông tin item đang chỉnh sửa
const showEditModal = ref(false); // Điều khiển hiển thị modal

// Status options
const statusOptions = [
  { value: '', label: 'Tất cả trạng thái' },
  { value: 'Gia hạn', label: 'Gia hạn' },
  { value: 'Đăng ký', label: 'Đăng ký' },
  { value: 'Hủy', label: 'Hủy' }
];


// Form data và filters
const formData = ref({
  id: '',
  customerName: '',
  membershipName: '',
  status: '',
  createAt: '',
  beginAt: '',
  endAt: ''
});

const filters = ref({
  phone: '',
  namepackage: '',
  status: '',
  createAt: '',
  beginAt: '',
  endAt: ''
});


// Hàm xử lý khi nhấp vào menu
const handleMenuClick = (menuId) => {
  activeMenu.value = menuId;
  console.log(`Menu clicked: ${menuId}`);
};

// Get status class based on status value
const getStatusClass = (status) => {
  if (!status) return 'status-unknown';

  switch (status.toLowerCase()) {
    case 'gia hạn':      
      return 'status-expired';
    case 'đăng ký':     
      return 'status-expiring';
    case 'hủy':        
      return 'status-canceled';
    default:
      return 'status-unknown';
  }
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


const showMessage = (text, type = 'success') => {
  message.value = text;
  messageType.value = type;

  // Auto-hide sau 5 giây
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

// Lấy dữ liệu
const fetchExtendList = async () => {
  try {
    isLoading.value = true;
    const token = getToken();
    if (!token) return;

    console.log('Fetching data with filters:', filters.value.createAt);
    const response = await axios.post(
      `${API_BASE_URL}/getMemberRegister`,
      {
        status: filters.value.status,
        createAt: filters.value.createAt
      },
      {
        headers: { token }
      }
    );

    if (response.data && response.data.status === "Lấy danh sách gia hạn thành công" && response.data.list) {
      formData.value = response.data.list.map(item => ({
        id: item.id,
        customerName: item.customerName,
        membershipName: item.membershipName,
        status: item.status,
        createAt: item.createAt,
        beginAt: item.beginAt,
        endAt: item.endAt
      }));
    } else {
      showMessage('Không có dữ liệu', 'error');
      formData.value = [];
    }
  } catch (error) {
    console.error('Error fetching data:', error);
    showMessage('Lỗi khi lấy dữ liệu: ' + (error.response?.data?.message || error.message), 'error');
    formData.value = [];
  } finally {
    isLoading.value = false;
  }
};

// Reset filters
const resetFilters = () => {
  filters.value = {
    status: '',
    createAt: ''
  };
};

// Search with filters
const searchWithFilters = () => {
  fetchExtendList();
};

//Lấy dữ liệu về gói tập
const fetchPackages = async () => {
  try {
    const token = getToken();
    if (!token) return;

    const response = await axios.get(`${API_BASE_URL}/getMembership`, {
      headers: { token }
    });

    if (response.data.list && Array.isArray(response.data.list)) {
      Packages.value = response.data.list.map(item => ({
        id: item.id,
        namepackage: item.namepackage,
        price: item.price,
        description: item.description,
        exercise_type: item.exercise_type
      }));
    } else {
      console.warn('Dữ liệu gói tập trả về không hợp lệ', response.data);
      Packages.value = [];
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi lấy danh sách gói tập');
    Packages.value = [];
  } finally {
    isLoading.value = false;
  }
};

const submitMemRegister = async () => {
  try {
    if (!filters.value.phone || !filters.value.namepackage || 
        !filters.value.status || !filters.value.beginAt || 
        !filters.value.endAt) {
      showMessage('Vui lòng điền đầy đủ thông tin thiết bị', 'error');
      return;
    }

    isLoading.value = true;

    const token = getToken();
    if (!token) return;

    const response = await axios.post(
      `${API_BASE_URL}/addMemberRegister`,
      {
        phone: filters.value.phone,
        namepackage: filters.value.namepackage,
        status: filters.value.status,
        beginAt:formatDateForInput( filters.value.beginAt),
        endAt: formatDateForInput(filters.value.endAt)
      },
      {
        headers: { token }
      }
    );

    if (response.data && response.data.status === "Đăng ký gói tập thành công") {
      showMessage('Đăng ký gói tập thành công', 'success');
      resetForm();
      fetchExtendList();
    } else {
      showMessage('Đăng ký gói tập không thành công', 'error');
    }
  } catch (error) {
    console.error('Error adding membership:', error);
    showMessage('Lỗi khi thêm thành viên: ' + (error.response?.data?.message || error.message), 'error');
  }
};

const resetForm = () => {
  filters.value = {
    phone: '',
    namepackage: '',
    status: '',
    beginAt: '',
    endAt: ''
  };
};


// Initialize data when component is mounted
onMounted(() => {
  fetchExtendList();
  fetchPackages();
});

const toggleAddForm = () => {
  showAddForm.value = !showAddForm.value;
  if (!showAddForm.value) {
    resetForm();
  } else {
    fetchPackages();
  }
};

//update
const formatDateForInput = (dateStr) => {
    if (!dateStr) return '';
    
    try {
      // Nếu dateStr là định dạng "DD-MM-YYYY"
      if (dateStr.includes('-') && dateStr.split('-').length === 3) {
        const parts = dateStr.split('-');
        // Kiểm tra nếu phần đầu tiên là ngày (2 ký tự)
        if (parts[0].length === 2) {
          // Chuyển từ DD-MM-YYYY sang YYYY-MM-DD
          return `${parts[2]}-${parts[1]}-${parts[0]}`;
        }
      }
      
      // Xử lý các định dạng khác (ISO, v.v.)
      const date = new Date(dateStr);
      if (isNaN(date.getTime())) {
        console.warn("Invalid date:", dateStr);
        return ''; 
      }
      
      // Format thành YYYY-MM-DD cho input date
      return date.toISOString().split('T')[0];
    } catch (e) {
      console.error("Lỗi xử lý ngày:", e);
      return '';
    }
};

const openEditModal = (item) => {
  editingItem.value = {
    id: String(item.id),
    customerName: item.customerName,
    membershipName: item.membershipName,
    status: item.status,
    beginAt: formatDateForInput(item.beginAt), 
    endAt: formatDateForInput(item.endAt)
  };
  showEditModal.value = true;
};

// Hủy và đóng modal
const cancelEdit = () => {
  editingItem.value = null;
  showEditModal.value = false;
};

const updateExtend = async () => {
  try {
      isLoading.value = true;
    const token = getToken();
    if (!token) return;

    // Gọi API cập nhật
    console.log({
        memberRegId: editingItem.value.id,
        status: editingItem.value.status,
        beginAt: editingItem.value.beginAt,
        endAt: editingItem.value.endAt
      });
    const response = await axios.post(
      `${API_BASE_URL}/updateMemberRegister`,
      {
        memberRegId: editingItem.value.id,
        status: editingItem.value.status,
        beginAt: editingItem.value.beginAt,
        endAt: editingItem.value.endAt
      },
      {
        headers: { token }
      }
    );

    if (response.data && (response.data.status === "Sửa thông tin đăng ký gói tập thành công" || response.data.status === "Xóa thông tin đăng ký gói tập thành công")) {
      showMessage('Sửa thông tin đăng ký gói tập thành công!', 'success');
      fetchExtendList();

      showEditModal.value = false;
      editingItem.value = null;
    } else {
      showMessage(response.data?.message || 'Cập nhật không thành công', 'error');
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi cập nhật gia hạn');
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="app-container">
    <!-- Sidebar -->
    <Slidebar :activeMenu="activeMenu" :onMenuClick="handleMenuClick" />

    <!-- Main Content -->
    <div class="main-content">
      <div class="header-actions">
        <h1 class="title">Quản lý gia hạn thành viên</h1>
        <button @click="toggleAddForm" class="add-button">
          <span v-if="!showAddForm">+ Đăng ký gói tập cho thành viên</span>
          <span v-else>Ẩn form</span>
        </button>
      </div>

      <!-- Hiển thị thông báo -->
      <div v-if="message" class="notification" :class="messageType">
        {{ message }}
      </div>

      <!-- Form thêm thành viên -->
      <div v-if="showAddForm" class="form-container">
        <h2 class="form-title">Đăng ký gói tập cho thành viên</h2>

        <form @submit.prevent="submitMemRegister" class="add-form">
          <div class="form-group">
            <label for="phone">Số điện thoại</label>
            <input type="text" id="phone" v-model="filters.phone" placeholder="Nhập số điện thoại khách hàng"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="namepackage">Gói tập</label>
            <select id="namepackage" v-model="filters.namepackage" class="form-control" required>
              <option value="" disabled selected>Chọn gói tập</option>
              <option v-for="memberShip in Packages" :key="memberShip.id" :value="memberShip.namepackage">
                {{ memberShip.namepackage }} - {{ memberShip.price }} VNĐ
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="status">Trạng thái</label>
            <select id="status" v-model="filters.status" class="form-control" required>
              <option value="" disabled selected>Chọn trạng thái</option>
              <option v-for="option in statusOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="beginAt">Ngày bắt đầu</label>
            <input type="date" id="beginAt" v-model="filters.beginAt" class="form-control" required />
          </div>

          <div class="form-group">
            <label for="endAt">Ngày kết thúc</label>
            <input type="date" id="endAt" v-model="filters.endAt" class="form-control" required />
          </div>

          <div class="form-actions">
            <button type="button" @click="resetForm" class="reset-button">Đặt lại</button>
            <button type="submit" class="submit-button" :disabled="isLoading">
              <span v-if="isLoading">Đang đăng ký...</span>
              <span v-else>Đăng ký</span>
            </button>
          </div>
        </form>
      </div>

      <!-- Form tìm kiếm -->
      <div class="search-form">
        <div class="form-group">
          <label for="status">Trạng thái</label>
          <select id="status" v-model="filters.status" class="form-control">
            <option v-for="option in statusOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="createAt">Ngày tạo</label>
          <input type="date" id="createAt" v-model="filters.createAt" class="form-control" />
        </div>

        <div class="search-actions">
          <button @click="searchWithFilters" class="search-button" :disabled="isLoading">
            <span v-if="isLoading">Đang tìm...</span>
            <span v-else>Tìm kiếm</span>
          </button>
          <button @click="resetFilters" class="reset-button">Đặt lại</button>
        </div>
      </div>

      <!-- Loading indicator -->
      <div v-if="isLoading" class="loading-indicator">
        <div class="loader"></div>
        <div>Đang tải dữ liệu...</div>
      </div>

      <!-- Bảng hiển thị kết quả -->
      <div v-else class="table-container">
        <table class="extend-table">
          <thead>
            <tr>
              <th>Mã đăng ký</th>
              <th>Khách hàng</th>
              <th>Gói thành viên</th>
              <th>Trạng thái</th>
              <th>Ngày tạo</th>
              <th>Ngày bắt đầu</th>
              <th>Ngày kết thúc</th>
              <th>Thao tác</th> <!-- Thêm cột thao tác -->
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in formData" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.customerName }}</td>
              <td>{{ item.membershipName }}</td>
              <td>
                <span class="status-badge" :class="getStatusClass(item.status)">
                  {{ item.status }}
                </span>
              </td>
              <td>{{ item.createAt }}</td>
              <td>{{ item.beginAt }}</td>
              <td>{{ item.endAt }}</td>
              <td>
                <button @click="openEditModal(item)" class="edit-button">
                  Chỉnh sửa
                </button>
              </td>
            </tr>

            <!-- Hiển thị khi không có dữ liệu -->
            <tr v-if="formData.length === 0">
              <td colspan="8" class="no-data">Không có dữ liệu gia hạn nào được tìm thấy</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Modal chỉnh sửa -->
      <div v-if="showEditModal" class="modal-overlay">
        <div class="edit-modal">
          <h2 class="modal-title">Chỉnh sửa thông tin gia hạn</h2>

          <div class="modal-content">
            <div class="form-group">
              <label>Mã gia hạn</label>
              <input type="text" v-model="editingItem.id" disabled class="form-control" />
            </div>

            <div class="form-group">
              <label>Khách hàng</label>
              <input type="text" v-model="editingItem.customerName" disabled class="form-control" />
            </div>

            <div class="form-group">
              <label>Gói tập</label>
              <input type="text" v-model="editingItem.membershipName" disabled class="form-control" />
            </div>

            <div class="form-group">
              <label for="edit-status">Trạng thái</label>
              <select id="edit-status" v-model="editingItem.status" class="form-control" required>
                <option v-for="option in statusOptions.filter(o => o.value)" :key="option.value" :value="option.value">
                  {{ option.label }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label for="edit-beginAt">Ngày bắt đầu</label>
              <input type="date" id="edit-beginAt" v-model="editingItem.beginAt" class="form-control" required />
            </div>

            <div class="form-group">
              <label for="edit-endAt">Ngày kết thúc</label>
              <input type="date" id="edit-endAt" v-model="editingItem.endAt" class="form-control" required />
            </div>
          </div>

          <div class="modal-actions">
            <button @click="cancelEdit" class="cancel-button">Hủy</button>
            <button @click="updateExtend" class="update-button" :disabled="isLoading">
              <span v-if="isLoading">Đang cập nhật...</span>
              <span v-else>Cập nhật</span>
            </button>
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

/* Notification styling */
.notification {
  padding: 10px 16px;
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

/* Add button styling */
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

.form-actions {
  grid-column: span 2;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
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

/* Responsive adjustments for add form */
@media (max-width: 768px) {
  .add-form {
    grid-template-columns: 1fr;
  }

  .form-actions {
    grid-column: 1;
  }
}

/* Search form styling */
.search-form {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

.form-group {
  display: flex;
  flex-direction: column;
  min-width: 200px;
  flex: 1;
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

.search-actions {
  display: flex;
  gap: 8px;
}

.search-button {
  padding: 10px 16px;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
  min-width: 100px;
}

.search-button:hover:not(:disabled) {
  background-color: #4338ca;
}

.search-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.reset-button {
  padding: 10px 16px;
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

.extend-table {
  width: 100%;
  border-collapse: collapse;
}

.extend-table th {
  padding: 12px 16px;
  text-align: left;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  background-color: #f3f4f6;
  border-bottom: 2px solid #e5e7eb;
}

.extend-table td {
  padding: 12px 16px;
  font-size: 14px;
  color: #4b5563;
  border-bottom: 1px solid #e5e7eb;
}

.extend-table tbody tr:hover {
  background-color: #f9fafb;
}

/* Status badges */
.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

/* Gia hạn */
.status-expired {
  background-color: #dbeafe;
  color: #1e40af;
}

/* Đăng ký */
.status-expiring {
  background-color: #e0e7ff;
  color: #4338ca;
}

.status-canceled {
  background-color: #fee2e2;
  color: #b91c1c;
}

.status-unknown {
  background-color: #fee2e2;
  color: #ff5b5b;
}

/* No data message */
.no-data {
  text-align: center;
  padding: 20px;
  color: #6b7280;
  font-style: italic;
}

/* Responsive design */
@media (max-width: 1024px) {
  .search-form {
    flex-direction: column;
  }

  .form-group {
    width: 100%;
  }

  .search-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .extend-table {
    min-width: 900px;
  }
}

/* Edit button styling */
.edit-button {
  padding: 6px 12px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.edit-button:hover {
  background-color: #2563eb;
}

/* Modal styling */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.edit-modal {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 600px;
  padding: 24px;
  animation: zoomIn 0.3s;
}

@keyframes zoomIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin-top: 0;
  margin-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 12px;
}

.modal-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-button {
  padding: 10px 16px;
  background-color: #f3f4f6;
  color: #4b5563;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.cancel-button:hover {
  background-color: #e5e7eb;
}

.update-button {
  padding: 10px 16px;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.update-button:hover:not(:disabled) {
  background-color: #4338ca;
}

.update-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Responsive modal */
@media (max-width: 768px) {
  .modal-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .main-content {
    margin-left: 0;
    width: 100%;
  }
}
</style>