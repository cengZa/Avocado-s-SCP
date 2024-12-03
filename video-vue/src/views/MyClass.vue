<template>
  <div>
    <!-- 控制上一周和下一周 -->
    <div class="week-controls">
      <el-button @click="changeWeek(-1)" size="small">上一周</el-button>
      <span>{{ weekLabel }}</span>
      <el-button @click="changeWeek(1)" size="small">下一周</el-button>
    </div>

    <!-- 当前周课程信息 -->
    <el-row :gutter="20" class="week-courses">
      <el-col v-for="(day, index) in weekDays" :key="index" :span="3">
        <div class="day-box">
          <div class="day-header">{{ day.label }}</div>
          <div v-if="courses[day.date] && courses[day.date].length > 0">
            <!-- 显示当天的所有课程 -->
            <div class="course-items">
              <div v-for="(course, index) in courses[day.date]" :key="index" class="course-item">
                <p><strong>课程：</strong>{{ course.courseName }}</p>
                <p><strong>时间：</strong>{{ course.startTime }} - {{ course.endTime }}</p>
                <p><strong>地点：</strong>{{ course.location }}</p>
              </div>
            </div>
          </div>
          <div v-else>没有课程</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref } from 'vue';
import axios from 'axios';

export default {
  name: 'CourseSchedule',
  data() {
    return {
      currentDate: new Date(), // 当前日期
      weekDays: [], // 存储一周的日期
      courses: {}, // 存储课程信息
      weekLabel: '', // 存储当前显示的周的标签
    };
  },
  methods: {
    // 计算当前选中日期所在的周的周一和周日
    getWeekDates(date) {
      const currentDate = new Date(date);
      const dayOfWeek = currentDate.getDay();
      const diffToMonday = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
      const monday = new Date(currentDate);
      monday.setDate(currentDate.getDate() + diffToMonday);
      monday.setHours(0, 0, 0, 0);

      const weekDates = [];
      for (let i = 0; i < 7; i++) {
        const currentDay = new Date(monday);
        currentDay.setDate(monday.getDate() + i);
        weekDates.push({
          date: currentDay.toISOString().split('T')[0], // 格式化为 'yyyy-MM-dd'
          label: currentDay.toLocaleDateString(),
        });
      }
      return weekDates;
    },

    // 获取当前周的课程
    async fetchCourses() {
      try {
        // 从 sessionStorage 获取用户 ID
        const userId = JSON.parse(sessionStorage.getItem('userInfo'))?.id;

        // 获取一周的日期
        this.weekDays = this.getWeekDates(this.currentDate);

        // 格式化周的标签，例如 "2024年11月第1周"
        const startOfWeek = this.weekDays[0].date;
        const endOfWeek = this.weekDays[6].date;
        this.weekLabel = `${startOfWeek} 至 ${endOfWeek}`;

        // 为每一天获取课程数据
        for (const day of this.weekDays) {
          // 创建一个新的日期对象
          const dayDate = new Date(day.date);

          // 将日期减去一天
          dayDate.setDate(dayDate.getDate() - 1);

          // 格式化为 'yyyy-MM-dd' 格式
          const adjustedDate = dayDate.toISOString().split('T')[0];

          // 发起请求，使用调整后的日期
          const response = await axios.get('/api/classes/todayClass', {
            params: {
              userId: userId,
              date: adjustedDate, // 使用调整后的日期
            }
          });

          // 直接更新 courses 对象
          this.courses[day.date] = response.data.data || [];
        }
      } catch (error) {
        console.error('Error fetching courses:', error);
        this.$message.error('无法获取课程信息，请稍后再试');
      }
    },

    // 切换到上一周或下一周
    changeWeek(offset) {
      const newDate = new Date(this.currentDate);
      newDate.setDate(this.currentDate.getDate() + offset * 7); // 改变日期，上一周：-7天，下一周：+7天
      this.currentDate = newDate;
      this.fetchCourses();
    }
  },
  watch: {
    currentDate(newDate) {
      this.fetchCourses(); // 日期变化时重新获取课程
    }
  },
  mounted() {
    this.fetchCourses(); // 页面加载时获取当前周课程
  }
};
</script>

/* 控制上一周和下一周按钮样式 */
.week-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

/* 设置课程行 */
.week-courses {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.day-box {
  background-color: #f9f9f9;
  padding: 10px;
  border-radius: 6px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.day-header {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 10px;
}

.course-items {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  text-align: left;  /* 确保内容左对齐 */
}

.course-item {
  margin-bottom: 10px;
  padding: 10px;
  background-color: #ffffff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  text-align: left; /* 使课程内容左对齐 */
}

/* 为每个课程项提供一定间距和左对齐 */
.course-item p {
  margin: 5px 0;  /* 为每一行提供一些间隔 */
  word-wrap: break-word; /* 防止长单词撑开宽度 */
}

/* 使每列的宽度相同，确保日期和课程部分对齐 */
.el-row {
  display: flex;
  justify-content: space-between;
}
