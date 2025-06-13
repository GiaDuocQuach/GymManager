<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import SlidebarCustomer from '@/components/SlidebarCustomer.vue';

const activeMenu = ref('reviewss');
const isLoading = ref(false);
const message = ref('');
const messageType = ref('');
const token = localStorage.getItem('token');
const userid = localStorage.getItem('userid');
const API_BASE_URL = 'http://localhost:3001/customer';

const reviewData = ref([]);
const formSubmit = ref({
  text: '',
});

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

const fetchReviewData = async () => {
  try {
    isLoading.value = true;
    if (!token) {
      showMessenger('Vui lòng đăng nhập lại', 'error');
      return;
    }

    const response = await axios.post(`${API_BASE_URL}/getReviews`,
      { userid },
      { headers: { token } }
    );

    if (response.data.status === 'Lấy danh sách review thành công' && Array.isArray(response.data.data)) {
      reviewData.value = response.data.data.map(session => ({
        reviewId: session.reviewId,
        customer: session.customer,
        text: session.text,
        date: session.date,
      }));
    } else {
      showMessenger(response.data.message);
      reviewData.value = [];
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi lấy dữ liệu đánh giá');
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchReviewData();
});

const submitReview = async () => {
  try {
    isLoading.value = true;
    if (!token) {
      showMessenger('Vui lòng đăng nhập lại', 'error');
      return;
    }

    const response = await axios.post(`${API_BASE_URL}/addReview`,
      { userid, text: formSubmit.value.text },
      { headers: { token } }
    );

    if (response.data.status === 'success') {
      showMessenger('Đánh giá đã được gửi thành công', 'success');
      resetForm();
      fetchReviewData();
    } else {
      showMessenger(response.data.message || 'Không thể gửi đánh giá', 'error');
    }
  } catch (error) {
    handleApiError(error, 'Lỗi khi gửi đánh giá');
  } finally {
    isLoading.value = false;
  }
};

const resetForm = () => {
  formSubmit.value.text = '';
};

const handleMenuClick = (menuId) => {
  activeMenu.value = menuId;
  console.log(`Menu clicked: ${menuId}`);
};

// Thêm các hàm tiện ích mới
const getInitials = (name) => {
  if (!name) return '?';
  return name
    .split(' ')
    .map(word => word[0])
    .slice(-2)
    .join('')
    .toUpperCase();
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  if (isNaN(date)) return dateString;

  const options = { year: 'numeric', month: 'short', day: 'numeric' };
  return date.toLocaleDateString('vi-VN', options);
};
</script>

<template>
  <div class="review-dashboard">
    <SlidebarCustomer :activeMenu="activeMenu" @menuClick="handleMenuClick" />

    <div class="content-area">
      <div class="header-section">
        <h1 class="page-title">Đánh giá & Phản hồi</h1>
        <button class="refresh-btn" @click="fetchReviewData">
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
        <!-- Review submission form -->
        <div class="review-form-container">
          <form @submit.prevent="submitReview" class="review-form">
            <div class="form-group">
              <label for="review-text">
                <h2 class="section-title">Nội dung đánh giá</h2>
              </label>
              <textarea id="review-text" v-model="formSubmit.text"
                placeholder="Nhập đánh giá của bạn về dịch vụ, HLV hoặc cơ sở vật chất..." rows="4" required></textarea>
            </div>
            <div class="form-actions">
              <button type="button" @click="resetForm" class="reset-button">Xóa</button>
              <button type="submit" class="submit-button" :disabled="isLoading || !formSubmit.text.trim()">
                Gửi đánh giá
              </button>
            </div>
          </form>
        </div>

        <!-- Reviews list -->
        <div class="reviews-container">
          <h2 class="section-title">Các đánh giá của bạn</h2>

          <div v-if="reviewData.length > 0" class="reviews-list">
            <div v-for="review in reviewData" :key="review.reviewId" class="review-card">
              <div class="review-avatar">
                <div class="avatar-circle">
                  <span>{{ getInitials(review.customer) }}</span>
                </div>
              </div>
              <div class="review-content-wrapper">
                <div class="review-header">
                  <div class="review-author">
                    <span class="author-name">{{ review.customer }}</span>
                    <span class="review-date">{{ formatDate(review.date) }}</span>
                  </div>
                </div>
                <div class="review-content">
                  <p>{{ review.text }}</p>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="no-reviews">
            <i class="far fa-comment-dots"></i>
            <p>Bạn chưa có đánh giá nào</p>
            <p class="no-reviews-tip">Chia sẻ trải nghiệm của bạn để giúp chúng tôi cải thiện dịch vụ</p>
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
  margin-left: 240px;
  /* Adjust based on your sidebar width */
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

/* Review form container */
.review-form-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.review-form .form-group {
  margin-bottom: 15px;
}

.review-form label {
  display: block;
  font-weight: 500;
  margin-bottom: 6px;
  color: #495057;
}

.review-form textarea {
  width: 97%;
  padding: 12px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  resize: vertical;
  font-family: inherit;
  font-size: 0.95rem;
  transition: border-color 0.2s;
}

.review-form textarea:focus {
  outline: none;
  border-color: #80bdff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.submit-button {
  background-color: #0d6efd;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-button:hover:not(:disabled) {
  background-color: #0b5ed7;
}

.submit-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.reset-button {
  background-color: #f8f9fa;
  color: #6c757d;
  border: 1px solid #ced4da;
  padding: 10px 20px;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.reset-button:hover {
  background-color: #e9ecef;
  color: #495057;
}

/* Reviews list - Redesigned */
.reviews-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-card {
  display: flex;
  gap: 15px;
  padding-bottom: 15px;
}

/* Avatar styling */
.review-avatar {
  flex-shrink: 0;
}

.avatar-circle {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background-color: #6c5ce7;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
}

/* Content area styling */
.review-content-wrapper {
  flex-grow: 1;
  background-color: #f8f9fa;
  border-radius: 12px;
  padding: 12px 15px;
  position: relative;
}

.review-content-wrapper::before {
  content: '';
  position: absolute;
  top: 15px;
  left: -8px;
  width: 15px;
  height: 15px;
  background-color: #f8f9fa;
  transform: rotate(45deg);
}

.review-header {
  margin-bottom: 8px;
}

.review-author {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.author-name {
  font-weight: 600;
  color: #333;
  font-size: 0.95rem;
}

.review-date {
  color: #6c757d;
  font-size: 0.8rem;
}

.review-content {
  color: #212529;
  line-height: 1.5;
}

.review-content p {
  margin: 0;
}


/* No reviews */
.no-reviews {
  text-align: center;
  padding: 30px 0;
}

.no-reviews i {
  font-size: 3rem;
  color: #adb5bd;
  margin-bottom: 15px;
}

.no-reviews p {
  margin: 5px 0;
  color: #6c757d;
}

.no-reviews-tip {
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
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 3px solid #0d6efd;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

/* Responsive */
@media (max-width: 768px) {
  .content-area {
    margin-left: 0;
    padding: 15px;
  }

  .review-header {
    flex-direction: column;
    gap: 5px;
  }

  .form-actions {
    flex-direction: column-reverse;
    gap: 10px;
  }

  .submit-button,
  .reset-button {
    width: 100%;
  }

  .review-card {
    gap: 10px;
  }

  .avatar-circle {
    width: 35px;
    height: 35px;
    font-size: 14px;
  }

  .review-content-wrapper {
    padding: 10px 12px;
  }

  .author-name {
    font-size: 0.9rem;
  }

  .review-author {
    flex-direction: column;
    gap: 2px;
  }
}
</style>