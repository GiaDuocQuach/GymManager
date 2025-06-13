<script setup>
import axios from 'axios';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

// chứa các biến 
const username = ref('');
const password = ref('');
const errorMessage = ref('');
const router = useRouter();

// Hàm đăng nhập
const login = async () => {
  try {
    // Reset error message
    errorMessage.value = '';

    // Validate input
    if (!username.value || !password.value) {
      errorMessage.value = 'Vui lòng nhập tên đăng nhập và mật khẩu!';
      return;
    }

    const response = await axios.post(`http://localhost:3001/login`, {
      username: username.value,
      password: password.value,
    });

    if (response.data.status === 'Đăng nhập thành công') {
      const { token, userid , role } = response.data;
      // Lưu token, userId và role vào localStorage
      localStorage.setItem('token', token);
      localStorage.setItem('userid', userid);
      localStorage.setItem('role', role);

      axios.defaults.headers.common['token'] = token;

      // Điều hướng dựa trên vai trò từ API
      if (role === 'manager') {
        router.push('/managerHome');
      } else if (role === 'customer') {
        router.push('/customerHome');
      } else if (role === 'pt') {
        router.push('/ptHome');
      } else {
        // Trường hợp role không xác định
        errorMessage.value = 'Quyền truy cập không hợp lệ';
      }
    } else {
      errorMessage.value = response.data.message || 'Thông tin đăng nhập không đúng!';
    }
  } catch (error) {
    console.error('Lỗi đăng nhập:', error);
    errorMessage.value = error.response?.data?.message || 'Đăng nhập thất bại! Vui lòng thử lại.';
  }
};

// Reset thông báo lỗi
const resetError = () => {
  errorMessage.value = '';
};
</script>

<template>
  <div class="wrapper">
    <div class="container">
      <div class="form-container sign-in">
        <form @submit.prevent="login">
          <h1>Đăng nhập</h1>

          <div class="input-group">
            <input type="text" v-model="username" placeholder="Tên đăng nhập" required @input="resetError"
              autocomplete="username" />
          </div>

          <div class="input-group">
            <input type="password" v-model="password" placeholder="Mật khẩu" required @input="resetError"
              autocomplete="current-password" />
          </div>

          <button type="submit">Đăng nhập</button>
        </form>
      </div>
    </div>
    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
  </div>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap");

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: "Poppins", sans-serif;
}

.wrapper {
  background: linear-gradient(-45deg, #ee7752, #e73c7e, #23a6d5, #23d5ab);
  background-size: 400% 400%;
  animation: gradient 15s ease infinite;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  min-height: 100vh;
}

@keyframes gradient {
  0% {
    background-position: 0% 50%;
  }

  50% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0% 50%;
  }
}

.container {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
  width: 400px;
  max-width: 100%;
  min-height: 350px;
}

.form-container {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 30px 40px;
  height: 100%;
}

.input-group {
  position: relative;
  width: 100%;
  margin-bottom: 15px;
}

input {
  background-color: #f4f8f7;
  border: none;
  padding: 15px;
  font-size: 14px;
  border-radius: 10px;
  width: 100%;
  outline: none;
  transition: all 0.3s ease;
}

input:focus {
  background-color: #e0e0e0;
  box-shadow: 0 0 0 2px rgba(35, 166, 213, 0.3);
}

button {
  background: linear-gradient(45deg, #e73c7e, #23a6d5);
  color: #fff;
  font-size: 14px;
  padding: 12px 35px;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  margin-top: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
}

button:hover {
  transform: translateY(-2px);
  box-shadow: 0 7px 14px rgba(50, 50, 93, 0.1), 0 3px 6px rgba(0, 0, 0, 0.08);
}

button:active {
  transform: translateY(1px);
}

h1 {
  color: #333;
  margin-bottom: 25px;
  font-weight: 700;
  font-size: 28px;
  text-align: center;
}

.error {
  background-color: rgba(231, 60, 126, 0.8);
  color: white;
  padding: 10px 20px;
  border-radius: 10px;
  margin-top: 20px;
  font-size: 14px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  max-width: 400px;
}
</style>