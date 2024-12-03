<template>
  <div class="dashboard">
    <!-- 顶部课程信息栏 -->
    <div class="header">
      <p class="course-info">
        课程名：{{ selectedCourseInfo.courseName }} |
        课程号：{{ selectedCourseInfo.courseCode }} |
        课序号：{{ selectedCourseInfo.classSequence }} |
        主讲教师：{{ selectedCourseInfo.teacherName }}
      </p>
    </div>

    <!-- 主体内容区域 -->
    <div class="main-content">
      <!-- 左侧导航栏 -->
      <div class="sidebar">
        <ul v-for="(item, index) in filteredMenuItems" :key="index">
          <li @click="toggleSubMenu(index)">
            {{ item.title }}
            <span v-if="item.submenu">{{ item.isOpen ? 'v' : '>' }}</span>
          </li>

          <!-- 二级导航 -->
          <ul v-if="item.isOpen">
            <li
              v-for="(subItem, subIndex) in item.submenu"
              :key="subIndex"
              @click.stop="selectComponent(subItem.component)"
            >
              {{ subItem.title }}
            </li>
          </ul>
        </ul>
      </div>

      <!-- 右侧内容区 -->
      <div class="content">
        <!-- 动态显示中间组件 -->
        <component
          :is="currentComponent"
          @grade-assignment-selected="handleGradeAssignments"
          :homework="selectedHomework"
          :course-code="selectedCourseInfo.courseCode"
          :class-sequence="selectedCourseInfo.classSequence"
        ></component>
      </div>
    </div>
  </div>
</template>

<script>
import CourseInfo from '../../components/CourseInfo/CourseInfo.vue';
import CourseStudent from '../../components/CourseInfo/CourseStudent.vue';
import CourseTeacher from '../../components/CourseInfo/CourseTeacher.vue';

import EleCourseware from '../../components/EleCourseware.vue';
import CourseAssignments from '../../components/CourseAssignment/CourseAssignments.vue';
import AssignHomework from '../../components/CourseAssignment/AssignHomework.vue';
import GradeHomework from '../../components/CourseAssignment/GradeHomework.vue';
import GradeAssignments from '../../components/CourseAssignment/GradeAssignments.vue';
import Discussion from '../../components/Discussion/Discussion.vue';
import CourseNoti from '../../components/Notification/CourseNoti.vue';
import CourseNotiRead from '../../components/Notification/CourseNotiRead.vue';
import CourseNotiUnread from '../../components/Notification/CourseNotiUnread.vue';
import PostNotification from '@/components/Notification/PostNotification.vue';
import ViewNotifications from '@/components/Notification/ViewNotifications.vue';





