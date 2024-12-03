<template>
  <div class="assignments">
    <h2>作业管理</h2>
    <table>
      <thead>
      <tr>
        <th>作业标题</th>
        <th>作业开始</th>
        <th>作业截止</th>
        <th>提交人数</th>
        <th>提交时间</th>
        <th>得分</th>
        <th>批改状态</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(assignment, index) in assignments" :key="index">
        <td @click="viewAssignment(assignment.id)" class="assignment-title">{{ assignment.title }}</td>
        <td>{{ assignment.startDate }}</td>
        <td>{{ assignment.dueDate }}</td>
        <td>{{ assignment.submittedCount }}</td>
        <td>{{ assignment.submissionTime || '未提交' }}</td>
        <td>{{ assignment.score || '未评分' }}</td>
        <td>{{ assignment.gradingStatus }}</td>

        <td><button @click.stop="openModal(assignment)">提交作业</button></td>

      </tr>

      </tbody></table>

    <AssignmentModal
        :isOpen="isModalOpen"
        :assignment="currentAssignment"
        @close="closeModal"
        @submit="handleSubmission"
    />

  </div>
</template>

<script>
import AssignmentModal from './AssignmentModal.vue';

const mockAssignments = [
  {
    id: 1,
    title: "第一周作业",
    startDate: "2024-10-01",
    dueDate: "2024-10-07",
    submittedCount: 20,
    submissionTime: null,
    score: null,
    gradingStatus: "未批改"
  },
  {
    id: 2,
    title: "第二周作业",
    startDate: "2024-10-08",
    dueDate: "2024-10-14",
    submittedCount: 15,
    submissionTime: null,
    score:null,
    gradingStatus:"未批改"
  },
  {
    id: 3,
    title: "第三周作业",
    startDate: "2024-10-08",
    dueDate: "2023-10-14",
    submittedCount: 15,
    submissionTime: null,
    score:null,
    gradingStatus:"未批改"
  },
];

export default {
  name:'CourseAssignments',

  components:{
    AssignmentModal
  },

  data() {
    return{
      assignments : mockAssignments,
      isModalOpen:false,
      currentAssignment:{},
    };
  },

  methods:{
    viewAssignment(id) {
// 跳转到作业介绍vue
      this.$router.push({ name:'AssignmentDetail', params:{ id } });
    },

    openModal(assignment) {
      console.log("打开模态框:", assignment);
      this.currentAssignment = assignment;
      this.isModalOpen = true;
    },

    closeModal() {
      this.isModalOpen = false;
    },

    handleSubmission({ text, file, assignmentId }) {
// 提交
      //需要if（time<duetime）
      //if(Date().toLocaleString())

      const formData = new FormData();
      formData.append('userId', this.userId);
      formData.append('assignmentId', assignmentId);
      formData.append('text', text);
      if (file) {
        formData.append('file', file);
      }
      const assignment = this.assignments.find(a => a.id === assignmentId);
      //// 数据发送到后端
      if (assignment) {
        assignment.submissionTime = new Date().toLocaleString();
//这里逻辑还没写

        //alert(`您已成功提交 ${assignment.title} 作业`);
        console.log("用户输入:", text);
      }
      this.closeModal();
    }
  }
}
</script>

<style scoped lang="scss">
.assignments {
  margin-top: 20px;
  padding: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead th, tbody td {
  border-bottom: 1px solid #ccc;
  padding: 8px;
  text-align: center;
}

.assignment-title {
  cursor: pointer;
  color: #007BFF;
  text-decoration: none;
}

.assignment-title:hover {
  font-weight: bold;
}

button {
  padding: .5em .75em;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: .25em;
  cursor: pointer;
  transition: .3s;

  &:hover {
    background-color: #218838;
  }
}

tbody tr:hover {
  background-color: #f0f0f0;
}
</style>

