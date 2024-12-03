\<template v-if="currentComponent === 'PostNotification'">
   <div class="notification-container">
     <h2>发布课程通知</h2>
     <form @submit.prevent="submitNotification">
       <div class="form-group">
         <label for="notificationTitle">通知标题</label>
         <input type="text" id="notificationTitle" v-model="notificationTitle" required />
       </div>

       <div class="form-group">
         <label for="notificationContent">通知内容</label>
         <textarea id="notificationContent" v-model="notificationContent" rows="4" required style="resize: none"></textarea>
       </div>

       <div class="form-group">
         <label for="notificationImages">上传图片</label>
         <input type="file" id="notificationImages" @change="handleImageUpload" accept="image/*" multiple />
       </div>

       <div class="form-group">
         <label for="notificationAttachments">上传附件</label>
         <input type="file" id="notificationAttachments" @change="handleAttachmentUpload" multiple />
       </div>

       <button type="submit">发布通知</button>
     </form>
   </div>
 </template>

 <script>
 import axios from 'axios';

 export default {
   name: 'PostNotificationComponent',
   data() {
     return {
       notificationTitle: '',
       notificationContent: '',
       notificationDate: '',
       selectedClass: null,
       imageFiles: [], // Store multiple image files
       attachmentFiles: [], // Store multiple attachment files
     };
   },
   created() {
     this.fetchClassIdFromSession(); // Get classId from session
   },
   methods: {
     // 从sessionStorage获取classId
     fetchClassIdFromSession() {
       const storedCourseInfo = sessionStorage.getItem('selectedCourse');
       const parsedCourseInfo = JSON.parse(storedCourseInfo);
       const classId = parsedCourseInfo ? parsedCourseInfo.classId : null;

       if (classId) {
         this.selectedClass = classId; // Set the selectedClass to the classId
       } else {
         console.error('未找到classId');
       }
     },

     // 处理图片上传
     handleImageUpload(event) {
       this.imageFiles = Array.from(event.target.files); // Convert FileList to array
     },

     // 处理附件上传
     handleAttachmentUpload(event) {
       this.attachmentFiles = Array.from(event.target.files); // Convert FileList to array
     },

     // 提交通知
     async submitNotification() {
       if (!this.selectedClass) {
         alert('班级信息未找到，请重新登录。');
         return;
       }

       try {
         const formData = new FormData();
         formData.append('title', this.notificationTitle);
         formData.append('content', this.notificationContent);
         formData.append('classId', this.selectedClass); // Use classId directly from sessionStorage

         // Append all image files
         this.imageFiles.forEach((file) => {
           formData.append('images', file); // 'images[]' to handle multiple images
         });

         // Append all attachment files
         this.attachmentFiles.forEach((file) => {
           formData.append('files', file); // 'attachments[]' to handle multiple attachments
         });

         await axios.post('api/notifications/create', formData, {
           headers: {
             'Content-Type': 'multipart/form-data'
           }
         });
         alert('通知发布成功');
         // 清空表单数据
         this.notificationTitle = '';
         this.notificationContent = '';
         this.selectedClass = null;
         this.imageFiles = [];
         this.attachmentFiles = [];
         document.getElementById('notificationImages').value = '';
         document.getElementById('notificationAttachments').value = '';
       } catch (error) {
         console.error('Error creating notification:', error);
         alert('通知发布失败');
       }
     }
   }
 };
 </script>

 <style scoped>
 .notification-container {
   padding: 20px;
   background-color: #fff;
   border-radius: 8px;
   max-width: 98%;
 }

 .form-group {
   margin-bottom: 15px;
 }

 label {
   display: block;
   font-weight: bold;
   margin-bottom: 5px;
 }

 input[type="text"],
 textarea,
 input[type="datetime-local"],
 input[type="file"] {
   width: 100%;
   padding: 10px;
   border: 1px solid #ccc;
   border-radius: 4px;
   box-sizing: border-box;
 }

 button[type="submit"] {
   background-color: #007BFF;
   color: #fff;
   border: none;
   padding: 10px 20px;
   border-radius: 4px;
   cursor: pointer;
   transition: background-color 0.3s ease;
   width: 100%;
 }

 button[type="submit"]:hover {
   background-color: #0056b3;
 }
 </style>
