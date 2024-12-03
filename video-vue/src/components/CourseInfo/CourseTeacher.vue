<template>
  <div class="course-teacher">
    <div class="profile-section" v-if="teacherData">
      <div class="info-container">
        <div class="teacher-details">
          <p><strong>姓名:</strong> {{ teacherData.name }}</p>
          <p><strong>工号:</strong> {{ teacherData.ID }}</p>
          <p><strong>职称:</strong> {{ teacherData.title }}</p>
          <p><strong>电话:</strong> {{ teacherData.phone }}</p>
          <p><strong>邮箱:</strong> {{ teacherData.email }}</p>
        </div>

        <img :src="teacherData.avatar" alt="用户头像显示失败" class="teacher-avatar">
      </div>

      <!-- 简介部分 -->
      <div class="teacher-info">
        <h3>简介:</h3>
        <p>{{ teacherData.info }}</p>
      </div>
    </div>

    <!-- 加载中的状态 -->
    <div v-else class="loading">加载中...</div>
  </div>
</template>

<script>
import axios from 'axios';  // Import axios

export default {
  data() {
    return {
      defaultTeacher: {
        avatar: "http://localhost:8080/upload/23fed9e5-3b77-4c04-b7c3-d762baf393b8.jpg",
        phone: '未提供',
        name: '未知教师',
        ID: '未提供',
        email: '未提供',
        title: '未提供',
        info: '这是老师的简介，内容可以根据实际情况进行调整。',
      },
      teacherData: null,
      classId: null,  // Store the classId here
    };
  },
  mounted() {
    this.fetchClassIdAndTeacherData();
  },
  methods: {
    // Fetch classId from sessionStorage and then fetch teacher data
    async fetchClassIdAndTeacherData() {
      try {
        // 从 sessionStorage 获取 classId
        const storedCourseInfo = sessionStorage.getItem('selectedCourse');
        const parsedCourseInfo = storedCourseInfo ? JSON.parse(storedCourseInfo) : null;
        this.classId = parsedCourseInfo ? parsedCourseInfo.classId : null;

        if (!this.classId) {
          console.error('classId 未找到');
          return;
        }

        console.log("班级id", this.classId);

        // Proceed to fetch teacher data if classId is found
        await this.fetchTeacherData();
      } catch (error) {
        console.error('获取 classId 时发生错误:', error);
      }
    },
    // Fetch teacher data from the API using axios
    async fetchTeacherData() {
      try {
        // Use classId to get teacher information (assuming the API uses classId to fetch teacher data)
        const response = await axios.get(`/api/classes/teacher/${this.classId}`);

        // Check if response is successful
        if (response.status !== 200) {
          throw new Error('获取教师数据失败');
        }

        const teacherData = response.data.data;
        console.log("老师信息",teacherData);

        this.teacherData = {
          avatar: teacherData.avatar || this.defaultTeacher.avatar,
          phone: teacherData.phone || this.defaultTeacher.phone,
          name: teacherData.nick_name || this.defaultTeacher.nick_name,
          ID: teacherData.username || this.defaultTeacher.username,
          email: teacherData.email || this.defaultTeacher.email,
          title: teacherData.title || this.defaultTeacher.title,
          info: teacherData.info || this.defaultTeacher.info,
        };
      } catch (error) {
        console.error('获取教师数据时发生错误:', error);

        // Fallback to default teacher data if there's an error
        this.teacherData = { ...this.defaultTeacher };
      }
    }
  }
};
</script>

<style scoped>
.course-teacher {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.profile-section {
  display: flex;
  flex-direction: column;
}

.info-container {
  display: flex;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  width: calc((100vw - 18vw)*0.6);
  height:calc((100vh - 10vh)*0.5);
}

.teacher-info{
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.teacher-details, .teacher-info {
  border-radius :8px;
  border :1px solid #ccc;
  background-color:#f9f9f9;
  padding :15px;
  width : calc((100vw - 18vw)*0.6);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.right-column {
  margin-left :20px;
}

.teacher-avatar {
  width: calc((100vw - 18vw)*0.2);
  height:calc((100vh - 10vh)*0.4);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.teacher-info h3{
  margin-top :20px;
  width: calc(100vw - 18vw);
  height:calc((100vh - 10vh)*0.3);
}

.loading {
  font-size: larger;
  color: #888888;
}
</style>
