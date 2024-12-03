<template>
  <div id="content">
    <h1 class="title">课程简介</h1>
    <div class="info-box">
      <p v-if="error" class="error">{{ error }}</p>
      <p v-else-if="courseDescription" class="course-info">{{ courseDescription }}</p>
      <p v-else class="loading">正在加载课程信息...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';

const courseDescription = ref(null);
const error = ref(null);

// 从 sessionStorage 获取课程描述
const getStoredCourseInfo = () => {
  const storedCourseInfo = sessionStorage.getItem('selectedCourse');
  if (storedCourseInfo) {
    const parsedCourseInfo = JSON.parse(storedCourseInfo);
    return parsedCourseInfo.courseDescription; // 直接获取 courseDescription 字段
  } else {
    console.warn('No course data found in sessionStorage.');
    return null;
  }
};

onMounted(() => {
  const storedCourseDescription = getStoredCourseInfo();
  if (storedCourseDescription) {
    courseDescription.value = storedCourseDescription;
  } else {
    error.value = '未找到有效的课程信息';
  }
});
</script>

<style scoped>
#content {
  width: calc(100vw - 22vw);
  height: calc(100vh - 25vh);
  padding: 20px;
}

.title {
  text-align: left;
}

.info-box {
  margin-top: 20px;
}

.error {
  color: #b00020;
}

.course-info {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  height: calc(100vh - 25vh);
  width: calc(100vw - 22vw);
  padding: 20px;
}

.loading {
  color: #FF9800;
}
</style>
