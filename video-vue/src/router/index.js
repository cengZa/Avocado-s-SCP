import { createRouter, createWebHistory } from 'vue-router'

import Login from '../views/login/LoginView'
import Register from '../views/register/RegisterView'
import Forget from '../views/login/ForgetPassword'


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
    }
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
