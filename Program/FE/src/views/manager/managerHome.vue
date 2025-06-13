<script setup>
import Slidebar from '@/components/SlidebarManager.vue'; // Import Slidebar component
import axios from 'axios';
import { onMounted, ref } from 'vue';

// State
const activeMenu = ref('dashboard');
const recentMembers = ref([]);
const revenueTimeframe = ref('month');
const membershipTimeframe = ref('month');

// Giá trị mô phỏng cho thống kê
const totalMembers = ref(256);
const totalRevenue = ref(125850000);
const activePackages = ref(187);
const expiringPackages = ref(24);

// Methods
const setActiveMenu = (menuId) => {
  activeMenu.value = menuId;
};

// Hàm format ngày (ví dụ: 27/04/2025)
function formatDate(dateString) {
  if (!dateString) return 'N/A';
  const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
  return new Date(dateString).toLocaleDateString('vi-VN', options);
}

// Format tiền tệ
function formatCurrency(value) {
  return new Intl.NumberFormat('vi-VN', { 
    style: 'currency', 
    currency: 'VND', 
    minimumFractionDigits: 0 
  }).format(value);
}

// Thêm tháng cho tính toán ngày hết hạn
function addMonths(dateString, months) {
  const date = new Date(dateString);
  date.setMonth(date.getMonth() + months);
  return date.toISOString();
}

// Tạo màu ngẫu nhiên cho avatar
function getRandomColorClass() {
  const colors = ['blue', 'green', 'orange', 'purple', 'red', 'teal'];
  return colors[Math.floor(Math.random() * colors.length)];
}

// Fetch dữ liệu từ API khi component được mount
onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:3001/manager/members');
    
    recentMembers.value = response.data.map(member => ({
      ...member,
      initials: member.name?.split(' ').map(word => word[0]).join('').toUpperCase() || 'FT',
      email: member.email || `${member.name?.toLowerCase().replace(/\s/g, '.')}@example.com`,
      membershipType: member.membershipType || 'Gói cơ bản',
      colorClass: getRandomColorClass()
    }));
  } catch (error) {
    console.error('Lỗi khi lấy danh sách khách hàng:', error);
    
    // Dữ liệu mẫu khi không thể kết nối API
    recentMembers.value = [
      {
        id: 1,
        name: 'Nguyễn Văn An',
        email: 'nguyenvanan@example.com',
        initials: 'NA',
        membershipType: 'Gói cao cấp',
        createdAt: '2025-04-28',
        
        colorClass: 'blue'
      },
      {
        id: 2,
        name: 'Trần Thị Bình',
        email: 'tranthibinh@example.com',
        initials: 'TB',
        membershipType: 'Gói tiêu chuẩn',
        createdAt: '2025-04-27',
        colorClass: 'green'
      },
      {
        id: 3,
        name: 'Lê Văn Cường',
        email: 'levancuong@example.com',
        initials: 'LC',
        membershipType: 'Gói cơ bản',
        createdAt: '2025-04-25',
        colorClass: 'orange'
      },
      {
        id: 4,
        name: 'Phạm Thị Dung',
        email: 'phamthidung@example.com',
        initials: 'PD',
        membershipType: 'Gói cao cấp',
        createdAt: '2025-04-23',
        colorClass: 'purple'
      },
      {
        id: 5,
        name: 'Hoàng Văn Giang',
        email: 'hoangvangiang@example.com',
        initials: 'HG',
        membershipType: 'Gói tiêu chuẩn',
        createdAt: '2025-04-20',
        colorClass: 'red'
      }
    ];
  }
});
</script>

