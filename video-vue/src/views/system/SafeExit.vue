<template>
  <div class="safe-exit">
    <div class="exit-confirmation">
      <p>你确定要退出吗？</p>
      <button @click="logout" class="confirm-btn">确认退出</button>
      <button @click="cancel" class="cancel-btn">取消</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SafeExit',
  methods: {
    logout() {
      // 清除前端本地存储的数据
      sessionStorage.removeItem('userInfo');  // 或者根据实际存储的内容清除
      localStorage.removeItem('userInfo');    // 清除本地存储的数据

      // 跳转到登录页面
      this.$router.push({ name: 'Login' });
    },
    cancel() {
      // 取消退出，跳转回仪表盘
      this.$router.push({ name: 'Dashboard' });
    }
  }
}
</script>

<style scoped>
.safe-exit {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.exit-confirmation {
  padding: 20px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.confirm-btn {
  background-color: #f44336;
  color: white;
  padding: 10px 20px;
  margin-right: 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.confirm-btn:hover {
  background-color: #d32f2f;
}

.cancel-btn {
  background-color: #f1f1f1;
  color: #333;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}
</style>
