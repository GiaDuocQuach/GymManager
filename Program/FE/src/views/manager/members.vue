<script setup>
import { onMounted, ref } from 'vue';
import SlidebarManager from '@/components/SlidebarManager.vue';
import axios from 'axios';  

const activeMenu = ref('members');
const members = ref([]);
const genderOptions = ref([
    { value: 'Nam', label: 'Nam' },
    { value: 'Nữ', label: 'Nữ' }
]);
const ROLE_CUSTOMER = 4;
const formData = ref({
    username: '',
    password: '',
    roleid: ROLE_CUSTOMER,
    firstname: '',
    lastname: '',
    email: '',
    phone: '',
    gender: '',
    age: ''
});

// Thêm state cho form chỉnh sửa
const showEditForm = ref(false);
const editingMember = ref(null);
const editFormData = ref({
    id: '',
    firstname: '',
    lastname: '',
    email: '',
    phone: '',
    age: ''
});

const message = ref('');
const messageType = ref('');
const showAddForm = ref(false);
const isLoading = ref(false);
// Hàm format ngày (ví dụ: 27/04/2025)
function formatDate(dateString) {
  if (!dateString) return 'N/A';
  const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
  return new Date(dateString).toLocaleDateString('vi-VN', options);
}

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

// Reset form values
const resetForm = () => {
  formData.value = {
    username: '',
    password: '',
    roleid: ROLE_CUSTOMER,
    firstname: '',
    lastname: '',
    email: '',
    phone: '',
    gender: '',
    age: ''
  };
};

// Toggle add form visibility
const toggleAddForm = () => {
  showAddForm.value = !showAddForm.value;
  if (!showAddForm.value) {
    resetForm();
  }
  // Đóng form chỉnh sửa nếu đang mở
  if (showAddForm.value && showEditForm.value) {
    showEditForm.value = false;
    editingMember.value = null;
  }
};

// Add new customer
const submitCustomer = async () => {
  try {
    // Validate form
    if (!formData.value.username || !formData.value.password || 
        !formData.value.firstname || !formData.value.lastname || 
        !formData.value.email || !formData.value.phone || 
        !formData.value.gender || !formData.value.age) {
      showMessage('Vui lòng điền đầy đủ thông tin khách hàng', 'error');
      return;
    }

    isLoading.value = true;
    const token = localStorage.getItem('token');
    if (!token) {
      showMessage('Token không tồn tại. Vui lòng đăng nhập lại.', 'error');
      return;
    }

    // Debug log
    console.log('Thêm khách hàng với data:', formData.value);

    const response = await axios.post(
      'http://localhost:3001/addUser',
      {
        username: formData.value.username,
        password: formData.value.password,
        roleid: formData.value.roleid,
        firstname: formData.value.firstname,
        lastname: formData.value.lastname,
        email: formData.value.email,
        phone: formData.value.phone,
        gender: formData.value.gender,
        age: formData.value.age
      },
      {
        headers: { token }
      }
    );

    // Handle successful response
    if (response.data && response.data.status === 'Thêm người dùng thành công') {
      showMessage('Thêm người dùng thành công');
      resetForm();
      fetchMembers(); // Refresh list
      toggleAddForm(); // Hide form
    } else {
      showMessage(response.data?.message || 'Thêm người dùng không thành công', 'error');
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi thêm khách hàng');
  } finally {
    isLoading.value = false;
  }
};

// Fetch customer list
const fetchMembers = async () => {
  try {
    isLoading.value = true;
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('Token không tồn tại. Vui lòng đăng nhập lại.');
      return;
    }

    // Gửi request đến server với token trong header
    const response = await axios.get('http://localhost:3001/manager/getInfoCustomer', {
      headers: {
        token: token
      }
    });

    // Xử lý dữ liệu trả về từ server
    members.value = response.data.list.map(member => ({
      customerId: member.customerId,
      customerName: member.customerName,
      customerAge: member.customerAge,
      customerGender: member.customerGender,
      customerPhone: member.customerPhone,
      customerEmail: member.customerEmail,
      infoUpdateAt: formatDate(member.infoUpdateAt)
    }));
  } catch (error) {
    handleApiError(error, 'Lỗi khi lấy danh sách khách hàng');
  } finally {
    isLoading.value = false;
  }
};

