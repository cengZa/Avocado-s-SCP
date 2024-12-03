<template>
  <div class="course" @click="navigateToCourse">
    <!-- 使用默认图片，如果没有图片，则使用默认图片 -->
    <img :src="defaultImage" alt="课程图片" class="course-image" />
    <h3>{{ course.name }}</h3>
    <p>课程名: {{ course.courseName }}</p>
    <p>课程号: {{ course.courseCode }}</p>
    <p>课序号: {{ course.classSequence }}</p>
  </div>
</template>

<script>
export default {
  props: {
    course: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      // 定义默认的课程图片路径
      defaultImage: require('@/assets/course.jpg'), // 默认图片的路径
    };
  },
  methods: {
    navigateToCourse() {
      console.log("Navigating to course:", this.course);

      // 保存所选课程的信息
      const courseWithTeacher = { ...this.course, teacherName: this.course.teacherName };
      console.log("课程信息", courseWithTeacher);

      sessionStorage.setItem("selectedCourse", JSON.stringify(courseWithTeacher));
      this.$router.push({ name: "CourseDetail" });
    },
  },
};
</script>

<style scoped>
.course {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  max-width: 250px; /* 限制整个课程块的宽度 */
  margin: 10px;  /* 给每个课程一个间距 */
  padding: 10px; /* 给课程块增加内边距 */
  border: 1px solid #ddd; /* 可选：边框 */
  border-radius: 8px; /* 可选：圆角 */
  overflow: hidden;  /* 防止超出的内容 */
}

.course-image {
  width: 100%; /* 让图片宽度适应父容器的宽度 */
  height: 80px; /* 设置固定高度 */
  object-fit: cover; /* 保持图片比例，裁剪并填满容器 */
  border-radius: 4px; /* 可选：圆角 */
  margin-bottom: 10px; /* 图片与文字之间的间距 */
}

h3, p {
  margin: 5px 0; /* 给标题和描述增加一些间距 */
}

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
