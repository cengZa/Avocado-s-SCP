<template v-if="currentComponent === 'CourseNoti'">
  <div class="notification-container">
    <h2>课程通知</h2>
    <ul class="notification-list">
      <li
        v-for="(notificationData, index) in notifications"
        :key="notificationData.id"
        @click="selectNotification(notificationData)"
        :class="{'notification-item': true, 'unread': !notificationData.is_read}"
      >
        <div class="notification-summary">
          <!-- 显示通知标题和时间 -->
          <p class="notification-title">{{ notificationData.title }}</p>
          <p class="notification-date">{{ formatDate(notificationData.notificationTime) }}</p>

          <!-- 显示已读人数和总人数 -->
          <p class="read-info">
            已读人数: {{ notificationData.readCount }} / 总人数: {{ notificationData.totalCount }}
          </p>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
import axios from 'axios';
import { useRouter } from 'vue-router';

export default {
  name: 'NotificationComponent',
  data() {
    return {
      notifications: [],
      selectedNotification: null,
    };
  },
  created() {
    this.fetchNotifications();
  },
  methods: {
    // 获取教师端的所有通知
    async fetchNotifications() {
      try {
        const storedCourseInfo = sessionStorage.getItem('selectedCourse');
        const parsedCourseInfo = JSON.parse(storedCourseInfo);
        const classId = parsedCourseInfo ? parsedCourseInfo.classId : null;

        console.log("课程id", classId);

        if (!classId) {
          console.error("课程ID未找到");
          return;
        }

        // 获取所有通知
        const response = await axios.get(`/api/notifications/getAllByClass/${classId}`);
        this.notifications = response.data.map(notification => ({
          ...notification,
          is_read: false,  // 初始化通知为未读状态
          readCount: 0,
          totalCount: 0,
        }));
        console.log("通知列表", this.notifications);

        // 获取每条通知的已读人数和总人数
        for (let notification of this.notifications) {
          const countResponse = await axios.get(`/api/notifications/getReadCount`, {
            params: {
              classId: classId,
              notificationId: notification.id,
            },
          });

          // 解析返回的已读人数和总人数
          const [readCount, totalCount] = countResponse.data.split(' / ').map(item => item.replace(/\D/g, ''));

          notification.readCount = readCount;
          notification.totalCount = totalCount;
        }

      } catch (error) {
        console.error('获取通知失败:', error);
      }
    },

    // 选择通知并跳转到通知详情页
    selectNotification(notificationData) {
      this.selectedNotification = notificationData;
      this.$router.push({ name: 'notificationDetail', params: { notificationId: notificationData.id } });
    },

    // 格式化日期，精确到分钟
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
  },
};
</script>

<style scoped>
.notification-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
}

.notification-list {
  list-style-type: none;
  padding: 0;
}

.notification-item {
  padding: 15px;
  border-bottom: 1px solid #e0e0e0;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.notification-item.unread {
  background-color: #f9f9f9;
}

.notification-item:hover {
  background-color: #f0f0f0;
}

.notification-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
}

.notification-title {
  font-weight: bold;
  font-size: 1.2em;
}

.notification-date {
  font-size: 0.9em;
  color: #888;
}

.read-info {
  font-size: 0.9em;
  color: #4caf50;
  margin-top: 5px;
}

.red-dot {
  height: 10px;
  width: 10px;
  background-color: red;
  border-radius: 50%;
  display: inline-block;
  position: absolute;
  left: -20px;
}
</style>
