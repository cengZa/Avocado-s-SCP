<template>
  <div class="notification-detail-container">
    <!-- Loading Indicator -->
    <div v-if="!notification">
      <el-spin class="loading" />
    </div>

    <!-- Notification Content -->
    <div v-else>
      <!-- Notification Title -->
      <el-card class="notification-card" shadow="hover">
        <h2 class="notification-title">{{ notification.title }}</h2>

        <el-row>
          <el-col :span="24">
            <p class="notification-content">
              <strong>内容:</strong> {{ notification.content }}
            </p>
            <p class="notification-date">
              <strong>发布时间:</strong> {{ formatDate(notification.notificationTime) }}
            </p>
          </el-col>
        </el-row>

        <!-- Attachments Section -->
        <div v-if="notification.images.length > 0" class="attachments-section">
          <h3 class="attachment-title">图片附件:</h3>
          <el-row gutter={20}>
            <el-col v-for="(image, index) in notification.images" :key="index" :span="8">
              <el-image
                :src="image"
                alt="Notification Image"
                class="notification-image"
                @click="openImageModal(image)"
              />
            </el-col>
          </el-row>
        </div>

        <div v-if="notification.files.length > 0" class="attachments-section">
          <h3 class="attachment-title">文件附件:</h3>
          <el-list>
            <el-list-item v-for="(file, index) in notification.files" :key="index">
              <el-button
                type="text"
                class="download-link"
                @click.prevent="downloadFile(file)"
              >
                {{ getFileName(file) }}
              </el-button>
            </el-list-item>
          </el-list>
        </div>
      </el-card>
    </div>

    <!-- Image Modal -->
    <el-dialog v-model="isImageModalOpen" :visible.sync="isImageModalOpen" width="80%">
      <img :src="selectedImage" alt="Expanded Image" class="expanded-image" />
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeImageModal">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'NotificationDetail',
  props: ['notificationId'],
  data() {
    return {
      notification: null, // Notification data
      isImageModalOpen: false, // Image modal visibility
      selectedImage: null, // Selected image for the modal
    };
  },
  created() {
    this.fetchNotificationDetail(); // Fetch notification details on component creation
  },
  methods: {
    // Fetch notification details
    async fetchNotificationDetail() {
      try {
        const response = await axios.get(`/api/notifications/notification/${this.notificationId}`);
        this.notification = response.data;  // Store the notification data
        if (!this.notification.is_read) {
          await this.markAsRead(); // Mark as read if not already read
        }
      } catch (error) {
        console.error('获取通知详情时出错:', error);
      }
    },

    // Mark notification as read
    async markAsRead() {
      try {
        const studentId = JSON.parse(sessionStorage.getItem('userInfo'))?.id;
        const storedCourseInfo = sessionStorage.getItem('selectedCourse');
        const parsedCourseInfo = JSON.parse(storedCourseInfo);
        const classId = parsedCourseInfo ? parsedCourseInfo.classId : null;

        if (!studentId || !classId) {
          console.error("未找到学生或班级信息.");
          return;
        }

        await axios.post(`/api/notifications/markAsRead?classId=${classId}&studentId=${studentId}&notificationId=${this.notificationId}`);
        this.notification.is_read = true;
        this.notification.readTime = new Date().getTime();
      } catch (error) {
        console.error('标记通知为已读时出错:', error);
      }
    },

    // Format date
    formatDate(dateString) {
      const date = new Date(dateString * 1000);
      const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
      return date.toLocaleDateString(undefined, options);
    },

    // Extract file name from URL
    getFileName(fileUrl) {
      return fileUrl.split('/').pop();
    },

    // Download file
    async downloadFile(fileUrl) {
      try {
        const fileName = this.getFileName(fileUrl);
        const response = await axios.get(`/api/notifications/download/${fileName}`, {
          responseType: 'blob',
        });

        const blob = new Blob([response.data], { type: response.headers['content-type'] });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = fileName;
        link.click();
      } catch (error) {
        console.error('下载文件时出错:', error);
      }
    },

    // Open image modal
    openImageModal(image) {
      this.selectedImage = image;
      this.isImageModalOpen = true;
    },

    // Close image modal
    closeImageModal() {
      this.isImageModalOpen = false;
      this.selectedImage = null;
    }
  }
};
</script>

<style scoped>
/* Notification container */
.notification-detail-container {
  padding: 30px;
  background-color: #f5f7fa;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 900px;
  margin-left: auto;
  margin-right: auto;
}

/* Loading spinner */
.loading {
  font-size: 1.5em;
  color: #1e40af;
  text-align: center;
  margin-top: 50px;
}

/* Notification title */
.notification-title {
  font-size: 2em;
  color: #1e3a8a;
  margin-bottom: 10px;
}

/* Notification content */
.notification-content {
  font-size: 1.1em;
  color: #4b5563;
  margin-bottom: 15px;
}

/* Notification date */
.notification-date {
  font-size: 1em;
  color: #6b7280;
  margin-bottom: 25px;
}

/* Attachments section */
.attachments-section {
  margin-top: 20px;
}

/* Notification image */
.notification-image {
  width: 100%;
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin-top: 10px;
  cursor: pointer;
}

/* Expanded image modal */
.expanded-image {
  width: 100%;
  height: auto;
  display: block;
  margin: 0 auto;
}
</style>
