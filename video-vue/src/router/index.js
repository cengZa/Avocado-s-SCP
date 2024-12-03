import { createRouter, createWebHistory } from 'vue-router'

import Login from '../views/login/LoginView'
import Register from '../views/register/RegisterView'
import Forget from '../views/login/ForgetPassword'
import UploadResource from '../views/resource/UploadResource'
import UserProfile from '../views/UserProfile.vue';
import Menu from '../views/Menu'
import CourseDetail from '../views/course/CourseDetail'
import GradeAssignments from '../views/assignment/GradeAssignments.vue';
import GradingPage from '../views/assignment/GradingPage.vue';
import MyClass from '../views/MyClass.vue';
import TopicDetail from '@/components/Discussion/TopicDetail.vue'; // 主题详情页
import NotificationDetail from '@/components/Notification/NotificationDetail.vue';
import SafeExit from '@/views/system/SafeExit.vue';
import MyNotification from '@/views/notification/MyNotification.vue';

const routes = [

  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
   {
      path: '/forget',
      name: 'Forget',
      component: Forget
    },
    {
    path:'/upload-resource',
    name:'UploadResource',
    component:UploadResource
    },
    {path:'/menu',
    name:'Menu',
    component:Menu
    },
    {path:'/safe-exit',
        name:'SafeExit',
        component:SafeExit
        },
       {path:'/notifications',
        name:'MyNotification',
        component:MyNotification
        },
        {
    path:'/course-detail',
    name:'CourseDetail',
    component:CourseDetail},
     {
   path: '/my-information',
   name:'UserProfile',
   component: UserProfile },
    { path: '/GradeAssignments', name: 'GradeAssignments',component: GradeAssignments },
    { path: '/grading', name: 'GradingPage', component: GradingPage},
    { path: '/my-class', name: 'MyClass',component:MyClass },
 {
      // 定义动态路由，通过 :topicId 获取主题的 ID
      path: '/topic/:topicId',
      name: 'topic-detail',
      component: TopicDetail,
      // 将 topicId 作为 props 传递给 TopicDetail 组件
      props: true
    },
      {
        path: '/notification/:notificationId',
        name: 'notificationDetail',
        component: NotificationDetail,
        props: true
      },

]
/* eslint-disable */
const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router




//// 导航守卫，前置处理
//router.beforeEach((to, from, next) => {
//    let isAuthenticated = !!sessionStorage.getItem('userInfo')
//    // 如果路由要跳转到除了登录和注册的界面的话就判断是否已经登录，如果没有登录就强制跳到登录界面
//    if (to.path !== '/login' && to.path !== '/register' && !isAuthenticated) {
//        next({ path: '/login' })
//    } else {
//        next()
//    }
//})
