<template>
  <div>
    <!-- 使用 Element Plus 的 Calendar 组件 -->
    <el-calendar
      v-model="selectedDate"
      @change="fetchCourses"
      style="width: 100%; max-width: 400px; margin-bottom: 20px;"
    />

    <!-- 课程列表 -->
    <div v-if="courses.length > 0">
      <el-card
        v-for="course in courses"
        :key="course.courseId"
        class="course"
        shadow="hover"
        style="border: 2px solid #ebeef5; border-radius: 8px; margin-bottom: 20px; padding: 15px;"
      >
        <h3>{{ course.courseName }}</h3>
        <p><strong>时间：</strong>{{ course.startTime }} - {{ course.endTime }}</p>
        <p><strong>地点：</strong>{{ course.location }}</p>
        <p><strong>老师：</strong>{{ course.teacherName }}</p> <!-- 显示老师姓名 -->
      </el-card>
    </div>

    <!-- 当天没有课程时的提示 -->
    <div v-else style="text-align: center; margin-top: 20px;">
      <p>今天没有课程</p>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import axios from 'axios';

export default {
  name: 'CourseSchedule',
  data() {
    return {
      selectedDate: new Date().toISOString().split('T')[0],  // 初始日期为今天，确保格式为 yyyy-MM-dd
      courses: [], // 存储课程信息
    };
  },
  methods: {
    async fetchCourses() {
      try {
        // 从 sessionStorage 获取用户 ID
        const userId = JSON.parse(sessionStorage.getItem('userInfo'))?.id;

        // 打印用户 ID 和选中的日期
        console.log("用户ID:", userId);
        console.log("选中的日期:", this.selectedDate);

        // 格式化日期为 'yyyy-MM-dd' 确保传递给后端的格式正确
        const date = new Date(this.selectedDate);
        date.setDate(date.getDate() - 1); // 减去一天

        // 将日期格式化为 'yyyy-MM-dd' 格式
        const formattedDate = date.toISOString().split('T')[0];
        console.log("转换后的日期", formattedDate);

        // 请求指定日期的课程数据，确保传递的日期格式正确
        const response = await axios.get(`/api/classes/todayClass`, {
          params: {
            userId: userId,
            date: formattedDate
          }
        });

        console.log("后端返回的课程数据:", response.data);
        this.courses = response.data.data || [];  // 假设响应结构为 { data: [...] }
      } catch (error) {
        console.error('Error fetching courses:', error);
        this.$message.error('无法获取课程信息，请稍后再试'); // 弹出错误提示
      }
    }
  },
  watch: {
    selectedDate(newDate) {
      this.fetchCourses(); // 当日期发生变化时重新获取课程
    }
  },
  mounted() {
    this.fetchCourses(); // 页面加载时获取课程
  }
};
</script>

<style scoped>
/* 样式改为使用 Element Plus 样式的风格 */
.courses-list {
  margin-top: 20px;
}

.course {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background-color: #ffffff;
}

.el-calendar {
  margin-bottom: 20px;
}
</style>
