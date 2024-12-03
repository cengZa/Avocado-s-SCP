<template>
  <div class="grade-homework">
    <h2>批改作业</h2>
    <table>
      <thead>
      <tr>
        <th>作业标题</th>
        <th>作业截止时间</th>
        <th>批改截止时间</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(homework, index) in homeworks" :key="index">
        <td>{{ homework.title }}</td>
        <td>{{ homework.dueDate }}</td>
        <td>{{ homework.gradingDueDate }}</td>
        <td><button @click="goToGradeAssignments(homework)">查看作业提交情况</button></td>
      </tr>
      </tbody>
    </table>

  </div >
</template >

<script >
export default {
  data() {
    return {
      homeworks: [
        {
          homeworkid:'1',
          title: '第一周作业',
          dueDate: '2024-10-15',
          gradingDueDate: '2024-10-20',
          submissions: [
            { studentId: '001', studentName: '张三', content: '这是我的作业', score: null },
            { studentId: '002', studentName: '李四', content: '这是我的作业', score: null }
          ]
        },
        {
          homeworkid:'2',
          title: '第二周作业',
          dueDate: '2024-10-20',
          gradingDueDate: '2024-10-25',
          submissions: [
            { studentId: '003', studentName: '王五', content: '这是我的作业', score: null }
          ]
        }
      ],
    };
  },

  methods: {
    goToGradeAssignments(homework) {
      this.$emit('grade-assignment-selected', homework);
    }
  }
};
</script >

<style scoped >
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
  text-align:center;
}

.details {
  margin-top: 10px;
}
</style >