export default {
  name: 'App',
  components: {
    CourseInfo,
    CourseTeacher,
    CourseStudent,
    CourseNoti,
    CourseNotiRead,
    CourseNotiUnread,
    PostNotification,
    ViewNotifications,
    EleCourseware,
    CourseAssignments,
    AssignHomework,

    GradeHomework,
    GradeAssignments,
    Discussion
  },

  data() {
    return {
      menuItems: [
        {
          title: '课程信息',
          submenu: [
            { title: '课程介绍', component: 'CourseInfo' },
            { title: '学生信息', component: 'CourseStudent' },
            { title: '老师信息', component: 'CourseTeacher' },
          ],
          isOpen: false,
        },
        {
       title: "课程通知",
             submenu: [
                  {title: "所有通知", component: "CourseNoti"},
                    {title: "已读通知", component: "CourseNotiRead"},
                   {title: "未读通知", component: "CourseNotiUnread"},
                  {title: "发布通知", component: "PostNotification"},
                    {title: "查看通知", component: "ViewNotifications"},
                     ],
                      isOpen: false,
            },
        {
          title: '课程资源',
          submenu: [{ title: '电子课件', component: 'EleCourseware' }],
          isOpen: false,
        },
        {
          title: '课程作业',
          submenu: [
            { title: '查看作业', component: 'CourseAssignments' }, // 学生可以查看
            { title: '发布作业', component: 'AssignHomework' }, // 老师可以发布
            { title: '批改作业', component: 'GradeHomework' }, // 老师可以批改
          ],
          isOpen: false,
        },

        {
          title: '答疑讨论',
          submenu: [{ title: '答疑讨论', component: 'Discussion' }],
          isOpen: false,
        },
      ],
      currentComponent: null,
      selectedHomework: null,
      selectedCourseInfo: null,
      role: null, // 角色信息
    };
  },

  created() {
   const userInfo = JSON.parse(sessionStorage.getItem('userInfo')); // 获取用户信息
   console.log("用户信息",userInfo);
    // 从 sessionStorage 获取角色信息和课程信息
    this.role =userInfo.role;// 假设角色信息保存在 sessionStorage 中
    console.log("角色",this.role);
    const storedCourseInfo = sessionStorage.getItem('selectedCourse');
    if (storedCourseInfo) {
      this.selectedCourseInfo = JSON.parse(storedCourseInfo);
    } else {
      console.warn('No course data found in sessionStorage.');
    }
  },

computed: {
// 过滤菜单项，根据用户角色决定显示哪些功能
    filteredMenuItems() {
      return this.menuItems.map(item => {
     if (item.title === '课程通知') {
       item.submenu = item.submenu.filter(subItem => {
         if (this.role === 'Teacher') {
           // Teacher can only see "查看通知" and "发布通知"
           if (subItem.title === '查看通知' || subItem.title === '发布通知') {
             return true;  // Show these items for Teacher
           } else {
             return false;  // Hide other items for Teacher
           }
         }
         if (this.role === 'Student') {
           // Student can only see "所有通知", "已读通知", "未读通知"
           if (subItem.title === '所有通知' || subItem.title === '已读通知' || subItem.title === '未读通知') {
             return true;  // Show these items for Student
           } else {
             return false;  // Hide other items for Student
           }
         }
         return false;  // Default: hide for other roles (if any)
       });
     }
     else if (item.title === '课程作业') {
          // 如果是老师，允许发布作业和批改作业，学生只能查看作业
          item.submenu = item.submenu.filter(subItem => {
            if (this.role === 'Teacher') {
              return true;  // 老师可以发布作业和批改作业
            }
            if (this.role === 'Student' && subItem.title === '查看作业') {
              return true;  // 学生只能查看作业
            }
            return false;
          });
        }
        return item;
      });
    },
  },

  methods: {
    toggleSubMenu(index) {
      this.menuItems[index].isOpen = !this.menuItems[index].isOpen;
    },

    selectComponent(component) {
      this.currentComponent = component;
    },

    handleGradeAssignments(homework) {
      this.currentComponent = 'GradeAssignments';
      this.selectedHomework = homework;
    },
      handleDiscussion(post) {
          this.currentComponent = 'Post';
          this.selectedPost = post;
        }
  },

};
</script>

<style scoped>
body {
  margin: 0;
  padding: 0;
  height: 100vh;
  width: 100vw;
}

.dashboard {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
}

.header {
  padding: 10px 20px;
  background-color: #00509e;
  color: white;
  text-align: left;
  display: flex;
  align-items: center;
}

.course-info {
  font-size: 1em;
  font-weight: bold;
}

.main-content {
  display: flex;
  flex-grow: 1;
}

.sidebar {
  border-right: 1px solid #ccc;
  width: 18vw;
  background-color: #f4f4f4;
  height: 100%;
  padding-top: 20px;
}

.sidebar ul {
  list-style-type: none;
  padding-left: 0;
}

.sidebar li {
  cursor: pointer;
  margin-bottom: .5em;
  padding: .5em;
  transition: color .3s ease, background-color .3s ease;
}

.sidebar li:hover {
  color: blue;
}

.sidebar ul ul {
  padding-left: 1em;
}

.content {
  flex-grow: 1;
  background-color: #fff;
  padding: 20px;
  overflow-y: auto;
}
</style>
