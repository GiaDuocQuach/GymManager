<script setup>
import {ref, onMounted} from 'vue';
import axios from 'axios';
import SlidebarCustomer from '@/components/SlidebarCustomer.vue';

const activeMenu = ref('payments');
const isLoading = ref(false);
const message = ref('');
const messageType = ref('');
const token = localStorage.getItem('token');
const userid = localStorage.getItem('userid');
const API_BASE_URL = 'http://localhost:3001/customer';

const paymentData = ref([]);

const showMessenger = (text, type = 'success') => {
  message.value = text;
  messageType.value = type;

  // Tự động ẩn sau 5 giây
  setTimeout(() => {
    message.value = '';
  }, 3000);
};

const handleApiError = (error, defaultMessage) => {
  console.error(defaultMessage, error);
  const errorMessage = error.response?.data?.message || error.message || 'Đã xảy ra lỗi không xác định';
  showMessenger(`${defaultMessage}: ${errorMessage}`, 'error');
};

const fetchPaymentData = async () => {
  isLoading.value = true;
  if (!token) {
      showMessenger('Vui lòng đăng nhập lại', 'error');
      return;
    }
  try {
    const response = await axios.post(`${API_BASE_URL}/getPayment`, 
    {
      userid
    },
    {
      headers: {token}
    });
    const info = response.data;
    if (info.status === 'Thành công' && Array.isArray(response.data.data)) {
      paymentData.value = info.data.map(item => ({
        id: item.id,
        method: item.method,
        amount: item.amount,
        paid: item.paid,
        createAt: item.createAt,
      }));
      showMessenger('Tải dữ liệu thành công');
    } else {
      showMessenger('Không có dữ liệu thanh toán', 'error');
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi tải dữ liệu thanh toán');
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchPaymentData();
});
</script>
<template>
  <div class="payment-dashboard">
    <SlidebarCustomer :activeMenu="activeMenu" />
    
    <div class="content-area">
      <div class="header-section">
        <h1 class="page-title">Thông báo giao dịch</h1>
        <button class="refresh-btn" @click="fetchPaymentData">
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
        <!-- Payment transactions -->
        <div class="payment-list-container">
          <div v-if="paymentData.length > 0" class="payment-list">
            <div v-for="payment in paymentData" :key="payment.id" class="payment-card">
              
              <div class="payment-details">
                <div class="detail-row">
                  <div class="detail-label">Thời gian:</div>
                  <div class="detail-value">
                    {{ payment.createAt }} 
                  </div>
                </div>
                
                <div class="detail-row">
                  <div class="detail-label">Giao dịch:</div>
                  <div :class="['detail-value transaction-amount']">
                    {{ payment.amount}} VND
                  </div>
                </div>
                
                <div class="detail-row">
                  <div class="detail-label">Phương thức thanh toán:</div>
                  <div class="detail-value description">
                    {{ payment.method }} 
                  
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div v-else class="no-payments">
            <i class="far fa-credit-card"></i>
            <p>Không có giao dịch nào</p>
            <p class="no-payments-tip">Các giao dịch của bạn sẽ hiển thị ở đây</p>
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
  color: white;
  margin: 0;
  font-size: 1.4rem;
  font-weight: 600;
}

.refresh-btn {
  background-color: rgba(255, 255, 255, 0.1);
  border: none;
  color: white;
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
  background-color: rgba(255, 255, 255, 0.2);
}

/* Alert styling */
.alert {
  padding: 10px 15px;
  margin-bottom: 15px;
  border-radius: 4px;
  font-size: 0.9rem;
}

.alert-success {
  background-color: rgba(209, 231, 221, 0.9);
  color: #0f5132;
  border: 1px solid rgba(186, 219, 204, 0.5);
}

.alert-error {
  background-color: rgba(248, 215, 218, 0.9);
  color: #842029;
  border: 1px solid rgba(245, 194, 199, 0.5);
}

/* Content container */
.content-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Payment list */
.payment-list-container {
  max-height: 80vh;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(255, 255, 255, 0.3) transparent;
  padding-right: 5px;
}

.payment-list-container::-webkit-scrollbar {
  width: 6px;
}

.payment-list-container::-webkit-scrollbar-track {
  background: transparent;
}

.payment-list-container::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 10px;
}

.payment-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.payment-card {
  background-color: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}


/* Payment details */
.payment-details {
  padding: 15px;
}

.detail-row {
  display: flex;
  margin-bottom: 10px;
}

.detail-label {
  width: 203px;
  color: #555;
  font-weight: 500;
}

.detail-value {
  flex: 1;
  color: #333;
  font-weight: 500;
}

.transaction-amount {
  font-weight: 600;
}


.description {
  line-height: 1.4;
}

.phone-number {
  color: #2175d9;
  text-decoration: none;
  font-weight: 600;
}

.phone-number:hover {
  text-decoration: underline;
}

/* No payments */
.no-payments {
  text-align: center;
  padding: 40px 0;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
}

.no-payments i {
  font-size: 3rem;
  color: #adb5bd;
  margin-bottom: 15px;
}

.no-payments p {
  margin: 5px 0;
  color: #6c757d;
}

.no-payments-tip {
  font-size: 0.9rem;
  max-width: 400px;
  margin: 10px auto;
}

/* Loading spinner */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
}

.spinner {
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top: 3px solid #ffffff;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 768px) {
  .content-area {
    margin-left: 0;
    padding: 15px;
  }
  
  .detail-row {
    flex-direction: column;
    gap: 5px;
    margin-bottom: 15px;
  }
  
  .detail-label {
    width: 100%;
    font-size: 0.85rem;
  }
}
</style>