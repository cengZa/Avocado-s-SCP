
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/theme-chalk/index.css'
import axios from 'axios'
import VueAxios from 'vue-axios'
import 'video.js/dist/video-js.css';
// main.js
import VueDPlayer from 'vue-dplayer'
axios.defaults.withCredentials = true;


const debounce = (fn, delay) => {
  let timer
   return (...args) => {
     if (timer) {
       clearTimeout(timer)
     }
     timer = setTimeout(() => {
       fn(...args)
     }, delay)
   }
}

const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver{
   constructor(callback) {
     callback = debounce(callback, 200);
     super(callback);
   }
}
const app = createApp(App)
app.use(ElementPlus)
app.use(VueAxios, axios)
app.use(VueDPlayer);
app.use(router).mount('#app')
