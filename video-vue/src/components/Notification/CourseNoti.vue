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
          <span v-if="!notificationData.is_read" class="red-dot"></span>
          <p class="notification-title">{{ notificationData.notification.title }}</p>
          <p class="notification-date">{{ formatDate(notificationData.notification.notificationTime) }}</p>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
import axios from 'axios';
import { useRouter } from 'vue-router'; // Import Vue Router for navigation

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
    // 获取学生的所有通知
   async fetchNotifications() {
     try {
       const storedCourseInfo = sessionStorage.getItem('selectedCourse');
       const parsedCourseInfo = JSON.parse(storedCourseInfo);
       const classId = parsedCourseInfo ? parsedCourseInfo.classId : null;
       console.log("班级id", classId);

       const studentId = JSON.parse(sessionStorage.getItem('userInfo'))?.id;

       if (!studentId || !classId) {
         console.error("Student or class information not found in sessionStorage.");
         return;
       }

       const response = await axios.get(`/api/notifications/getAllForStudent`, {
         params: {
           classId: classId,
           studentId: studentId,
         },
       });

       this.notifications = response.data
         .map(notification => ({
           ...notification,
           is_read: notification.is_read,
         }))
         .sort((a, b) => b.notification.notificationTime - a.notification.notificationTime);

     } catch (error) {
       console.error('Error fetching notifications:', error);
     }
   },

    // 选择通知并将其标记为已读
    async selectNotification(notificationData) {
      if (!notificationData.is_read) {
        await this.markAsRead(notificationData.id);
        notificationData.is_read = true;
      }
      this.selectedNotification = notificationData;
      console.log("即将跳转的通知id",notificationData.id);
      this.$router.push({ name: 'notificationDetail', params: { notificationId: notificationData.id } });
    },

    // 标记通知为已读
    async markAsRead(notificationId) {
      try {
        const storedCourseInfo = sessionStorage.getItem('selectedCourse');
        const parsedCourseInfo = JSON.parse(storedCourseInfo);
        const classId = parsedCourseInfo ? parsedCourseInfo.classId : null;
        const studentId = JSON.parse(sessionStorage.getItem('userInfo'))?.id;

        if (!studentId || !classId) {
          console.error("Student or class information not found in sessionStorage.");
          return;
        }
        console.log(classId, studentId, notificationId);  // 调试，确保这些参数有值

      await axios.post('/api/notifications/markAsRead', null, {
        params: {
          classId: classId,
          studentId: studentId,
          notificationId: notificationId
        }
      })
      .then(response => {
        console.log('Notification marked as read:', response.data);
      })
      .catch(error => {
        console.error('Error marking notification as read:', error);
      });


      } catch (error) {
        console.error('Error marking notification as read:', error);
      }
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

.notification-content {
  font-size: 1em;
  color: #555;
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