<template>
  <div class="app-container">
    <!-- Sidebar -->
    <Slidebar :activeMenu="activeMenu" :onMenuClick="setActiveMenu" />

    <!-- Main Content -->
    <div class="main-content">

      <!-- Dashboard Content -->
      <div class="dashboard">
        <!-- Thẻ thống kê -->
        <div class="stats-container">
          <div class="stat-card">
            <div class="stat-icon member-icon">
              <i class="fas fa-users"></i>
            </div>
            <div class="stat-info">
              <h3>{{ totalMembers }}</h3>
              <p>Tổng thành viên</p>
              <div class="stat-change positive">
                <i class="fas fa-arrow-up"></i> 12% tháng này
              </div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon revenue-icon">
              <i class="fas fa-dollar-sign"></i>
            </div>
            <div class="stat-info">
              <h3>{{ formatCurrency(totalRevenue) }}</h3>
              <p>Tổng doanh thu</p>
              <div class="stat-change positive">
                <i class="fas fa-arrow-up"></i> 8.5% tháng này
              </div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon active-icon">
              <i class="fas fa-dumbbell"></i>
            </div>
            <div class="stat-info">
              <h3>{{ activePackages }}</h3>
              <p>Gói tập đang hoạt động</p>
              <div class="stat-change positive">
                <i class="fas fa-arrow-up"></i> 5% tháng này
              </div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon expire-icon">
              <i class="fas fa-calendar-times"></i>
            </div>
            <div class="stat-info">
              <h3>{{ expiringPackages }}</h3>
              <p>Gói sắp hết hạn</p>
              <div class="stat-change negative">
                <i class="fas fa-arrow-down"></i> 2% tháng này
              </div>
            </div>
          </div>
        </div>

        <!-- Biểu đồ doanh thu và thành viên -->
        <div class="charts-container">
          <div class="chart-card">
            <div class="chart-header">
              <h3>Tổng quan doanh thu</h3>
              <div class="chart-actions">
                <select v-model="revenueTimeframe" class="chart-select">
                  <option value="week">Tuần này</option>
                  <option value="month">Tháng này</option>
                  <option value="quarter">Quý này</option>
                  <option value="year">Năm này</option>
                </select>
              </div>
            </div>
            <div class="chart-placeholder">
              <!-- Giả lập biểu đồ doanh thu -->
              <div class="revenue-chart">
                <div class="chart-bar" style="height: 40%"></div>
                <div class="chart-bar" style="height: 65%"></div>
                <div class="chart-bar" style="height: 45%"></div>
                <div class="chart-bar" style="height: 80%"></div>
                <div class="chart-bar" style="height: 60%"></div>
                <div class="chart-bar" style="height: 75%"></div>
                <div class="chart-bar" style="height: 90%"></div>
              </div>
            </div>
          </div>

          <div class="chart-card">
            <div class="chart-header">
              <h3>Đăng ký mới</h3>
              <div class="chart-actions">
                <select v-model="membershipTimeframe" class="chart-select">
                  <option value="week">Tuần này</option>
                  <option value="month">Tháng này</option>
                </select>
              </div>
            </div>
            <div class="chart-placeholder">
              <!-- Giả lập biểu đồ thành viên -->
              <div class="members-chart">
                <div class="pie-chart">
                  <div class="pie-segment segment-1"></div>
                  <div class="pie-segment segment-2"></div>
                  <div class="pie-segment segment-3"></div>
                  <div class="pie-center"></div>
                </div>
                <div class="pie-legend">
                  <div class="legend-item">
                    <span class="legend-color legend-basic"></span>
                    <span>Gói cơ bản (45%)</span>
                  </div>
                  <div class="legend-item">
                    <span class="legend-color legend-standard"></span>
                    <span>Gói tiêu chuẩn (30%)</span>
                  </div>
                  <div class="legend-item">
                    <span class="legend-color legend-premium"></span>
                    <span>Gói cao cấp (25%)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Thành viên mới đăng ký với giao diện nâng cao -->
        <div class="section-header">
          <h2>Thành viên mới đăng ký</h2>
          <button class="view-all-btn">Xem tất cả <i class="fas fa-arrow-right"></i></button>
        </div>

        <div class="members-table">
          <table>
            <thead>
              <tr>
                <th>Thành viên</th>
                <th>Gói tập</th>
                <th>Ngày đăng ký</th>
                <th>Ngày hết hạn</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(member, index) in recentMembers" :key="index">
                <td class="member-cell">
                  <div class="member-avatar" :class="member.colorClass">{{ member.initials }}</div>
                  <div class="member-info">
                    <div class="member-name">{{ member.name }}</div>
                    <div class="member-email">{{ member.email || 'không có email' }}</div>
                  </div>
                </td>
                <td>
                  <div class="membership-badge">{{ member.membershipType }}</div>
                </td>
                <td>{{ formatDate(member.createdAt) }}</td>
                <td>{{ formatDate(member.expiryDate || addMonths(member.createdAt, 1)) }}</td>
                <td>
                  <div class="action-buttons">
                    <button class="action-btn view-btn" title="Xem chi tiết">
                      <i class="fas fa-eye"></i>
                    </button>
                    <button class="action-btn edit-btn" title="Chỉnh sửa">
                      <i class="fas fa-pen"></i>
                    </button>
                  </div>
                </td>
              </tr>
              
              <!-- Hiển thị khi không có dữ liệu -->
              <tr v-if="recentMembers.length === 0">
                <td colspan="6" class="no-data">
                  <div class="empty-state">
                    <i class="fas fa-users-slash"></i>
                    <p>Chưa có thành viên nào</p>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Main Layout */
