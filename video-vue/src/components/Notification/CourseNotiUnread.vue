<template v-if="currentComponent === 'CourseNotiUnread'">
  <div class="notification-container">
    <h2 class="header">未读通知</h2>
    <el-row gutter={20}>
      <el-col :span="24">
        <el-card
          v-for="(notification, index) in unreadNotifications"
          :key="index"
          class="notification-card"
          shadow="hover"
          @click="selectNotification(notification)"
        >
          <div class="notification-summary">
            <el-row type="flex" justify="space-between" align="middle">
              <el-col :span="16" class="notification-title">
                {{ notification.title }}
              </el-col>
              <el-col :span="8" class="notification-date">
                {{ formatDate(notification.notificationTime) }}
              </el-col>
            </el-row>
          </div>


        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios from 'axios';
import { ElMessage } from 'element-plus';

export default {
  name: 'UnreadNotificationComponent',
  data() {
    return {
      unreadNotifications: [],
      selectedNotification: null,
      currentComponent: 'CourseNotiUnread', // 控制显示的组件
    };
  },
  created() {
    this.fetchUnreadNotifications();
  },
  methods: {
    // 获取学生的所有未读通知
    async fetchUnreadNotifications() {
      try {
        const studentId = JSON.parse(sessionStorage.getItem('userInfo'))?.id;
        const classId = JSON.parse(sessionStorage.getItem('selectedCourse'))?.classId; // 从 sessionStorage 获取班级ID

        if (!classId || !studentId) {
          ElMessage.error('班级ID或学生ID丢失');
          return;
        }

        const response = await axios.get('/api/notifications/getUnreadForStudent', {
          params: {
            classId: classId,
            studentId: studentId,
          },
        });

        // 处理返回的数据
        this.unreadNotifications = response.data.map(notification => ({
          ...notification,
          isRead: notification.readStatus === 'UNREAD', // 判断未读状态
        }));
      } catch (error) {
        ElMessage.error('获取未读通知失败');
        console.error('Error fetching unread notifications:', error);
      }
    },

    // 格式化日期
    formatDate(dateString) {
      const options = {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      };
      return new Date(dateString * 1000).toLocaleString('zh-CN', options); // Format date to minute
    },

    // 选择通知并跳转到通知详情页
    selectNotification(notificationData) {
      this.selectedNotification = notificationData;
      console.log("即将跳转的通知id", notificationData.id);
      // 跳转到通知详情页
      this.$router.push({ name: 'notificationDetail', params: { notificationId: notificationData.id } });
    },
  },
};
</script>

<style scoped>
.notification-container {
  padding: 30px 40px;
  background-color: #f5f7fa; /* 淡灰背景色 */
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header {
  font-size: 1.3em;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.notification-card {
  padding: 10px 15px;
  margin-bottom: 10px;
  border: 1px solid #007bff; /* 蓝色边框 */
  border-radius: 10px; /* 圆角 */
  background-color: #ffffff; /* 白色背景 */
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  font-size: 0.9em; /* 缩小字体 */
}

.notification-card:hover {
  transform: translateY(-5px); /* 向上浮动效果 */
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3); /* 鼠标悬浮时添加阴影 */
}

.notification-summary {
  margin-bottom: 8px; /* 缩小标题和日期之间的间距 */
}

.notification-title {
  font-size: 1em; /* 减小标题字体 */
  font-weight: 600;
  color: #333;
  padding-right: 20px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-date {
  font-size: 0.8em; /* 缩小日期字体 */
  color: #888;
  text-align: right;
}



.notification-content {
  font-size: 0.85em; /* 缩小内容字体 */
  color: #555;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
