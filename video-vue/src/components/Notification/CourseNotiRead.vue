<template v-if="currentComponent === 'CourseNotiRead'">
  <div class="notification-container">
    <h2 class="header">已读通知</h2>
    <el-row gutter={20}>
      <el-col :span="24">
        <el-card
          v-for="(notification, index) in readNotifications"
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
  name: 'ReadNotificationComponent',
  data() {
    return {
      readNotifications: [],
      selectedNotification: null,
      currentComponent: 'CourseNotiRead', // 控制显示的组件
    };
  },
  created() {
    this.fetchReadNotifications();
  },
  methods: {
    // 获取学生的所有已读通知
    async fetchReadNotifications() {
      try {
           const studentId = JSON.parse(sessionStorage.getItem('userInfo'))?.id;
        const classId = JSON.parse(sessionStorage.getItem('selectedCourse'))?.classId; // 从 sessionStorage 获取班级ID

        if (!classId || !studentId) {
          ElMessage.error('班级ID或学生ID丢失');
          return;
        }

        const response = await axios.get('/api/notifications/getReadForStudent', {
          params: {
            classId: classId,
            studentId: studentId,
          },
        });

        // 处理返回的数据
        this.readNotifications = response.data.map(notification => ({
          ...notification,
          isRead: notification.readStatus === 'READ', // 判断已读状态
        }));
      } catch (error) {
        ElMessage.error('获取已读通知失败');
        console.error('Error fetching read notifications:', error);
      }
    },

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


</style>
