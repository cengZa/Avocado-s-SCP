<template>
  <div class="users">
    <!-- 错误信息 -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <!-- 成功信息 -->
    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>

    <div class="header" style="height: 10%; display: flex; align-items: center; padding: 0 20px; background-color: #64b5f6; color: white;">
      <div style="flex: 1; display: flex; align-items: center;">
        <h2 style="margin: 0;">智慧课程平台个人中心</h2>
      </div>
      <div class="header-right" style="display: flex; align-items: center; gap: 15px;">
        <div class="avatar-container-small">
          <img :src="avatarUrl" alt="头像" class="avatar-image-small">
        </div>
        <p>{{ user.username }}</p>
        <router-link to="/safe-exit" class="btn logout-btn">安全退出</router-link>
        <router-link to="/operating-instructions" class="btn operating-instructions">使用说明</router-link>
      </div>
    </div>

    <div class="user-profile" style="flex: 1; display: flex;">
      <div class="user-profile-l" style="width: 30%">
        <button @click="showSection('profile')" class="side-btn">个人信息</button>
        <button @click="showSection('password')" class="side-btn">修改密码</button>
        <button @click="showSection('favorites')" class="side-btn">收藏夹</button>
      </div>

      <div class="user-profile-r" style="width: 70%">

        <div v-if="currentSection === 'profile'" class="user-profile" style="width: 95%">
          <div class="profile-info" v-if="!isEditMode">
            <p><strong>姓名:</strong> {{ user.username }}</p>
            <p><strong>学号:</strong> {{ user.studentID }}</p>
            <p><strong>学院:</strong> {{ user.academy }}</p>
            <p><strong>邮箱:</strong> {{ user.email }}</p>
            <button @click="toggleEditMode" class="btn">编辑信息</button>
          </div>

          <div v-else class="edit-form">
            <div class="edit-field">
              <label><strong>姓名:</strong></label>
              <input v-model="editableUser.username" placeholder="用户名" disabled />
            </div>
            <div class="edit-field">
              <label><strong>学号:</strong></label>
              <input v-model="editableUser.studentID" placeholder="学号" disabled />
            </div>
            <div class="edit-field">
              <label><strong>学院:</strong></label>
              <input v-model="editableUser.academy" placeholder="学院" disabled />
            </div>
            <div class="edit-field">
              <label><strong>邮箱:</strong></label>
              <input v-model="editableUser.email" placeholder="邮箱" />
            </div>
            <div class="edit-buttons">
              <button @click="saveChanges" class="btn">保存</button>
              <button @click="cancelEdit" class="btn">取消</button>
            </div>
          </div>

          <div class="profile-header">
            <label for="avatarInput" class="avatar-label">
              <div class="avatar-container">
                <img :src="avatarUrl" alt="头像" class="avatar-image">
                <input id="avatarInput" type="file" @change="handleFileUpload" accept="image/*" style="display: none">
                <div class="overlay">点击更换头像
                </div>
              </div>
            </label>
            <h2>{{ user.username }}</h2>
          </div>
        </div>

        <div v-if="currentSection === 'password'" class="change-password" style="width: 95%; padding: 10px;">
          <h3>修改密码</h3>
          <div class="password-field">
            <label><strong>当前密码:</strong></label>
            <input type="password" v-model="passwordData.currentPassword" placeholder="当前密码" />
          </div>
          <div class="password-field">
            <label><strong>新密码:</strong></label>
            <input type="password" v-model="passwordData.newPassword" placeholder="新密码" />
          </div>
          <div class="password-field">
            <label><strong>确认新密码:</strong></label>
            <input type="password" v-model="passwordData.confirmNewPassword" placeholder="确认新密码" />
          </div>
          <div class="edit-buttons">
            <button @click="changePassword" class="btn">保存</button>
            <button @click="cancelEdit" class="btn">取消</button>
          </div>
        </div>
        <!-- 收藏夹模块 -->
        <div v-if="currentSection === 'favorites'" class="favorites" style="width: 100%">
          <h3>我的收藏</h3>
          <ul class="favorites-list">
            <li v-for="(post, index) in favoritePosts" :key="index" class="favorite-item">
              <div class="favorite-title"><strong>{{ post.title }}</strong></div>
              <div class="favorite-content">{{ post.content }}</div>
              <div class="favorite-author">作者: {{ post.author }}</div>
              <div class="favorite-date">收藏时间: {{ post.date }}</div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { reactive, ref, onMounted, watch } from 'vue';

