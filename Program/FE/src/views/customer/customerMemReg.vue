<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import SlidebarCustomer from '@/components/SlidebarCustomer.vue';

const activeMenu = ref('extendss');
const isLoading = ref(false);
const message = ref('');
const messageType = ref('');
const token = localStorage.getItem('token');
const userid = localStorage.getItem('userid');
const API_BASE_URL = 'http://localhost:3001/customer';
const classData = ref([]);
const packageData = ref([]);

const showMessenger = (text , type = 'success') => {
    message.value = text;
    messageType.value = type;

    setTimeout(() => {
        message.value = '';
    }, 5000);
}

const handleApiError = (error, defaultMessage) => {
    console.error(defaultMessage, error);
    const errorMessage = error.response?.data?.message || error.message || 'Đã xảy ra lỗi không xác định';
    showMessenger(`${defaultMessage}: ${errorMessage}`, 'error');
};

// Get status class based on status value
const getStatusClass = (status) => {
  if (!status) return 'status-unknown';

  switch (status.toLowerCase()) {
    case 'đã đăng ký':
      return 'status-active';
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

const fetchDataMemberReg = async () => {
    try {
        isLoading.value = true;
        if(!token) {
            showMessenger('Vui lòng đăng nhập lại', 'error');
            return;
        }
        
        const response = await axios.post(`${API_BASE_URL}/getMemberRegistration`,
            { userid },
            { headers: { token } }
        );
        const info = response.data;
        if(info.status === 'Thành công') {
                classData.value = info.data.map(session => ({
                    id: session.id,
                    customerName: session.customerName,
                    beginAt: session.beginAt,
                    endAt: session.endAt,
                    membershipName: session.membershipName,
                    status: session.status,
                    createAt: session.createAt
                }));
        } else {
            showMessenger(info.message || 'Không có gói tập nào', 'error'); // Fixed from data.message to info.message
            classData.value = [];
        }
    } catch (error) {
        handleApiError(error, 'Lỗi khi lấy dữ liệu lớp học');
    } finally {
        isLoading.value = false;
    }
}

const fetchDataPackage = async () =>{
    try {
        isLoading.value = true;
        if(!token) {
            showMessenger('Vui lòng đăng nhập lại', 'error');
            return;
        }
        
        const response = await axios.post(`${API_BASE_URL}/getpackages`,
            { userid },
            { headers: { token } }
        );
        const info = response.data;
        if(info.status === 'Lấy danh sách gói tập thành công') {
                packageData.value = info.list.map(session => ({
                    id: session.id,
                    namepackage: session.namepackage,
                    price: session.price,
                    description: session.description,
                    exercise_type: session.exercise_type
                }));
        } else {
            showMessenger(info.message || 'Không có gói tập nào', 'error'); // Fixed from data.message to info.message
            packageData.value = [];
        }
    } catch (error) {
        handleApiError(error, 'Lỗi khi lấy dữ liệu lớp học');
    } finally {
        isLoading.value = false;
    }
}

onMounted(() => {
    fetchDataMemberReg();
    fetchDataPackage();
});

const handleMenuClick = (menuId) => {
  activeMenu.value = menuId;
  console.log(`Menu clicked: ${menuId}`);
};

</script>
<template>
  <div class="customer-dashboard">
    <SlidebarCustomer :activeMenu="activeMenu" @menuClick="handleMenuClick" />
    
    <div class="content-area">
      <div class="header-section">
        <h1 class="page-title">Gói tập của tôi</h1>
        <button class="refresh-btn" @click="fetchDataMemberReg">
          <i class="fas fa-sync-alt"></i>
        </button>
      </div>
      
      <!-- Message alert -->
      <div v-if="message" :class="['alert', `alert-${messageType}`]">
        {{ message }}
      </div>

      <!-- Loading spinner -->
      <div v-if="isLoading" class="loading-container">
        <div class="spinner"></div>
      </div>
      
      <!-- Main content -->
      <div v-else class="content-container">
        <!-- No data message -->
        <div v-if="classData.length === 0" class="no-data">
          <i class="far fa-calendar-times"></i>
          <p>Bạn chưa đăng ký gói tập nào</p>
          <button class="btn-primary">Đăng ký ngay</button>
        </div>
        
        <!-- Membership data table -->
        <div v-else class="membership-data-container">
          <h2 class="section-title">Gói tập đã đăng ký</h2>
          <div class="membership-table">
            <table>
              <thead>
                <tr>
                  <th>Mã đăng ký</th>
                  <th>Tên gói</th>
                  <th>Thời gian</th>
                  <th>Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in classData" :key="item.id">
                  <td>{{ item.id }}</td>
                  <td>{{ item.membershipName }}</td>
                  <td>
                    <div class="date-info">
                      <span class="date-label">Bắt đầu:</span>
                      <span class="date-value">{{ item.beginAt }}</span>
                    </div>
                    <div class="date-info">
                      <span class="date-label">Kết thúc:</span>
                      <span class="date-value">{{ item.endAt }}</span>
                    </div>
                  </td>
                  <td>
                    <span class="status-badge" :class="getStatusClass(item.status)">
                      {{ item.status }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <!-- Available membership packages -->
        <div class="available-memberships">
          <h2 class="section-title">Các gói tập hiện có</h2>
          <div class="membership-table compact">
            <table>
              <thead>
                <tr>
                  <th>Tên gói tập</th>
                  <th>Mô tả</th>
                  <th>Giá (VNĐ)</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in packageData" :key="item.id">
                  <td>{{item.namepackage}}</td>
                  <td>{{item.description}}</td>
                  <td>{{item.price}}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="note">
            <i class="fas fa-info-circle"></i>
            <span>Liên hệ quản lý để đăng ký gói tập phù hợp với bạn</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>


.content-area {
  flex: 1;
  padding: 20px;
  margin-left: 240px; /* Adjust based on your sidebar width */
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  color: #333;
  margin: 0;
  font-size: 1.4rem;
  font-weight: 600;
}

.refresh-btn {
  background-color: transparent;
  border: none;
  color: #6c757d;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.refresh-btn:hover {
  background-color: #e9ecef;
  color: #495057;
}

.section-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #495057;
  margin: 0 0 15px 0;
}

/* Alert styling */
.alert {
  padding: 10px 15px;
  margin-bottom: 15px;
  border-radius: 4px;
  font-size: 0.9rem;
}

.alert-success {
  background-color: #d1e7dd;
  color: #0f5132;
  border: 1px solid #badbcc;
}

.alert-error {
  background-color: #f8d7da;
  color: #842029;
  border: 1px solid #f5c2c7;
}

/* Content container */
.content-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Membership data container */
.membership-data-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  padding: 15px;
}

.membership-table {
  width: 100%;
  overflow-x: auto;
}

.membership-table.compact th,
.membership-table.compact td {
  padding: 10px 12px;
  font-size: 0.9rem;
}

.membership-table table {
  width: 100%;
  border-collapse: collapse;
}

.membership-table th {
  background-color: #f8f9fa;
  color: #495057;
  font-weight: 600;
  text-align: left;
  padding: 12px 15px;
  border-bottom: 2px solid #e9ecef;
}

.membership-table td {
  padding: 12px 15px;
  border-bottom: 1px solid #e9ecef;
  color: #212529;
}

.membership-table tbody tr:hover {
  background-color: #f8f9fa;
}

/* Date info styling */
.date-info {
  display: flex;
  align-items: center;
  font-size: 0.85rem;
  margin-bottom: 3px;
}

.date-label {
  color: #6c757d;
  width: 70px;
}

.date-value {
  color: #212529;
}

/* Status badges - updated to match manager's view */
.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

/* Đã đăng ký */
.status-active {
  background-color: #d1fae5;
  color: #065f46;
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

/* Hủy */
.status-canceled {
  background-color: #fee2e2;
  color: #b91c1c;
}

/* Unknown status */
.status-unknown {
  background-color: #fee2e2;
  color: #ff5b5b;
}

/* The existing status-tag classes are kept for backward compatibility */
.status-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 50px;
  font-size: 0.75rem;
  font-weight: 500;
}

.status-tag.active {
  background-color: #d1fae5;
  color: #065f46;
}

.status-tag.inactive,
.status-tag.expired {
  background-color: #fee2e2;
  color: #b91c1c;
}

.status-tag.pending {
  background-color: #fff3cd;
  color: #ffc107;
}

/* Available memberships */
.available-memberships {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  padding: 15px;
}

.note {
  display: flex;
  align-items: center;
  margin-top: 10px;
  color: #6c757d;
  font-size: 0.85rem;
}

.note i {
  margin-right: 8px;
  color: #0dcaf0;
}

/* Loading spinner */
.loading-container {
  display: flex;
  justify-content: center;
  padding: 30px 0;
}

.spinner {
  border: 3px solid rgba(0,0,0,0.1);
  border-radius: 50%;
  border-top: 3px solid #0d6efd;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* No data message */
.no-data {
  text-align: center;
  padding: 30px 0;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.no-data i {
  font-size: 2.5rem;
  color: #adb5bd;
  margin-bottom: 15px;
}

.no-data p {
  margin-bottom: 20px;
  color: #6c757d;
}

.btn-primary {
  background-color: #0d6efd;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-primary:hover {
  background-color: #0b5ed7;
}

@media (max-width: 768px) {
  .content-area {
    margin-left: 0;
    padding: 15px;
  }
  
  .date-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .date-label {
    width: auto;
  }
}
</style>