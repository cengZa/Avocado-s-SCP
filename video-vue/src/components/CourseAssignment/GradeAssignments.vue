<template>
  <div class="grade-homework">
    <h2>{{ homework.homeworkid }}</h2>
    <table>
      <thead>
      <tr>
        <th>学号</th>
        <th>姓名</th>
        <th>分数</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="student in students" :key="student.id">
        <td>{{ student.id }}</td>
        <td>{{ student.name }}</td>
        <td>{{ student.score }}</td>
        <td>
          <button @click="goToGradeAssignments(student)">批改作业</button>
        </td>
      </tr>
      </tbody>
    </table>

  </div>
</template>

<script>

import {useRouter} from 'vue-router';

export default {
  props: {
    homework: {
      type: Object,
      required: true
    }
  },

  data() {
    return {
      students: []
    };
  },
  created() {
    this.fetchStudents();
  },
  methods: {
    async fetchStudents() {
      try {
        const response = await fetch('http://localhost:8080/hello');
        if (!response.ok) {
          throw new Error('网络响应不正常');
        }
        this.students = await response.json();
      } catch (error) {
        console.error('获取学生数据时发生错误:', error);
      }
    },
    goToGradeAssignments(student) {
      // 使用 window.open 打开 GradingPage.vue 的新窗口，传学生 ID
      const gradingUrl = `/grading?id=${student.id}&homeworkId=${this.homework.homeworkid}`;
      window.open(gradingUrl, '_blank'); // 在新窗口打开该页面
    },
  }
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

