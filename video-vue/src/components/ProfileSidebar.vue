<template>
  <div class="sidebar">
    <div class="profile-section" v-if="userData">
      <img :src="userData.avatar" alt="用户头像显示失败" class="user-avatar">
      <h3 style="color: blue;">{{ userData.roleName }}</h3>
      <div class="user-details">
        <p>{{ userData.name  }}</p>
        <p>{{ userData.email }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      userData: {
        avatar: "http://localhost:8080/upload/23fed9e5-3b77-4c04-b7c3-d762baf393b8.jpg",
        roleName: '角色',
        name: '姓名',
        email: '邮箱'
      },
    };
  },
  mounted() {
    this.fetchUserData();
  },
  methods: {
    async fetchUserData() {
 const userInfo = JSON.parse(sessionStorage.getItem('userInfo')); // 获取用户信息
              const userId = userInfo.id;
      try {
        const response = await axios.get(`/api/user/showUserInfo/${userId}`); // 获取用户信息的接口
        console.log("头像",response.data);
        if (response.data) {
          this.userData.avatar = response.data.data.avatar||"http://localhost:8080/upload/23fed9e5-3b77-4c04-b7c3-d762baf393b8.jpg"; // 更新userData
          this.userData.name = response.data.data.nick_name; // 更新userData
          this.userData.email= response.data.data.email; // 更新userData
           if (response.data.data.role === 'Student') {
                this.userData.roleName = '学生';
              } else if (response.data.data.role === 'Teacher') {
                this.userData.roleName = '教师';
              } else {
                this.userData.roleName = '未知角色'; // 处理其他情况
              }
        }
      } catch (error) {
        console.error('Error fetching user data:', error);
      }
    }
  }
};
</script>

<style scoped>
.sidebar {
  flex: 1;
  padding: 20px;
  background-color: #f0f0f2;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-left: 10px;
}

.profile-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-bottom: 10px;
}

.user-details {
  text-align: center;
}
</style>
