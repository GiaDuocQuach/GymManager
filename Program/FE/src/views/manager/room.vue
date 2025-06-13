<script setup>
import Slidebar from '@/components/SlidebarManager.vue';
import axios from 'axios';
import { computed, onMounted, ref } from 'vue';

const API_BASE_URL = 'http://localhost:3001/manager';

const activeMenu = ref('equipment');
const roomEquipments = ref([]);
const rooms = ref([]);
const editingEquipment = ref(null);
const deletingEquipment = ref(null);
const showDeleteConfirm = ref(false);

const handleMenuClick = (menuId) => {
  activeMenu.value = menuId;
};

const filters = ref({
  room_name: '',
  equipment_name: '',
  status: ''
});

const formData = ref({
  room_name: '',
  equipment_name: '',
  quantity: '',
  status: ''
});

const message = ref('');
const messageType = ref('');
const showAddForm = ref(false);
const isLoading = ref(false);

const statusOptions = [
  { value: 'Hoạt động', label: 'Hoạt động' },
  { value: 'Bảo trì', label: 'Bảo trì' },
  { value: 'Hỏng', label: 'Hỏng' }
];

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
  setTimeout(() => {
    message.value = '';
  }, 5000);
};

const handleApiError = (error, defaultMessage) => {
  console.error(defaultMessage, error);
  const errorMessage = error.response?.data?.message || error.message || 'Đã xảy ra lỗi không xác định';
  showMessage(`${defaultMessage}: ${errorMessage}`, 'error');
};

const startEditing = (equipment) => {
  editingEquipment.value = {
    room_name: equipment.room_name,
    equipment_name: equipment.equipment_name,
    status: equipment.status,
    quantity: equipment.quantity,
  };
};

const isEditing = (equipment) => {
  return editingEquipment.value && 
         editingEquipment.value.room_name === equipment.room_name && 
         editingEquipment.value.equipment_name === equipment.equipment_name;
};

const cancelEditing = () => {
  editingEquipment.value = null;
};

const updateDevice = async () => {
  try {
    isLoading.value = true;
    const token = getToken();
    if (!token) return;

    console.log('Updating device with data:', editingEquipment.value);

    const response = await axios.post(
      `${API_BASE_URL}/updateDevice`,
      {
        room_name: editingEquipment.value.room_name,
        equipment_name: editingEquipment.value.equipment_name,
        status: editingEquipment.value.status,
      },
      {
        headers: { token }
      }
    );

    if (response.data && response.data.status === 'Cập nhật thiết bị thành công') {
      showMessage('Cập nhật trạng thái thiết bị thành công!');
      
      const index = roomEquipments.value.findIndex(
        item => item.room_name === editingEquipment.value.room_name && 
               item.equipment_name === editingEquipment.value.equipment_name
      );
      
      if (index !== -1) {
        roomEquipments.value[index].status = editingEquipment.value.status;
      }
      
      editingEquipment.value = null;
    } else {
      showMessage(response.data?.message || 'Cập nhật trạng thái không thành công', 'error');
    }
  } catch (error) {
    if (error.response && error.response.data) {
      console.error('Error response:', error.response.data);
      showMessage(`Lỗi khi cập nhật thiết bị: ${error.response.data.message || 'Đã xảy ra lỗi'}`, 'error');
    } else {
      handleApiError(error, 'Lỗi khi cập nhật thiết bị');
    }
  } finally {
    isLoading.value = false;
  }
};

const startDeleting = (equipment) => {
  deletingEquipment.value = {
    room_name: equipment.room_name,
    equipment_name: equipment.equipment_name
  };
  showDeleteConfirm.value = true;
};

const cancelDeleting = () => {
  deletingEquipment.value = null;
  showDeleteConfirm.value = false;
};

