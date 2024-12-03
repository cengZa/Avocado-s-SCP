<template>
  <div class="dashboard">
    <!-- 上 -->
    <div class="sidebar-top">
      <router-link to="/my-information" class="myinformation">个人中心</router-link>
      <span class="spacer"></span>
      <router-link to="/safe-exit" class="safeexit">安全退出</router-link>
      <span class="spacer"></span>
      <router-link to="/operating-instructions" class="operatinginstructions">使用说明</router-link>
    </div>

    <!-- 左 -->
    <div class="sidebar-left">
      <ProfileSidebar/>
      <div class="left-button">
        <router-link to="/my-class" class="myclass">我的课表</router-link>
        <div class="spacer"></div>
        <router-link to="/notifications" class="notification">通知</router-link>
        <div class="spacer"></div>
        <router-link to="/my-homework" class="myhomework">我的作业</router-link>
      </div>
    </div>

    <!-- 中 -->
    <div class="content">
      <Courses/>
    </div>

    <!-- 右 -->
    <div class="sidebar-right">
      <Calendar/>
    </div>

  </div>

</template>

<script>
import ProfileSidebar from '../components/ProfileSidebar.vue';
import Courses from '../components/Courses.vue';
import Calendar from '../components/Calendar.vue';

export default {
  components: {
    ProfileSidebar,
    Courses,
    Calendar,
  }
};
</script>

<style scoped>
body {
  margin: 0;
  padding: 0;
  height: 100vh;
  width:100vw;
}

.dashboard {
  display: flex;
  flex-direction: row;
  flex-wrap:wrap;
  height: 100vh;
  width:100vw;
}

.sidebar-top {
  background-color:#00509e;
  color: white;
  padding: 5vh;
  height: 10vh;
  width:100vw;
  display: flex;
  justify-content: flex-end;
}

.sidebar-left {
  flex-basis: 25%;
  background-color: #f9f9f9;
  height: 90vh;
  width:20vw;
  display: flex;
  flex-direction: column;
}
.left-button{
  display: flex;
  height: 50vh;
  width:20vw;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.content {
  flex-grow:1;
  background-color: #fff;
  height: 90vh;
  width:55vw;

}

.sidebar-right {
  flex-basis:20%;
  background-color:#f9f9f9;
  height: 90vh;
  width:25vw;
}


</style>