// Mở form chỉnh sửa thông tin khách hàng
const openEditForm = (member) => {
  // Đóng form thêm mới nếu đang mở
  if (showAddForm.value) {
    showAddForm.value = false;
    resetForm();
  }
  
  editingMember.value = member;
  // Lấy dữ liệu từ dòng được chọn và phân tách tên thành họ và tên
  let nameParts = member.customerName ? member.customerName.split(' ') : ['', ''];
  let firstName = nameParts[0] || '';
  let lastName = nameParts.slice(1).join(' ') || '';
  
  editFormData.value = {
    id: member.customerId,  // Lấy mã khách hàng từ dòng được chọn
    firstname: firstName,
    lastname: lastName,
    email: member.customerEmail,
    phone: member.customerPhone,
    age: member.customerAge
  };
  
  console.log("Đang sửa thông tin khách hàng:", editFormData.value);
  showEditForm.value = true;
  
  // Scroll tới form chỉnh sửa
  setTimeout(() => {
    const editFormElement = document.getElementById('edit-form');
    if (editFormElement) {
      editFormElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }, 100);
};

// Hủy chỉnh sửa
const cancelEdit = () => {
  showEditForm.value = false;
  editingMember.value = null;
};

// Lưu chỉnh sửa
const submitEdit = async () => {
  try {
    // Validate form
    if (!editFormData.value.firstname || 
        !editFormData.value.lastname||
        !editFormData.value.email||
        !editFormData.value.phone||
        !editFormData.value.age) {
      showMessage('Vui lòng điền đầy đủ thông tin khách hàng', 'error');
      return;
    }

    isLoading.value = true;
    const token = localStorage.getItem('token');
    if (!token) {
      showMessage('Token không tồn tại. Vui lòng đăng nhập lại.', 'error');
      return;
    }

    // Debug log
    console.log('Cập nhật khách hàng với data:', editFormData.value);

    const response = await axios.post(
      'http://localhost:3001/updateInfoCustomer',
      {
        id: editFormData.value.id,
        firstname: editFormData.value.firstname,
        lastname: editFormData.value.lastname,
        age: editFormData.value.age,
        phone: editFormData.value.phone,
        email: editFormData.value.email
      },
      {
        headers: { token }
      }
    );

    // Handle successful response
    if (response.data && response.data.status === "Cập nhật thông tin thành công") {
      showMessage('Cập nhật thông tin khách hàng thành công');
      fetchMembers(); // Refresh list
      showEditForm.value = false;
      editingMember.value = null;
    } else {
      showMessage(response.data?.message || 'Cập nhật thông tin không thành công', 'error');
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi cập nhật thông tin khách hàng');
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchMembers();
});

const handleMenuClick = (menuId) => {
  activeMenu.value = menuId;
  console.log(`Menu clicked: ${menuId}`);
};
</script>

<template>
  <div class="app-container">
    <!-- Sidebar -->
    <SlidebarManager :activeMenu="activeMenu" :onMenuClick="handleMenuClick" />

    <!-- Main Content -->
    <div class="main-content">
      <div class="header-actions">
        <h1 class="title">Danh sách khách hàng</h1>
        <button @click="toggleAddForm" class="add-button">
          <span v-if="!showAddForm">+ Thêm khách hàng mới</span>
          <span v-else>Ẩn form</span>
        </button>
      </div>

      <!-- Hiển thị thông báo -->
      <div v-if="message" class="notification" :class="messageType">
        {{ message }}
      </div>
      
      <!-- Add Customer Form -->
      <div v-if="showAddForm" class="form-container">
        <h2 class="form-title">Thêm khách hàng mới</h2>

        <form @submit.prevent="submitCustomer" class="add-form">
          <!-- Form thêm mới -->
          <div class="form-group">
            <label for="username">Tên đăng nhập</label>
            <input type="text" id="username" v-model="formData.username" placeholder="Nhập tên đăng nhập"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="password">Mật khẩu</label>
            <input type="password" id="password" v-model="formData.password" placeholder="Nhập mật khẩu"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="firstname">Họ</label>
            <input type="text" id="firstname" v-model="formData.firstname" placeholder="Nhập họ"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="lastname">Tên</label>
            <input type="text" id="lastname" v-model="formData.lastname" placeholder="Nhập tên"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" v-model="formData.email" placeholder="Nhập email"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="phone">Số điện thoại</label>
            <input type="tel" id="phone" v-model="formData.phone" placeholder="Nhập số điện thoại"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="gender">Giới tính</label>
            <select id="gender" v-model="formData.gender" class="form-control" required>
              <option value="" disabled selected>Chọn giới tính</option>
              <option v-for="option in genderOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="age">Tuổi</label>
            <input type="number" id="age" v-model="formData.age" placeholder="Nhập tuổi"
              class="form-control" required min="1" max="120" />
          </div>

          <div class="form-actions">
            <button type="button" @click="resetForm" class="reset-button">Đặt lại</button>
            <button type="submit" class="submit-button" :disabled="isLoading">
              <span v-if="isLoading">Đang thêm...</span>
              <span v-else>Thêm khách hàng</span>
            </button>
          </div>
        </form>
      </div>

      <!-- Edit Customer Form -->
      <div v-if="showEditForm" id="edit-form" class="form-container edit-form-container">
        <h2 class="form-title"><i class="fas fa-edit form-icon"></i> Sửa thông tin khách hàng</h2>

        <form @submit.prevent="submitEdit" class="edit-form">
          <div class="form-group">
            <label for="edit-customer-id">Mã khách hàng</label>
            <input type="text" id="edit-customer-id" v-model="editingMember.customerId" class="form-control disabled-field" readonly disabled />
            <small class="help-text">Mã khách hàng không thể thay đổi</small>
          </div>

          <div class="form-group">
            <label for="edit-customer-firstname">
              <span>Họ</span>
              <span class="edit-label">Sửa được</span>
            </label>
            <input type="text" id="edit-customer-firstname" v-model="editFormData.firstname" placeholder="Nhập họ"
              class="form-control editable-field" required />
          </div>

          <div class="form-group">
            <label for="edit-customer-lastname">
              <span>Tên</span>
              <span class="edit-label">Sửa được</span>
            </label>
            <input type="text" id="edit-customer-lastname" v-model="editFormData.lastname" placeholder="Nhập tên"
              class="form-control editable-field" required />
          </div>

          <div class="form-group">
            <label for="edit-customer-age">
              <span>Tuổi</span>
              <span class="edit-label">Sửa được</span>
            </label>
            <input type="number" id="edit-customer-age" v-model="editFormData.age" placeholder="Nhập tuổi"
              class="form-control editable-field" required min="1" max="120" />
          </div>

          <div class="form-group">
            <label for="edit-customer-gender">Giới tính</label>
            <select id="edit-customer-gender" v-model="editingMember.customerGender" class="form-control disabled-field" readonly disabled>
              <option v-for="option in genderOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
            <small class="help-text">Giới tính không thể thay đổi</small>
          </div>

          <div class="form-group">
            <label for="edit-customer-phone">
              <span>Số điện thoại</span>
              <span class="edit-label">Sửa được</span>
            </label>
            <input type="tel" id="edit-customer-phone" v-model="editFormData.phone" placeholder="Nhập số điện thoại"
              class="form-control editable-field" required />
          </div>

          <div class="form-group">
            <label for="edit-customer-email">
              <span>Email</span>
              <span class="edit-label">Sửa được</span>
            </label>
            <input type="email" id="edit-customer-email" v-model="editFormData.email" placeholder="Nhập email"
              class="form-control editable-field" required />
          </div>

          <div class="form-actions">
            <button type="button" @click="cancelEdit" class="reset-button">Hủy</button>
            <button type="submit" class="submit-button" :disabled="isLoading">
              <span v-if="isLoading">Đang cập nhật...</span>
              <span v-else>Lưu thay đổi</span>
            </button>
          </div>
        </form>
      </div>

      <!-- Loading indicator -->
      <div v-if="isLoading && !showAddForm && !showEditForm" class="loading-indicator">
        <div class="loader"></div>
        <div>Đang tải dữ liệu...</div>
      </div>
        
      <!-- Customer Table -->
      <div v-else-if="!isLoading" class="table-container">
        <table class="members-table">
          <thead>
            <tr>
              <th>Mã khách hàng</th>
              <th>Họ tên</th>
              <th>Tuổi</th>
              <th>Giới tính</th>
              <th>Số điện thoại</th>
              <th>Email</th>
              <th>Cập nhật</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="member in members" :key="member.customerId">
              <td>{{ member.customerId }}</td>
              <td>{{ member.customerName }}</td>
              <td>{{ member.customerAge }}</td>
              <td>
                <span :class="{'gender-male': member.customerGender === 'Nam', 'gender-female': member.customerGender === 'Nữ'}">
                  {{ member.customerGender }}
                </span>
              </td>
              <td>{{ member.customerPhone }}</td>
              <td>{{ member.customerEmail }}</td>
              <td>{{ member.infoUpdateAt }}</td>
              <td>
                <div class="action-buttons">
                  <button @click="openEditForm(member)" class="edit-button" title="Sửa thông tin">
                   Chỉnh sửa <i class="fas fa-edit"></i>
                  </button>
                </div>
              </td>
            </tr>
            <!-- Hiển thị thông báo khi không có dữ liệu -->
            <tr v-if="members.length === 0">
              <td colspan="8" class="no-data">Không có khách hàng nào được tìm thấy</td>
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
  display: flex;
  align-items: center;
}

.form-icon {
  margin-right: 8px;
  color: #4f46e5;
}

.add-form, .edit-form {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.disabled-field {
  background-color: #f3f4f6;
  cursor: not-allowed;
  border-color: #e5e7eb;
}

.editable-field {
  border-left: 3px solid #10b981;
}

.edit-label {
  background-color: #d1fae5;
  color: #065f46;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: normal;
  margin-left: 8px;
}

.help-text {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
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

/* Edit form specific styles */
.edit-form-container {
  border-left: 4px solid #4f46e5;
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

/* Hiển thị khi không có dữ liệu */
.no-data {
  text-align: center;
  padding: 20px;
  color: #6b7280;
  font-style: italic;
}

.table-container {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
}

.members-table {
  width: 100%;
  border-collapse: collapse;
}

.members-table thead {
  background-color: #f3f4f6;
}

.members-table th {
  padding: 12px 16px;
  text-align: left;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  border-bottom: 2px solid #e5e7eb;
}

.members-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #e5e7eb;
  font-size: 14px;
  color: #4b5563;
}

.members-table tbody tr:hover {
  background-color: #f9fafb;
}

/* Styling for the gender column */
.gender-male {
  display: inline-block;
  padding: 2px 8px;
  background-color: #e0f2fe;
  color: #0369a1;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.gender-female {
  display: inline-block;
  padding: 2px 8px;
  background-color: #fce7f3;
  color: #be185d;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

/* Action buttons */
.action-buttons {
  display: flex;
  gap: 8px;
}

.edit-button {
  padding: 6px;
  background-color: #3b82f6;
  color:white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-button:hover {
  background-color: #bae6fd;
  color: #0284c7;
}

.edit-button i {
  font-size: 14px;
}

/* Responsive design */
@media (max-width: 1024px) {
  .members-table {
    min-width: 800px;
  }
  
  .add-form, .edit-form {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    grid-column: 1;
  }
}
</style>