export default {
  name: 'UserProfile',
  props: {
    initialAvatarUrl: {
      type: String,
      default: 'http://localhost:8080/upload/4a350ce9-e6e0-4c0d-9601-7117cef417ab.jpg' // 默认头像路径
    }
  },
  setup(props) {
    const user = reactive({
      username: '张三',
      studentID: '1234567890',
      academy: '软件学院',
      email: '2757582052@qq.com',
    });
    const response = reactive({
      username: '',
      studentID: '',
      academy: '',
      email: '',
      avatar: '',
      password: '',
    });
    const avatarUrl = ref(props.initialAvatarUrl); // 默认头像
    console.log("头像",avatarUrl);
    const errorMessage = ref('');  // 保存错误消息
    const successMessage = ref(''); // 保存成功消息
       // 定时清空消息函数
        const clearMessages = () => {
          setTimeout(() => {
            errorMessage.value = '';
            successMessage.value = '';
          }, 3000); // 信息显示 3 秒后清除
        };
   // 监听 errorMessage 和 successMessage 并触发定时清空
    watch([errorMessage, successMessage], ([newErrorMessage, newSuccessMessage]) => {
      if (newErrorMessage || newSuccessMessage) {
        clearMessages();
      }
    });


    onMounted(async () => {
      try {
      const userInfo = JSON.parse(sessionStorage.getItem('userInfo')); // 获取用户信息
      console.log("userInfo",userInfo);
       const userId = userInfo.id; // 获取用户ID
        const response = await axios.get(`api/user/showUserInfo/${userId}`);  // 获取用户信息的API
        console.log("response",response.data.data);
        user.username = response.data.data.nick_name;
        user.studentID = response.data.data.username;
        user.academy = response.data.data.academy;
        user.email = response.data.data.email;
        avatarUrl.value = response.data.data.avatar||"http://localhost:8080/upload/23fed9e5-3b77-4c04-b7c3-d762baf393b8.jpg";
      } catch (error) {
        console.error('获取用户信息失败:', error);
        errorMessage.value = '无法加载用户信息，请检查您的网络连接或稍后再试。';
      }
    });


      const handleFileUpload = async (event) => {
          const file = event.target.files[0];

          if (file) {
            const formData = new FormData();
            formData.append('files', file);


            try {
              const userInfo = JSON.parse(sessionStorage.getItem('userInfo')); // 获取用户信息
              const userId = userInfo.id;
                 formData.append('userId', userId);

             const response = await axios.post('/api/user/uploadAvatar', formData, {
                       headers: {
                         'Content-Type': 'multipart/form-data',
                       },
                     });
              // 更新前端头像显示
              console.log("头像回复",response);
              avatarUrl.value = response.data.data; // 假设服务器返回新的头像 URL
              successMessage.value = '头像上传成功！';

            } catch (error) {
              console.error('头像上传失败:', error);
              errorMessage.value = '头像上传失败，请稍后再试。';
            }
          }
        };


    const editableUser = reactive({ ...user });
    const isEditMode = ref(false);
    const currentSection = ref('profile');

    const passwordData = reactive({
      currentPassword: '',
      newPassword: '',
      confirmNewPassword: ''
    });

    const toggleEditMode = () => {
      Object.assign(editableUser, user);
      isEditMode.value = true;
    };

    const saveChanges = async() => {
      try {
          const userInfo = JSON.parse(sessionStorage.getItem('userInfo')); // 获取用户信息
                console.log("userInfo", userInfo);
                if (!userInfo) {
                  console.error('用户信息未找到');
                  this.loading = false;
                  return;
                }
                const userId = userInfo.id; // 获取用户ID
                console.log("userId", userId);
               console.log("新的邮箱信息",editableUser.email);
        // 向后端发送请求，更新用户信息

          const response = await axios.put('/api/user/updateUserEmail', null, {
              params: {
                userId: userId,
                email:editableUser.email
              },
              headers: {
                'Content-Type': 'application/json',  // 确保请求头正确
                'Accept': 'application/json'
              }
            });

        // 请求成功后，更新本地用户数据
        Object.assign(user, editableUser);
        isEditMode.value = false;
        successMessage.value = '邮箱已成功更新！';
      } catch (error) {
        console.error('保存用户信息失败:', error);
        errorMessage.value = '保存用户信息失败，请稍后再试。';
      }
    };


    const cancelEdit = () => {
      isEditMode.value = false;
    };

    const showSection = (section) => {
      currentSection.value = section;
      isEditMode.value = false;
    };
const changePassword = async () => {
    // 获取用户信息
    const userInfo = JSON.parse(sessionStorage.getItem('userInfo'));
    console.log("userInfo", userInfo);
    if (!userInfo) {
        console.error('用户信息未找到');
        this.loading = false;
        return;
    }

    const userId = userInfo.id; // 获取用户ID

    try {
        // 检查原始密码是否正确

                const checkPasswordResult = await axios.post('/api/user/checkPassword', null, {
                    params: {
                        userId: userId,
                        password:passwordData.currentPassword    // 新密码
                    },
                    headers: {
                        'Content-Type': 'application/json',  // 确保请求头正确
                        'Accept': 'application/json'
                    }
                });

      console.log("返回的信息",checkPasswordResult.data.data);
        if (checkPasswordResult.data.data==200) {  // 假设result中有success字段表示是否成功
            // 原始密码验证成功，继续验证新密码和确认密码是否一致
            if (passwordData.newPassword !== passwordData.confirmNewPassword) {
                errorMessage.value = '新密码和确认密码不一致，请重新输入。';
                console.log("两次输入的密码不一致");
                return;
            }

            // 向后端发送请求，更新密码
            const updatePasswordResponse = await axios.put('/api/user/updateUserPassword', null, {
                params: {
                    userId: userId,
                    password: passwordData.newPassword  // 新密码
                },
                headers: {
                    'Content-Type': 'application/json',  // 确保请求头正确
                    'Accept': 'application/json'
                }
            });

            // 假设后端返回 200 状态码表示成功
            if (updatePasswordResponse.status === 200) {
                successMessage.value = '密码已成功修改！';
                console.log("密码修改成功");

                // 清空输入的密码
                passwordData.currentPassword = '';
                passwordData.newPassword = '';
                passwordData.confirmNewPassword = '';
            }
        } else {
            // 原始密码错误的处理
            errorMessage.value = checkPasswordResult.data.message || '原始密码错误，请重新输入。';
            console.log("错误信息",errorMessage.value);
            console.log("原始密码错误");
        }
    } catch (error) {
        console.error('修改密码失败:', error);

        // 如果后端返回特定的错误，可以根据响应内容更详细地提示用户
        if (  errorMessage.value) {
            errorMessage.value = `修改密码失败：${error.response.data.message}`;
        } else {
            errorMessage.value = '修改密码失败，请稍后再试。';
        }
    }
};


    const favoritePosts = ref([
      {
        title: '机器学习入门',
        content: '这篇文章介绍了机器学习的基本概念和方法。',
        author: '李四',
        date: '2024-10-20'
      },
      {
        title: '深度学习的未来',
        content: '深度学习在未来可能会有更多的应用场景，例如自动驾驶和医疗诊断。',
        author: '王五',
        date: '2024-10-21'
      },
      {
        title: '数据科学入门指南',
        content: '数据科学是一门交叉学科，需要掌握统计学、编程和领域知识。',
        author: '赵六',
        date: '2024-10-22'
      }
    ]);

    const logout = () => {
      // 处理登出逻辑
      console.log('用户已登出');
    };

    return {
      user,
      editableUser,
      isEditMode,
      avatarUrl,
      toggleEditMode,
      saveChanges,
      cancelEdit,
      handleFileUpload,
      currentSection,
      showSection,
      passwordData,
      favoritePosts,
      changePassword,
      logout,
       errorMessage,
       successMessage,
    };
  },
};
</script>

