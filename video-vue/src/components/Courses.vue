<template>
  <div class="courses">
    <h2>所有课程列表</h2>
    <div class="course-list">
      <Course v-for="course in courses" :key="course.id" :course="course" class="course-item" />
    </div>
  </div>
</template>

<script>
import Course from './Course.vue';
import axios from 'axios';

export default {
  components: {
    Course
  },
  data() {
    return {
      loading: true,
      courses: []
    };
  },
  mounted() {
    this.fetchCourses();
  },
  methods: {
    async fetchCourses() {
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

        // 根据用户角色调用不同的接口
        let response;
        if (userInfo.role === 'Student') {
          console.log("userrole", userInfo.role);
          response = await axios.get(`api/user/student/courses/${userId}`); // 学生课程接口
        } else if (userInfo.role === 'Teacher') {
          response = await axios.get(`api/user/teacher/courses/${userId}`); // 老师课程接口
        } else {
          console.error('未知角色:', userInfo.role);
          this.loading = false;
          return;
        }

        this.courses = response.data.data; // 假设返回数据在response.data.data中
        console.log('课程信息:', this.courses); // 打印课程信息
      } catch (error) {
        console.error('Error fetching courses:', error);
      } finally {
        this.loading = false; // 确保 loading 状态更新
      }
    }
  }
};
</script>

<style>
.courses {
  display: flex;
  flex-wrap: wrap;
  flex-direction: column;
  justify-content: space-around;
  text-align: left;
}

.course-list {
  display: flex;
  flex-wrap: wrap;
  margin-top: 20px;
}

.course-item {
  width: 250px;
  height: 220px;
  margin-left: 30px;
  margin-top: 20px;
}
</style>
