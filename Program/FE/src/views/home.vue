<script setup>
import { useRouter } from 'vue-router';

// Khởi tạo router
const router = useRouter();

// Hàm điều hướng đến trang đăng nhập
const goToLogin = () => {
  router.push('/login');
};

// Hàm cuộn mượt đến Footer
const scrollToFooter = () => {
  const footer = document.getElementById('footer');
  if (footer) {
    footer.scrollIntoView({ behavior: 'smooth' });
  }
};

// Hàm cuộn mượt đến bảng giá
const scrollToPrice = () => {
  const pricingSection = document.getElementById('pricing');
  if (pricingSection) {
    pricingSection.scrollIntoView({ behavior: 'smooth' });
  }
};
</script>

<template>
  <div class="home-page">
    <!-- Header -->
    <header class="header">
      <div class="logo">
        <h1>GYM</h1>
      </div>
      <nav class="nav">
        <ul>
          <li><a href="#">Trang chủ</a></li>
          <li><a @click.prevent="scrollToPrice">Gói tập</a></li>
          <li><a @click.prevent="scrollToFooter" href="#contact">Liên hệ</a></li>
        </ul>
      </nav>
      <div class="auth-buttons">
        <button @click="goToLogin" class="btn login">Đăng nhập</button>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1>Thay đổi cơ thể, nâng tầm cuộc sống</h1>
        <p>Tham gia ngay hôm nay để chinh phục mục tiêu sức khỏe của bạn!</p>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features">
      <h2>Tại sao chọn chúng tôi?</h2>
      <div class="features-grid">
        <div class="feature-item">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
          </svg>
          <h3>Thiết bị hiện đại</h3>
          <p>Trang thiết bị tối tân, đảm bảo hiệu quả tập luyện.</p>
        </div>
        <div class="feature-item">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
            <circle cx="12" cy="7" r="4" />
          </svg>
          <h3>Huấn luyện viên chuyên nghiệp</h3>
          <p>Đội ngũ HLV tận tâm, hỗ trợ bạn từng bước.</p>
        </div>
        <div class="feature-item">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M22 11.08V12a10 10 0 11-5.93-9.14" />
            <path d="M22 4L12 14.01l-3-3" />
          </svg>
          <h3>Lớp học đa dạng</h3>
          <p>Yoga, Zumba, Boxing và nhiều lớp học thú vị khác.</p>
        </div>
      </div>
    </section>

    <!-- Pricing Section -->
    <section id="pricing" class="pricing-section">
      <div class="container">
        <header>
          <h1>BẢNG GIÁ THÀNH VIÊN</h1>
          <p class="subtitle">Lựa chọn gói tập phù hợp với nhu cầu của bạn</p>
        </header>
        
        <div class="pricing-table">
          <!-- Gói cơ bản -->
          <div class="pricing-card">
            <div class="pricing-header">
              <h2 class="pricing-title">GÓI CƠ BẢN</h2>
              <div class="pricing-price">1.200.000<sup>đ</sup></div>
              <div class="pricing-period">/ tháng</div>
            </div>
            <div class="pricing-features">
              <ul>
                <li><span class="feature-icon">✓</span> Tập luyện không giới hạn</li>
                <li><span class="feature-icon">✓</span> Sử dụng máy tập cơ bản</li>
                <li><span class="feature-icon">✓</span> Hướng dẫn an toàn ban đầu</li>
                <li><span class="feature-icon">✓</span> Wifi miễn phí</li>
                <li><span class="feature-icon">✓</span> Khăn tắm miễn phí</li>
              </ul>
            </div>
          </div>
          
          <!-- Gói tiêu chuẩn (nổi bật) -->
          <div class="pricing-card highlight">
            <div class="highlight-label">PHỔ BIẾN</div>
            <div class="pricing-header">
              <h2 class="pricing-title">GÓI TIÊU CHUẨN</h2>
              <div class="pricing-price">2.500.000<sup>đ</sup></div>
              <div class="pricing-period">/ 3 tháng</div>
            </div>
            <div class="pricing-features">
              <ul>
                <li><span class="feature-icon">✓</span> Tất cả ưu đãi gói cơ bản</li>
                <li><span class="feature-icon">✓</span> Sử dụng toàn bộ máy tập</li>
                <li><span class="feature-icon">✓</span> 2 buổi PT miễn phí</li>
                <li><span class="feature-icon">✓</span> Đánh giá thể chất định kỳ</li>
                <li><span class="feature-icon">✓</span> Miễn phí phòng xông hơi</li>
              </ul>
            </div>
          </div>
          
          <!-- Gói cao cấp -->
          <div class="pricing-card">
            <div class="pricing-header">
              <h2 class="pricing-title">GÓI CAO CẤP</h2>
              <div class="pricing-price">8.000.000<sup>đ</sup></div>
              <div class="pricing-period">/ 12 tháng</div>
            </div>
            <div class="pricing-features">
              <ul>
                <li><span class="feature-icon">✓</span> Tất cả ưu đãi gói tiêu chuẩn</li>
                <li><span class="feature-icon">✓</span> 8 buổi PT cá nhân</li>
                <li><span class="feature-icon">✓</span> Chế độ dinh dưỡng riêng</li>
                <li><span class="feature-icon">✓</span> Miễn phí khóa học nhóm</li>
                <li><span class="feature-icon">✓</span> Ưu đãi 20% dịch vụ spa</li>
              </ul>
            </div>
          </div>
        </div>
        
        <div class="note">
          <p>* Giá trên chưa bao gồm VAT. Áp dụng từ ngày 01/04/2025.</p>
          <p>Liên hệ hotline: 1900 1234 để được tư vấn thêm.</p>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer id="footer" class="footer">
      <p>Có sức khỏe là có tất cả</p>
      <div class="social-links">
        <a href="https://www.facebook.com/piggym2020?fbclid=PAZXh0bgNhZW0CMTEAAaeLF-VPJjRJHRrSM_f-PZ1N-2EpwKyRttZmYXP5C9XtJ0jzrEulvp07n-3SCg_aem_Dep9AZ1Z44BbeVQ2HzAUew"><img width="50" height="50" src="https://img.icons8.com/ios-filled/50/facebook-new.png" alt="facebook-new"/></a>
        <a href="https://www.instagram.com/piggym.2020/"><img width="60" height="60" src="https://img.icons8.com/ios-glyphs/60/instagram-new.png" alt="instagram-new" /></a>
        <a href="https://www.tiktok.com/@piggym2020"><img width="50" height="50" src="https://img.icons8.com/ios-filled/50/tiktok--v1.png" alt="tiktok--v1" /></a>
      </div>
      <p>Liên hệ: cskh.piggymdn@gmail.com | 085 966 3468</p>
    </footer>
  </div>