.main-content {
  margin-left: 250px;
  padding: 25px;
  background-color: #f3f4f6;
  min-height: 100vh;
}

/* Stats Cards */
.stats-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon i {
  font-size: 24px;
  color: white;
}

.member-icon {
  background-color: #4f46e5;
}

.revenue-icon {
  background-color: #10b981;
}

.active-icon {
  background-color: #f59e0b;
}

.expire-icon {
  background-color: #ef4444;
}

.stat-info h3 {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 2px 0;
  color: #111827;
}

.stat-info p {
  font-size: 14px;
  margin: 0 0 5px 0;
  color: #6b7280;
}

.stat-change {
  font-size: 12px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 3px;
}

.positive {
  color: #10b981;
}

.negative {
  color: #ef4444;
}

/* Charts Section */
.charts-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.chart-card {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.chart-header h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  color: #111827;
}

.chart-select {
  padding: 6px 10px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 13px;
  color: #4b5563;
  background-color: white;
  cursor: pointer;
}

.chart-placeholder {
  height: 200px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 10px 0;
}

/* Revenue Chart - Bar Chart */
.revenue-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  width: 100%;
  height: 100%;
}

.chart-bar {
  width: 12%;
  background: linear-gradient(to top, #4f46e5, #818cf8);
  border-radius: 4px 4px 0 0;
  transition: height 0.3s;
}

.chart-bar:hover {
  background: linear-gradient(to top, #4338ca, #6366f1);
}

/* Members Chart - Pie Chart */
.members-chart {
  display: flex;
  align-items: center;
  justify-content: space-around;
  width: 100%;
  height: 100%;
}

.pie-chart {
  position: relative;
  width: 150px;
  height: 150px;
  border-radius: 50%;
}

.pie-segment {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.segment-1 {
  background-color: #4f46e5;
  clip-path: polygon(50% 50%, 100% 0, 100% 100%, 0 100%, 0 0);
}

.segment-2 {
  background-color: #10b981;
  clip-path: polygon(50% 50%, 100% 0, 100% 50%);
}

.segment-3 {
  background-color: #f59e0b;
  clip-path: polygon(50% 50%, 100% 0, 50% 0, 0 0);
}

.pie-center {
  position: absolute;
  width: 70px;
  height: 70px;
  background-color: white;
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.pie-legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  font-size: 13px;
}

.legend-color {
  display: inline-block;
  width: 12px;
  height: 12px;
  margin-right: 8px;
  border-radius: 3px;
}

.legend-basic { background-color: #4f46e5; }
.legend-standard { background-color: #10b981; }
.legend-premium { background-color: #f59e0b; }

/* Section Header */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.view-all-btn {
  background-color: transparent;
  color: #4f46e5;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: color 0.2s;
}

.view-all-btn:hover {
  color: #4338ca;
}

/* Members Table */
.members-table {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background-color: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

th {
  padding: 12px 16px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #4b5563;
}

td {
  padding: 12px 16px;
  border-bottom: 1px solid #f3f4f6;
  font-size: 14px;
  color: #111827;
}

tr:last-child td {
  border-bottom: none;
}

.member-cell {
  display: flex;
  align-items: center;
}

.member-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: white;
  margin-right: 12px;
  font-size: 14px;
}

.blue { background-color: #4f46e5; }
.green { background-color: #10b981; }
.orange { background-color: #f59e0b; }
.purple { background-color: #8b5cf6; }
.red { background-color: #ef4444; }
.teal { background-color: #14b8a6; }

.member-info {
  display: flex;
  flex-direction: column;
}

.member-name {
  font-weight: 500;
  margin-bottom: 2px;
}

.member-email {
  font-size: 12px;
  color: #6b7280;
}

.membership-badge {
  display: inline-block;
  padding: 4px 8px;
  background-color: #e0e7ff;
  color: #4f46e5;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 20px;
}

.badge-active {
  background-color: #dcfce7;
  color: #15803d;
}

.badge-inactive {
  background-color: #fee2e2;
  color: #b91c1c;
}

.badge-pending {
  background-color: #fef3c7;
  color: #92400e;
}

.action-buttons {
  display: flex;
  gap: 5px;
}

.action-btn {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
  color: #4b5563;
  background-color: transparent;
  transition: all 0.2s;
}

.action-btn:hover {
  background-color: #f3f4f6;
}

.view-btn:hover {
  color: #4f46e5;
}

.edit-btn:hover {
  color: #10b981;
}

/* Empty State */
.no-data {
  text-align: center;
  padding: 40px 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #9ca3af;
}

.empty-state i {
  font-size: 40px;
  margin-bottom: 10px;
}

.empty-state p {
  font-size: 14px;
}
</style>