<script setup>
import Sidebar from '@/components/SlidebarManager.vue'; // Import Sidebar component
import axios from 'axios';
import { ref } from 'vue';

const activeMenu = ref('payment');
const phone = ref(''); 
const paymentMethod = ref(''); 
const amount = ref(''); 
const paid = ref(''); 
const message = ref(''); 
const messageType = ref(''); 

// Danh sách phương thức thanh toán
const paymentMethods = [
  { value: 'cash', label: 'Tiền mặt' },
  { value: 'credit_card', label: 'Thẻ tín dụng' },
  { value: 'bank_transfer', label: 'Chuyển khoản' },
  { value: 'momo', label: 'Ví điện tử MoMo' },
  { value: 'vnpay', label: 'VNPAY' }
];

// Danh sách trạng thái thanh toán (boolean)
const paidStatus = [
  { value: true, label: 'Đã thanh toán' },
  { value: false, label: 'Chưa thanh toán' }
];

// Hàm xử lý khi nhấp vào menu
const handleMenuClick = (menuId) => {
  activeMenu.value = menuId;
  console.log(`Menu clicked: ${menuId}`);
};

// Hàm xử lý gửi form
const submitPayment = async () => {
  try {
    // Validate form
    if (!phone.value || !paymentMethod.value || !amount.value || paid.value === '') {
      message.value = 'Vui lòng điền đầy đủ thông tin';
      messageType.value = 'error';
      return;
    }

    // Lấy token từ localStorage
    const token = localStorage.getItem('token');
    if (!token) {
      message.value = 'Token không tồn tại. Vui lòng đăng nhập.';
      messageType.value = 'error';
      return;
    }

    // Gửi request đến server với token trong header
    const response = await axios.post(
  'http://localhost:3001/manager/addPayment',  
  {
    phone: phone.value,
    paymentMethod: paymentMethod.value,
    amount: parseFloat(amount.value), 
    paid: Boolean(paid.value)         
  },
  {
    headers: {
      token: token  // Thay đổi từ Authorization: Bearer ${token} thành token: token
    }
  }
);

    if (response.data.status === 'Thêm thanh toán thành công') {
      message.value = 'Thêm thanh toán thành công';
      messageType.value = 'success';
      setTimeout(() => {
      message.value = '';
  }, 5000);
      resetForm();
    } else {
      message.value = 'Thêm thanh toán thất bại';
      messageType.value = 'error';
    }
  } catch (error) {
    console.error('Lỗi khi thêm thanh toán:', error);
    message.value = `Lỗi: ${error.message || 'Đã xảy ra lỗi không xác định.Kiểm tra customer'}`;
    messageType.value = 'error';
  }
};

// Hàm reset form
const resetForm = () => {
  phone.value = '';
  paymentMethod.value = '';
  amount.value = '';
  paid.value = '';
};
</script>

<template>
  <div class="app-container">
    <!-- Sidebar -->
    <Sidebar :activeMenu="activeMenu" :onMenuClick="handleMenuClick" />

    <!-- Main Content -->
    <div class="main-content">
      <div class="payment-container">
        <h1 class="title">Thêm thanh toán mới</h1>

        <!-- Notification Message -->
        <div v-if="message" class="notification" :class="messageType">
          <i v-if="messageType === 'success'" class="fas fa-check-circle"></i>
          <i v-else class="fas fa-exclamation-circle"></i>
          {{ message }}
        </div>

        <!-- Payment Form -->
        <form @submit.prevent="submitPayment" class="payment-form">
          <div class="form-row">
            <div class="form-group">
              <label for="phone">Số điện thoại</label>
              <input
                type="text"
                id="phone"
                v-model="phone"
                placeholder="Nhập số điện thoại"
                class="form-control"
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="paymentMethod">Phương thức thanh toán</label>
              <select id="paymentMethod" v-model="paymentMethod" class="form-control">
                <option value="" disabled selected>Chọn phương thức thanh toán</option>
                <option v-for="method in paymentMethods" :key="method.value" :value="method.value">
                  {{ method.label }}
                </option>
              </select>
            </div>
            <div class="form-group">
              <label for="amount">Số tiền</label>
              <div class="input-with-icon">
                <input
                  type="number"
                  id="amount"
                  v-model="amount"
                  placeholder="Nhập số tiền"
                  class="form-control"
                />
                <span class="currency-icon">VND</span>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label for="paid">Trạng thái thanh toán</label>
            <select id="paid" v-model="paid" class="form-control payment-status">
              <option value="" disabled selected>Chọn trạng thái thanh toán</option>
              <option v-for="status in paidStatus" :key="status.value" :value="status.value" :class="status.value ? 'paid' : 'unpaid'">
                {{ status.label }}
              </option>
            </select>
          </div>

          <div class="form-actions">
            <button type="button" @click="resetForm" class="btn-secondary">
              <i class="fas fa-redo"></i> Đặt lại
            </button>
            <button type="submit" class="btn-primary">
              <i class="fas fa-save"></i> Lưu thanh toán
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
  min-height: 100vh;
}

.main-content {
  margin-left: 250px;
  padding: 24px;
  background-color: #f9fafb;
  flex: 1;
}

.payment-container {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.notification {
  padding: 12px 16px;
  border-radius: 6px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  font-size: 14px;
}

.notification i {
  margin-right: 10px;
  font-size: 16px;
}

.notification.success {
  background-color: #ecfdf5;
  color: #047857;
  border-left: 4px solid #059669;
}

.notification.error {
  background-color: #fef2f2;
  color: #b91c1c;
  border-left: 4px solid #dc2626;
}

.payment-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  gap: 20px;
}

.form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
}

label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.form-control {
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-control:focus {
  border-color: #4f46e5;
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
}

.input-with-icon {
  position: relative;
}

.currency-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #6b7280;
  font-size: 14px;
}

/* Styling for payment status select */
.payment-status option.paid {
  color: #047857;
  background-color: #d1fae5;
}

.payment-status option.unpaid {
  color: #b91c1c;
  background-color: #fee2e2;
}

/* Override select styling for payment status */
.payment-status {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=utf-8,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3E%3Cpath stroke='%236B7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='m6 8 4 4 4-4'/%3E%3C/svg%3E");
  background-position: right 0.5rem center;
  background-repeat: no-repeat;
  background-size: 1.5em 1.5em;
  padding-right: 2.5rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 20px;
}

.btn-primary,
.btn-secondary {
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
  border: none;
}

.btn-primary {
  background-color: #4f46e5;
  color: white;
}

.btn-primary:hover {
  background-color: #4338ca;
}

.btn-secondary {
  background-color: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
}

.btn-secondary:hover {
  background-color: #e5e7eb;
}

/* Responsive design */
@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 20px;
  }
}
</style>