</template>

<style scoped>
/* Nhúng Google Fonts */
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&family=Roboto:wght@900&display=swap');

/* CSS chung */
.home-page {
  font-family: 'Poppins', sans-serif;
  color: #333;
  overflow-x: hidden;
  scroll-behavior: smooth;
}

/* Header */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 3rem;
  background: linear-gradient(180deg, #121212, #1a1a1a);
  color: white;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
}

.logo h1 {
  font-family: 'Roboto', sans-serif;
  font-size: 2rem;
  font-weight: 900;
  color: #ff4500;
  margin: 0;
  transition: transform 0.3s ease;
}

.logo h1:hover {
  transform: scale(1.05);
}

.nav ul {
  display: flex;
  list-style: none;
  gap: 2.5rem;
}

.nav a {
  color: #e0e0e0;
  text-decoration: none;
  font-weight: 600;
  font-size: 1.2rem;
  position: relative;
  transition: color 0.3s ease;
  cursor: pointer;
}

.nav a::after {
  content: '';
  position: absolute;
  width: 0;
  height: 3px;
  bottom: -6px;
  left: 0;
  background: linear-gradient(90deg, #ff4500, #ff6b6b);
  transition: width 0.3s ease;
}

.nav a:hover::after {
  width: 100%;
}

.nav a:hover {
  color: #ff4500;
}

.auth-buttons .btn.login {
  background: linear-gradient(45deg, #007bff, #00b7ff);
  color: white;
  padding: 0.8rem 2rem;
  border: none;
  border-radius: 30px;
  font-weight: 600;
  font-size: 1.1rem;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.auth-buttons .btn.login:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 123, 255, 0.5);
}

/* Hero Section */
.hero {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: white;
  background: linear-gradient(rgba(0, 0, 0, 0.4), rgba(0, 0, 0, 0.4)), url('') no-repeat center center/cover;
  background-size: cover;
  background-attachment: scroll;
  overflow: hidden;
}

.hero-content {
  max-width: 800px;
  padding: 0 1.5rem;
  animation: slideIn 1.2s ease-out;
  z-index: 1;
}

.hero h1 {
  font-family: 'Roboto', sans-serif;
  font-size: 4.5rem;
  font-weight: 900;
  margin-bottom: 1.5rem;
  line-height: 1.1;
  text-shadow: 4px 4px 8px rgba(0, 0, 0, 0.7);
  animation: fadeInUp 1s ease-out;
}

.hero p {
  font-size: 1.5rem;
  margin-bottom: 3rem;
  opacity: 0.95;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  animation: fadeInUp 1.2s ease-out 0.2s;
}

.hero .cta {
  background: linear-gradient(45deg, #ff4500, #ff6b6b);
  color: white;
  padding: 1.2rem 3rem;
  border: none;
  border-radius: 30px;
  font-size: 1.4rem;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  animation: fadeInUp 1.4s ease-out 0.4s;
}

.hero .cta:hover {
  transform: scale(1.1);
  box-shadow: 0 8px 25px rgba(255, 69, 0, 0.6);
}

/* Animations */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Features Section */
.features {
  padding: 6rem 3rem;
  background: linear-gradient(180deg, #f9f9f9, #ffffff);
  text-align: center;
}

.features h2 {
  font-family: 'Roboto', sans-serif;
  font-size: 3rem;
  font-weight: 900;
  margin-bottom: 3.5rem;
  color: #121212;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 3rem;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-item {
  background: white;
  padding: 2.5rem;
  border-radius: 20px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-12px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.feature-item .icon {
  width: 80px;
  height: 80px;
  margin-bottom: 1.5rem;
  stroke: #ff4500;
  stroke-width: 2;
}

.feature-item h3 {
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: #121212;
}

.feature-item p {
  font-size: 1.1rem;
  color: #555;
}

/* Pricing Section */
.pricing-section {
  padding: 6rem 0;
  background: linear-gradient(180deg, #ffffff, #f5f5f5);
  text-align: center;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.pricing-section h1 {
  font-family: 'Roboto', sans-serif;
  color: #2c3e50;
  font-size: 2.5rem;
  margin-bottom: 10px;
}

.subtitle {
  color: #e74c3c;
  font-size: 1.2rem;
  font-weight: 300;
  margin-bottom: 40px;
}

.pricing-table {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
}

.pricing-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  width: 300px;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.pricing-card:hover {
  transform: translateY(-10px);
}

.pricing-header {
  background: #2c3e50;
  color: white;
  padding: 20px;
  text-align: center;
}

.pricing-title {
  font-size: 1.5rem;
  margin: 0;
}

.pricing-price {
  font-size: 2.5rem;
  margin: 15px 0;
  font-weight: bold;
}

.pricing-period {
  font-size: 1rem;
  opacity: 0.8;
}

.pricing-features {
  padding: 20px;
}

.pricing-features ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.pricing-features li {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
}

.pricing-features li:last-child {
  border-bottom: none;
}

.feature-icon {
  color: #f39c12;
  margin-right: 10px;
  font-weight: bold;
}

.pricing-footer {
  padding: 20px;
  text-align: center;
}

.pricing-footer .btn {
  display: inline-block;
  background: #e74c3c;
  color: white;
  padding: 12px 25px;
  border-radius: 5px;
  text-decoration: none;
  font-weight: bold;
  transition: background 0.3s ease;
  border: none;
  cursor: pointer;
  font-size: 1rem;
}

.pricing-footer .btn:hover {
  background: #c0392b;
}

.highlight {
  border: 2px solid #f39c12;
  position: relative;
}

.highlight-label {
  position: absolute;
  top: -10px;
  right: 20px;
  background: #f39c12;
  color: white;
  padding: 5px 10px;
  border-radius: 5px;
  font-size: 0.8rem;
  font-weight: bold;
}

.note {
  text-align: center;
  margin-top: 30px;
  font-style: italic;
  color: #7f8c8d;
}

/* Footer */
.footer {
  background-color: #fff;
  color:#333;
  text-align: center;
  padding: 4rem 2rem;
}

.footer p {
  font-size: 1.2rem;
  margin: 0.7rem 0;
}

.social-links {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin: 2rem 0;
}

.social-links a {
  transition: transform 0.3s ease;
}

.social-links a:hover {
  transform: rotate(360deg);
}

.social-links img {
  height: 50px;
}

/* Responsive Design */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 1.5rem;
    padding: 1rem 2rem;
  }

  .nav ul {
    flex-direction: column;
    gap: 1.2rem;
  }

  .hero {
    height: 80vh;
  }

  .hero h1 {
    font-size: 3rem;
  }

  .hero p {
    font-size: 1.2rem;
  }

  .hero .cta {
    padding: 1rem 2.5rem;
    font-size: 1.2rem;
  }

  .features {
    padding: 4rem 1.5rem;
  }

  .features h2 {
    font-size: 2.2rem;
  }

  .pricing-table {
    flex-direction: column;
    align-items: center;
  }

  .footer {
    padding: 3rem 1.5rem;
  }
}

@media (max-width: 480px) {
  .header {
    padding: 1rem;
  }

  .logo h1 {
    font-size: 1.8rem;
  }

  .nav a {
    font-size: 1rem;
  }

  .hero h1 {
    font-size: 2.2rem;
  }

  .hero p {
    font-size: 1rem;
  }

  .features h2 {
    font-size: 1.8rem;
  }

  .feature-item .icon {
    width: 60px;
    height: 60px;
  }

  .pricing-card {
    width: 100%;
    max-width: 300px;
  }
}
</style>