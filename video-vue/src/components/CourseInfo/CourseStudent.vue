<template>
  <div class="student-info">
    <h2>学生信息</h2>
    <table>
      <thead>
        <tr>
          <th>学号</th>
          <th>姓名</th>
          <th>学院</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="student in students" :key="student.id">
          <td>{{ student.username }}</td>
          <td>{{ student.nick_name }}</td>
          <td>{{ student.academy }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      students: [],
    };
  },
  created() {
    this.fetchStudents();
  },
  methods: {
    async fetchStudents() {
      try {
        // 从 sessionStorage 中获取 classId
        const storedCourseInfo = sessionStorage.getItem('selectedCourse');
        const parsedCourseInfo = JSON.parse(storedCourseInfo);
        const classId = parsedCourseInfo.classId;
        console.log("班级id", classId);
        if (!classId) {
          console.error('classId 未找到');
          return;
        }
        // 使用 axios 发起 GET 请求
        const response = await axios.get(`api/classes/student/${classId}`);
        console.log("学生信息", response.data.data);
        this.students = response.data.data;
      } catch (error) {
        console.error('获取学生数据时发生错误:', error);
      }
    },
  },
};
</script>

<style scoped>
.grade-homework {
  padding: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead th,
tbody td {
  border-bottom: 1px solid #ccc;
  padding: 8px;
  text-align: center;
}

.details {
  margin-top: 10px;
}
</style>