const confirmDelete = async () => {
  try {
    isLoading.value = true;
    const token = getToken();
    if (!token) return;

    console.log('Deleting device:', deletingEquipment.value);

    const response = await axios.post(
      `${API_BASE_URL}/deleteDevice`,
      {
        room_name: deletingEquipment.value.room_name,
        equipment_name: deletingEquipment.value.equipment_name
      },
      {
        headers: { token }
      }
    );

    if (response.data && response.data.status === 'Xóa thiết bị thành công') {
      showMessage('Xóa thiết bị thành công!');
      fetchRoomEquipments();
    } else {
      showMessage(response.data?.message || 'Xóa thiết bị không thành công', 'error');
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi xóa thiết bị');
  } finally {
    isLoading.value = false;
    showDeleteConfirm.value = false;
    deletingEquipment.value = null;
  }
};

const submitDevice = async () => {
  try {
    if (!formData.value.room_name || !formData.value.equipment_name ||
      !formData.value.quantity || !formData.value.status) {
      showMessage('Vui lòng điền đầy đủ thông tin thiết bị', 'error');
      return;
    }

    isLoading.value = true;
    const token = getToken();
    if (!token) return;

    console.log('Adding device with data:', formData.value);

    const response = await axios.post(
      `${API_BASE_URL}/addDevice`,
      {
        room_name: formData.value.room_name,
        equipment_name: formData.value.equipment_name,
        quantity: formData.value.quantity,
        status: formData.value.status,
      },
      {
        headers: { token }
      }
    );

    if (response.data && response.data.status === 'Thêm thiết bị thành công') {
      showMessage('Thêm thiết bị thành công!');
      resetAddForm();
      fetchRoomEquipments();
    } else {
      showMessage(response.data?.message || 'Thêm thiết bị không thành công', 'error');
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi thêm thiết bị');
  } finally {
    isLoading.value = false;
  }
};

const resetAddForm = () => {
  formData.value = {
    room_name: '',
    equipment_name: '',
    quantity: '',
    status: ''
  };
  message.value = '';
};

const toggleAddForm = () => {
  showAddForm.value = !showAddForm.value;
  if (!showAddForm.value) {
    resetAddForm();
  } else {
    fetchRooms();
  }
};

const fetchRoomEquipments = async () => {
  try {
    isLoading.value = true;
    const token = getToken();
    if (!token) return;

    const response = await axios.post(
      `${API_BASE_URL}/findDevice`,
      {
        room_name: filters.value.room_name,
        roomEquipment: filters.value.equipment_name,
        status: filters.value.status,
      },
      {
        headers: { token }
      }
    );

    if (response.data.list && Array.isArray(response.data.list)) {
      roomEquipments.value = response.data.list.map((item) => ({
        room_name: item.room_name,
        equipment_name: item.equipment_name,
        quantity: item.quantity,
        status: item.status,
      }));
    } else {
      console.warn('Dữ liệu trả về không hợp lệ:', response.data);
      roomEquipments.value = [];
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi lấy danh sách thiết bị');
    roomEquipments.value = [];
  } finally {
    isLoading.value = false;
  }
};

const fetchRooms = async () => {
  try {
    isLoading.value = true;
    const token = getToken();
    if (!token) return;

    const response = await axios.get(
      `${API_BASE_URL}/getRoom`,
      {
        headers: { token }
      }
    );

    if (response.data.list && Array.isArray(response.data.list)) {
      rooms.value = response.data.list.map(room => ({
        room_name: room.name,
      }));
    } else {
      console.warn('Dữ liệu phòng trả về không hợp lệ:', response.data);
      rooms.value = [];
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi lấy danh sách phòng');
    rooms.value = [];
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchRoomEquipments();
  fetchRooms();
});

const clearFilters = () => {
  filters.value = {
    room_name: '',
    equipment_name: '',
    status: ''
  };
  fetchRoomEquipments();
};

const hasFilters = computed(() => {
  return filters.value.room_name || filters.value.equipment_name || filters.value.status;
});
</script>

<template>
  <div class="app-container">
    <Slidebar :activeMenu="activeMenu" :onMenuClick="handleMenuClick" />

    <div class="main-content">
      <div class="header-actions">
        <h1 class="title">Danh sách thiết bị trong phòng</h1>
        <button @click="toggleAddForm" class="add-button">
          <span v-if="!showAddForm">+ Thêm thiết bị mới</span>
          <span v-else>Ẩn form</span>
        </button>
      </div>

      <div v-if="message" class="notification" :class="messageType">
        {{ message }}
      </div>

      <div v-if="showAddForm" class="form-container">
        <h2 class="form-title">Thêm thiết bị mới</h2>

        <form @submit.prevent="submitDevice" class="add-form">
          <div class="form-group">
            <label for="room_name">Tên phòng</label>
            <select id="room_name" v-model="formData.room_name" class="form-control" required>
              <option value="" disabled selected>Chọn phòng</option>
              <option v-for="room in rooms" :key="room.room_name" :value="room.room_name">
                {{ room.room_name }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="equipment_name">Tên thiết bị</label>
            <input type="text" id="equipment_name" v-model="formData.equipment_name" placeholder="Nhập tên thiết bị"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="quantity">Số lượng</label>
            <input type="number" id="quantity" v-model="formData.quantity" placeholder="Nhập số lượng"
              class="form-control" required />
          </div>

          <div class="form-group">
            <label for="status">Trạng thái</label>
            <select id="status" v-model="formData.status" class="form-control" required>
              <option value="" disabled selected>Chọn trạng thái</option>
              <option v-for="option in statusOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </div>
          <div class="form-actions">
            <button type="button" @click="resetAddForm" class="reset-button">Đặt lại</button>
            <button type="submit" class="submit-button" :disabled="isLoading">
              <span v-if="isLoading">Đang thêm...</span>
              <span v-else>Thêm thiết bị</span>
            </button>
          </div>
        </form>
      </div>

      <div class="search-form">
        <input type="text" v-model="filters.room_name" placeholder="Tìm theo tên phòng" class="search-input" />
        <input type="text" v-model="filters.equipment_name" placeholder="Tìm theo tên thiết bị" class="search-input" />
        <select v-model="filters.status" class="search-input">
          <option value="">Tất cả trạng thái</option>
          <option v-for="option in statusOptions" :key="option.value" :value="option.value">
            {{ option.label }}
          </option>
        </select>
        <button @click="fetchRoomEquipments" class="search-button" :disabled="isLoading">
          <span v-if="isLoading">Đang tìm...</span>
          <span v-else>Tìm kiếm</span>
        </button>

        <button v-if="hasFilters" @click="clearFilters" class="clear-button">
          Xóa bộ lọc
        </button>
      </div>

      <div v-if="isLoading" class="loading-indicator">
        <div class="loader"></div>
        <div>Đang tải dữ liệu...</div>
      </div>

      <div class="table-container" v-else>
        <table>
          <thead>
            <tr>
              <th>Tên phòng</th>
              <th>Tên thiết bị</th>
              <th>Số lượng</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(equipment, index) in roomEquipments" :key="index" 
                :class="{'editing-row': isEditing(equipment)}">
              <td>{{ equipment.room_name }}</td>
              <td>{{ equipment.equipment_name }}</td>
              <td>{{ equipment.quantity }}</td>
              <td>
                <template v-if="isEditing(equipment)">
                  <select v-model="editingEquipment.status" class="edit-status-select">
                    <option v-for="option in statusOptions" :key="option.value" :value="option.value">
                      {{ option.label }}
                    </option>
                  </select>
                </template>
                <template v-else>
                  <span class="status-badge" :class="{
                    'status-active': equipment.status.toLowerCase() === 'hoạt động',
                    'status-maintenance': equipment.status.toLowerCase() === 'bảo trì',
                    'status-broken': equipment.status.toLowerCase() === 'hỏng'
                  }">{{ equipment.status }}</span>
                </template>
              </td>

              <td class="action-column">
                <template v-if="isEditing(equipment)">
                  <button @click="updateDevice" class="action-button save-button" :disabled="isLoading">
                    <span v-if="isLoading">Đang lưu...</span>
                    <span v-else>Lưu</span>
                  </button>
                  <button @click="cancelEditing" class="action-button cancel-button">
                    Hủy
                  </button>
                </template>
                <template v-else>
                  <button @click="startEditing(equipment)" class="action-button edit-button">
                    Sửa
                  </button>
                  <button @click="startDeleting(equipment)" class="action-button delete-button">
                    Xóa
                  </button>
                </template>
              </td>
            </tr>

            <tr v-if="roomEquipments.length === 0">
              <td colspan="5" class="no-data">Không có thiết bị nào được tìm thấy</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="showDeleteConfirm" class="overlay">
      <div class="confirm-dialog">
        <h3 class="confirm-title">Xác nhận xóa</h3>
        <p class="confirm-message">
          Bạn có chắc chắn muốn xóa thiết bị "<strong>{{ deletingEquipment?.equipment_name }}</strong>" 
          tại phòng "<strong>{{ deletingEquipment?.room_name }}</strong>"?
        </p>
        <div class="confirm-actions">
          <button @click="cancelDeleting()" class="cancel-button">Hủy bỏ</button>
          <button @click="confirmDelete()" class="delete-confirm-button" :disabled="isLoading">
            <span v-if="isLoading">Đang xóa...</span>
            <span v-else>Xóa</span>
          </button>
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

.submit-button:disabled,
.search-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.search-form {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-input {
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  flex: 1;
  min-width: 120px;
}

.search-button {
  padding: 10px 20px;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  white-space: nowrap;
}

.search-button:hover:not(:disabled) {
  background-color: #4338ca;
}

.clear-button {
  padding: 10px 15px;
  background-color: #f3f4f6;
  color: #4b5563;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  white-space: nowrap;
}

.clear-button:hover {
  background-color: #e5e7eb;
}

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
  min-width: 700px;
}

thead {
  background-color: #f3f4f6;
}

th {
  text-align: center;
  padding: 12px 16px;
  font-size: 14px;
  font-weight: bold;
  color: #374151;
  border-bottom: 2px solid #e5e7eb;
  position: sticky;
  top: 0;
  background-color: #f3f4f6;
  z-index: 1;
}

td {
  text-align: center;
  padding: 12px 16px;
  font-size: 14px;
  color: #4b5563;
  border-bottom: 1px solid #e5e7eb;
}

tr:hover {
  background-color: #f9fafb;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-active {
  background-color: #d1fae5;
  color: #065f46;
}

.status-maintenance {
  background-color: #fef3c7;
  color: #92400e;
}

.status-broken {
  background-color: #fee2e2;
  color: #b91c1c;
}

.action-column {
  white-space: nowrap;
  width: 120px;
}

.action-button {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  margin-right: 5px;
  border: none;
  transition: background-color 0.2s, transform 0.1s;
}

.action-button:active {
  transform: scale(0.97);
}

.edit-button {
  background-color: #3b82f6;
  color: white;
}

.edit-button:hover {
  background-color: #2563eb;
}

.save-button {
  background-color: #10b981;
  color: white;
}

.save-button:hover:not(:disabled) {
  background-color: #059669;
}

.save-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.cancel-button {
  background-color: #f3f4f6;
  color: #4b5563;
  border: 1px solid #d1d5db;
}

.cancel-button:hover {
  background-color: #e5e7eb;
}

.delete-button {
  background-color: #ef4444;
  color: white;
}

.delete-button:hover {
  background-color: #dc2626;
}

.edit-status-select {
  padding: 6px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
  width: 100%;
  min-width: 120px;
  background-color: white;
}

.edit-status-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.3);
}

tr.editing-row {
  background-color: #f0f9ff !important;
}

tr.editing-row:hover {
  background-color: #e0f2fe !important;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.confirm-dialog {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  width: 90%;
  max-width: 450px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  animation: fadeIn 0.2s ease-out;
}

.confirm-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin-top: 0;
  margin-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 12px;
}

.confirm-message {
  margin-bottom: 20px;
  color: #4b5563;
  line-height: 1.5;
}

.confirm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.delete-confirm-button {
  padding: 8px 16px;
  background-color: #ef4444;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.delete-confirm-button:hover:not(:disabled) {
  background-color: #dc2626;
}

.delete-confirm-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

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

  .search-form {
    flex-direction: column;
  }

  .search-input {
    width: 100%;
    min-width: auto;
  }
}
</style>