<style scoped>

.users{

  display: flex;
  flex-direction: column;
  max-width: 100%;
  width: 100%;
  height: 100vh; /* 视口高度 */
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.5); /* 添加一些阴影效果 */
  background: linear-gradient(to right, #e3f2fd, #ffffff);
}
.header{
  height: 10%;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background-color: #64b5f6;
  color: white;
}
.header-right{
  display: flex;
  align-items: center;
  gap: 15px;
}
.avatar-container-small{
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
}
.avatar-image-small{
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.logout-btn{
  background-color: #f44336;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
.logout-btn:hover{
  background-color: #e53935;
}
.btn operating-instructions{
  background-color: #f44336;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
.btn operating-instructions:hover{
  background-color: #e53935;
}
.user-profile{
  flex: 1;
  display: flex;
  padding: 20px;
  gap: 20px;
}
.user-profile-l{
  border: 2px solid #ccc;
  border-radius: 10px;
  padding: 10px;
  box-sizing: border-box;
  background-color: #f5f5f5;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.side-btn {
  background-color: #64b5f6;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.3s ease;
}
.side-btn:hover {
  background-color: #42a5f5;
  transform: translateY(-2px);
}
.user-profile-r{
  border-radius: 5px;
  height: 100%;
  padding: 10px;
  box-sizing: border-box;
}
.avatar-container {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  text-align: center;
  transition: transform 0.3s;
  border: 1px solid #ccc;  /* 添加边框，边框宽度为 3px，颜色为灰色 */
}
.avatar-container:hover {
  transform: scale(1.1);
}
.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}
.avatar-container:hover .overlay {
  opacity: 1;
}
.profile-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  text-align: center;
  margin-left: 50px;
}
.edit-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.edit-field {
  display: flex;
  align-items: center;
  gap: 10px;
}
.edit-buttons {
  display: flex;
  flex-direction: row;
  gap: 10px;
}
.edit-form input {
  width: 70%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.password-field {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}
.edit-form button {
  margin: 5px;
  background-color: #64b5f6;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.3s ease, color 0.3s ease;
}
.edit-form button:hover {
  background-color: #42a5f5;
  transform: translateY(-2px);
}
.edit-form input[disabled] {
  cursor: not-allowed;
}
.btn {
  background-color: #64b5f6;
  color: white;
  border: none;
  padding: 10px 20px;
  margin-top: 10px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.3s ease, color 0.3s ease;
}
.btn:disabled {
  background-color: #b0bec5;
  color: #eceff1;
  cursor: not-allowed;
}
.btn:hover {
  background-color: #42a5f5;
  transform: translateY(-2px);
}
.error-message {
  color: red;
  background-color: #ffebee;
  padding: 10px;
  margin: 10px 20px;
  border: 1px solid #f44336;
  border-radius: 5px;
  text-align: center;
}
.success-message {
  color: green;
  background-color: #e8f5e9;
  padding: 10px;
  margin: 10px 20px;
  border: 1px solid #4caf50;
  border-radius: 5px;
  text-align: center;
}
.favorites {
  padding: 10px;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
.favorites-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.favorite-item {
  border: 1px solid #ddd;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 5px;
  background-color: #f9f9f9;
}
.favorite-title {
  font-weight: bold;
  margin-bottom: 5px;
}
.favorite-content {
  margin-bottom: 5px;
}
.favorite-author,
.favorite-date {
  font-size: 0.9em;
  color: #888;
}
</style>
