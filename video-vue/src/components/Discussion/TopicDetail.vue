<template>
  <div class="topic-detail" v-if="topic">
    <!-- 主题头部 -->
    <div class="topic-detail-header">
      <button class="close-btn" @click="closeDetail">
        <i class="fas fa-times"></i> 关闭
      </button>
      <h2 class="topic-title">{{ topic.topicName }}</h2>
    </div>

   <!-- 主题信息部分 -->
   <div class="topic-info">
     <div class="author-info">
       <img :src="author.avatar" alt="作者头像" class="author-avatar"/>
       <div class="author-details">
         <p><strong></strong>{{ author.nick_name }}</p>
         <p><strong></strong>{{ formatDate(topic.topicTime) }}</p>
       </div>
     </div>
     <p><strong></strong>{{ topic.description }}</p>

     <!-- 主题的图片 -->
     <div v-if="topic.images && topic.images.length > 0" >
       <h3>图片</h3>
       <div class="image-gallery">
         <div v-for="(image, index) in topic.images" :key="index" class="reply-image">
             <img :src="image" alt="主题图片" class="responsive-image" />
         </div>
       </div>
     </div>

     <!-- 主题的附件 -->
     <div v-if="topic.files && topic.files.length > 0" class="topic-files">
       <h3>附件</h3>
       <div v-for="(file, index) in topic.files" :key="index" class="topic-file">
         <a :href="file.url" download>{{ file.name }}</a>
       </div>
     </div>
   </div>

    <!-- 点赞和收藏按钮 -->
    <div class="topic-detail-footer">
      <button class="like-btn" @click="likeTopic">{{ liked ? '👍' : '👍🏻' }}</button>
      <button class="favorite-btn" @click="favoriteTopic">{{ favorited ? '⭐' : '⭐' }}</button>
    </div>

    <!-- 评论区域 -->
    <div class="comments-section">
      <h3>回复</h3>
      <div v-for="comment in comments" :key="comment.commentId" class="comment">
        <div class="comment-header">
          <img :src="comment.authorAvatar" alt="作者头像" class="comment-avatar"/>
          <div class="comment-author-info">
            <p><strong>{{ comment.authorName }}</strong></p>
            <p class="comment-time">{{ formatDate(comment.commentTime) }}</p>
          </div>
        </div>

        <p class="comment-content">{{ comment.content }}</p>
    <!-- 点赞、收藏和展开按钮 -->
      <div class="comment-detail-footer">
        <button class="like-comment-btn" @click="likeComment(comment)">{{ comment.liked ? '👍' : '👍🏻' }}</button>
        <button class="favorite-comment-btn" @click="favoriteComment(comment)">{{ comment.favorited ? '⭐' : '⭐' }}</button>
         <button class="open-comment-btn" @click="openCommentDialog(comment)">{{ open ? '☁️' : '☁️' }}</button>
      </div>

    <!-- 弹窗：评论详情 -->
         <el-dialog v-model="dialogVisible" title="评论详情">
         <div v-if="selectedComment">
           <h4>{{ selectedComment.authorName }}</h4>
           <p>{{ selectedComment.content }}</p>
           <div v-if="selectedComment.images && selectedComment.images.length > 0">
             <h5>图片:</h5>
             <div v-for="image in selectedComment.images" :key="image" class="image-preview">
               <img :src="image" alt="评论图片" class="responsive-image">
             </div>
           </div>

           <!-- 附件展示 -->
           <div v-if="selectedComment.files && selectedComment.files.length > 0">
             <h5>附件</h5>
                   <div v-for="(file, index) in topic.files" :key="index" class="topic-file">
                     <a :href="file.url" download>{{ file.name }}</a>
                   </div>
           </div>

           <!-- 回复展示 -->
           <div v-if="selectedComment.replies && selectedComment.replies.length > 0">
             <h5>回复:</h5>
             <div v-for="reply in selectedComment.replies" :key="reply.commentId" class="comment reply">
               <div class="comment-header">
                 <img :src="reply.authorAvatar" alt="回复头像" class="comment-avatar" />
                 <div class="comment-author-info">
                   <p><strong>{{ reply.authorName }}</strong></p>
                   <p class="comment-time">{{ formatDate(reply.commentTime) }}</p>
                 </div>
               </div>
               <p class="reply-content">{{ reply.content }}</p>
                    <div v-if="reply.images && reply.images.length > 0">
                            <h5>图片:</h5>
                            <div v-for="image in reply.images" :key="image" class="reply-image">
                                <img :src="image" alt="评论图片" class="responsive-image" />
                            </div>
                          </div>

                          <!-- 附件展示 -->
                          <div v-if="reply.files && reply.files.length > 0">
                            <h5>附件</h5>
                                  <div v-for="(file, index) in reply.files" :key="index" class="reply-file">
                                    <a :href="file.url" download>{{ file.name }}</a>
                                  </div>
                          </div>

             </div>
           </div>

           <!-- 添加回复 -->
           <div>
              <textarea v-model="newReplyContent" placeholder="写下你的回复..."></textarea>

                <el-form-item label="文件上传">
                             <el-upload
                                  ref="fileUpload"
                                  action="/upload"
                                  multiple
                                  :auto-upload="false"
                                  :on-change="handleFileChangeReply"
                                  :before-upload="beforeUploadFile">
                                  <el-button>点击选择文件</el-button>
                                </el-upload>
                              </el-form-item>

                              <!-- 图片上传 -->
                              <el-form-item label="图片上传">
                                <el-upload
                                  ref="imageUpload"
                                  action="/upload"
                                  multiple
                                  :auto-upload="false"
                                  :on-change="handleImageChangeReply"
                                  :before-upload="beforeUploadImage">
                                  <el-button>点击选择图片</el-button>
                                </el-upload>
                          </el-form-item>
                <!-- 提交回复按钮 -->
                <button @click="submitReply">提交回复</button>

           </div>
         </div>
         </el-dialog>
      </div>
    </div>

    <div class="comment-input">
      <!-- 评论内容输入框 -->
      <textarea v-model="newComment.content" placeholder="写下你的评论..." class="comment-textarea"></textarea>

      <!-- 上传文件和图片的按钮 -->
      <div class="attachment-buttons">
        <!-- 文件上传 -->
    <el-form-item label="文件上传">
                 <el-upload
                      ref="fileUpload"
                      action="/upload"
                      multiple
                      :auto-upload="false"
                      :on-change="handleFileChangeComment"
                      :before-upload="beforeUploadFile">
                      <el-button>点击选择文件</el-button>
                    </el-upload>
                  </el-form-item>

                  <!-- 图片上传 -->
                  <el-form-item label="图片上传">
                    <el-upload
                      ref="imageUpload"
                      action="/upload"
                      multiple
                      :auto-upload="false"
                      :on-change="handleImageChangeComment"
                      :before-upload="beforeUploadImage">
                      <el-button>点击选择图片</el-button>
                    </el-upload>
              </el-form-item>
      </div>

      <!-- 提交评论按钮 -->
      <button @click="submitComment" class="submit-btn">提交评论</button>
    </div>

  </div>

  <div v-else>加载中...</div> <!-- 如果 topic 还没有加载，显示加载提示 -->
</template>

<script>
import axios from 'axios';

export default {
  props: {
    topicId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      topic: null,
      author: { nick_name: '', avatar: '' },
      liked: false,
      favorited: false,
      comments: [],
       newComment: {
            topicId: null,
            commenterId: null,
            content: "",
            files: [],  // 附件文件
            images: [],  // 上传的图片
            parentCommentId: null,  // 回复评论的父评论ID
          },
        comments: [
             {
               commentId: 1,
               authorName: 'John',
               authorAvatar:"",
               commentTime: 1634567890,
               content: '这是一个评论。',
               liked: false,
               favorited: false,
               images: [],
               files: [],
               replies: []
             },
             // 更多评论...
           ],
           open: false,  // 是否展开评论
                          dialogVisible: false, // 控制弹窗的显示
           selectedComment:{
           commentId:"",
           content:"",
           commenterId:null,
            authorName: 'John',
            authorAvatar: 'avatar1.jpg',
            commentTime: 1634567890,
            files:[],
            images:[],
            replies:[]
           },
            newReplyContent:"",
           newReply: {files:[],images:[]},  // 存储每个评论的回复
            open: false,  // 是否展开评论
      loading: true,
      error: null,
    };
  },
  mounted() {
    this.fetchTopicData(this.topicId);
  },
  methods: {
    closeDetail() {
      this.$router.go(-1);
    },
    async fetchTopicData(topicId) {
      try {
        const topicResponse = await axios.get(`/api/topics/topic/${topicId}`);
        this.topic = topicResponse.data.data;
        if (this.topic.images) {
          this.topic.images = this.topic.images;
        }
        if (this.topic.files) {
          this.topic.files = this.topic.files.map(file => {
            return {
              name: file.split('/').pop(),
              url: file
            };
          });
        }
        this.fetchAuthorData(this.topic.topicerId);
        this.fetchComments(topicId);
        this.loading = false;
      } catch (error) {
        console.error('Error fetching topic data:', error);
        this.error = '加载失败，请稍后重试';
        this.loading = false;
      }
    },
        async fetchAuthorData(authorId) {
          try {
            const authorResponse = await axios.get(`/api/user/profile/${authorId}`);
            this.author = authorResponse.data.data;
          } catch (error) {
            console.error('Error fetching author data:', error);
            this.error = '加载发布人信息失败';
          }
        },
    async fetchComments(topicId) {
      try {
        const commentsResponse = await axios.get(`/api/comments/topic/${topicId}`);
        this.comments = commentsResponse.data.data;
        for (let comment of this.comments) {
          const authorResponse = await axios.get(`/api/user/profile/${comment.commenterId}`);
          comment.authorName = authorResponse.data.data.nick_name;
          comment.authorAvatar = authorResponse.data.data.avatar;
        }
      } catch (error) {
        console.error('Error fetching comments:', error);
      }
    },
        handleFileChangeComment(file, fileList) {
          this.newComment.files = fileList;  // 将文件列表存储到 data 中
          console.log("选中的文件:", fileList);  // 打印选中的文件
        },

        // 处理图片选择事件
        handleImageChangeComment(file, fileList) {
          this.newComment.images = fileList;  // 将图片列表存储到 data 中
          console.log("选中的图片:", fileList);  // 打印选中的图片
        },
             handleFileChangeReply(file, fileList) {
                  this.newReply.files = fileList;  // 将文件列表存储到 data 中
                  console.log("选中的文件:", fileList);  // 打印选中的文件
                },

                // 处理图片选择事件
                handleImageChangeReply(file, fileList) {
                  this.newReply.images = fileList;  // 将图片列表存储到 data 中
                  console.log("选中的图片:", fileList);  // 打印选中的图片
                },


        beforeUploadFile(file) {
               const validTypes = ['image/png', 'image/jpg', 'image/jpeg', 'text/plain', 'application/pdf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
                const isFileTypeValid = validTypes.includes(file.type);
                const isLimit = file.size / 1024 / 1024 < 2;  // 2MB limit
                console.log("检查有被正常调用");
                if (!isFileTypeValid) {
                  this.$message.error('只能上传 png, jpg, jpeg, txt, pdf, docx 文件!');
                }
                if (!isLimit) {
                  this.$message.error('上传文件大小不能超过 2MB!');
                }

                return isFileTypeValid && isLimit;
            },
            beforeUploadImage(file) {
              const validTypes = ['image/png', 'image/jpg', 'image/jpeg'];
               const isFileTypeValid = validTypes.includes(file.type);
               const isLimit = file.size / 1024 / 1024 < 2;  // 2MB limit

               if (!isFileTypeValid) {
                 this.$message.error('只能上传 png, jpg, jpeg文件!');
               }
               if (!isLimit) {
                 this.$message.error('上传文件大小不能超过 2MB!');
               }

               return isFileTypeValid && isLimit;
            },

        // 提交评论
        async submitComment() {
          if (!this.newComment.content.trim()) {
            alert('评论内容不能为空');
            return;
          }

          try {
            const formData = new FormData();
            const commenterId = JSON.parse(sessionStorage.getItem('userInfo'))?.id;
            formData.append('topicId', this.topicId);
            formData.append('commenterId', commenterId);
            formData.append('content', this.newComment.content);
            console.log("选中的评论",this.newComment.files);

            this.newComment.files.forEach(file => formData.append('files', file.raw));
           this.newComment.images.forEach(image => formData.append('images', image.raw));


            // 使用 axios 发送 POST 请求
            await axios.post("/api/comments/create", formData, {
              headers: {
                'Content-Type': 'multipart/form-data',  // 设置请求头为 multipart/form-data
              },
            });

            console.log("评论提交成功");
            alert("评论提交成功");

            // 提交后清空输入框和文件列表
            this.newComment = {
              content: '',
              files: [],
              images: [],
              parentCommentId: null,
            };
          } catch (error) {
            console.error("创建评论失败", error);
            alert("创建评论失败，请稍后重试");
          }
        },

formatDate(timestamp) {
  const date = new Date(timestamp * 1000);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}/${month}/${day} ${hours}:${minutes}`;
},
    likeTopic() {
      this.liked = !this.liked;
    },
    favoriteTopic() {
      this.favorited = !this.favorited;
    },
     async openCommentDialog(comment) {
          try {
          console.log("id",comment.commentId);
            // 获取评论详情
            const response = await axios.get(`/api/comments/${comment.commentId}`);

            console.log("返回的信息",response);
            console.log("返回的信息",response.data.data.content);
            this.selectedComment.content = response.data.data.content; // 加载评论详情
            this.selectedComment.commentId=comment.commentId; // 加载评论详情
            if (response.data.data.images) {

                      this.selectedComment.images =response.data.data.images ;
                    }
                    if (response.data.data.files) {
                      this.selectedComment.files = response.data.data.files.map(file => {
                        return {
                          name: file.split('/').pop(),
                          url: file
                        };
                      });}

            this.selectedComment.commenterId = response.data.data.commenterId; // 加载评论详情
             const authorResponse = await axios.get(`/api/user/profile/${comment.commenterId}`);
             this.selectedComment.authorName = authorResponse.data.data.nick_name;
             this.selectedComment.replies =response.data.data.replies;
             console.log("获取到的回复数据:", response.data.data.replies);
                     for (let reply of this.selectedComment.replies) {
                       const authorResponse = await axios.get(`/api/user/profile/${reply.commenterId}`);
                       reply.authorName = authorResponse.data.data.nick_name;
                       reply.authorAvatar = authorResponse.data.data.avatar;
                         if (reply.images) {
                             reply.images =reply.images ;
                                           }
                         if (reply.files) {
                     reply.files = reply.files.map(file => {
                                           return {
                          name: file.split('/').pop(),
                            url: file
                                    };
                            });}
                     }
            this.dialogVisible = true; // 显示弹窗
            console.log("显示了弹窗");
          } catch (error) {
            console.error("获取评论详情失败:", error);
          }
        },
        submitReply() {
            const formData = new FormData();
             const commenterId = JSON.parse(sessionStorage.getItem('userInfo'))?.id;
            formData.append('topicId', this.topicId);
            formData.append('commenterId', commenterId);

            // 添加文本内容
            formData.append('content', this.newReplyContent);
            formData.append('topicId', this.newReplyContent);
            console.log("父亲id",this.selectedComment.commentId);
              formData.append('parentCommentId', this.selectedComment.commentId);

            // 添加上传的文件和图片
            this.newReply.files.forEach(file => {
              formData.append('files', file.raw);  // 上传文件
            });

            this.newReply.images.forEach(image => {
              formData.append('images', image.raw);  // 上传图片
            });

            // 发送请求提交数据
            axios.post('/api/comments/create', formData)
              .then(response => {
                console.log('回复成功', response);
                // 清除表单内容
                this.newReplyContent = '';
                this.newReply.files = [];
                this.newReply.images = [];
              })
              .catch(error => {
                console.error('回复失败', error);
              });
          }
}
};
</script>

<style scoped>
.topic-detail-header {
  display: flex;
  justify-content: flex-start; /* 左对齐 */
  margin-bottom: 20px;
}

.topic-title {
  font-size: 1.8rem;
  font-weight: bold;
  text-align: left; /* 确保标题左对齐 */
}

.topic-info {
  margin-bottom: 20px;
  text-align: left; /* 确保内容左对齐 */
  border: 1px solid #1890FF; /* 蓝色边框 */
  border-radius: 10px; /* 圆角边框 */
}

.author-info {
  display: flex;
  margin-bottom: 15px;
  align-items: center;
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
}

.author-details {
  margin-left: 10px;
}

.topic-images, .topic-files {
  margin-top: 20px;
}

.image-gallery {
  text-align: center; /* 居中对齐 */
}

.topic-files {
  margin-top: 10px;
}

.topic-files a {
  display: block;
  margin-bottom: 5px;
  text-align: left;
}

/* 父容器 */
.topic-detail-footer {
  display: flex;
  justify-content: flex-start; /* 左对齐按钮 */
  gap: 10px; /* 可选：为按钮之间添加间距 */
}

/* 按钮样式 */
.like-btn, .favorite-btn,.like-comment-btn,.favorite-comment-btn,.open-comment-btn {
  padding: 5px 10px;
  font-size: 16px;
  cursor: pointer;
  border: none;
  background-color: transparent;
}
.like-btn:hover, .favorite-btn:hover {
  background-color: #f0f0f0; /* 鼠标悬停时的背景色 */
}

.comment-input {
  margin-top: 20px;
}

.comment-textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  height: 80px;
}

.attachment-buttons {
  display: flex;
  justify-content: flex-start; /* 左对齐按钮 */
  gap: 10px; /* 可选：按钮之间的间距 */
  margin-top: 10px; /* 可选：调整与文本区域的距离 */
}

.upload-button {
  cursor: pointer;
  margin-right: 20px;
}

.submit-btn {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #28a745;
  color: #fff;
  border-radius: 5px;
  cursor: pointer;
}

.submit-btn:hover {
  background-color: #218838;
}

.comment {
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #f9f9f9;
  text-align: left; /* 确保评论内容左对齐 */
}

.comment-header {
  display: flex;
  align-items: center;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.comment-author-info {
  margin-left: 10px;
}

.comment-content {
  margin-top: 10px;
  text-align: left; /* 确保评论内容左对齐 */
}

.comment-time {
  font-size: 0.8rem;
  color: #aaa;
}

.replies {
  margin-top: 20px;
  padding-left: 20px;
  border-left: 2px solid #ddd;
}

.reply {
  margin-top: 10px;
  padding: 10px;
  border:1px solid #1890FF;
  border-radius: 5px;
  background-color: #f4f4f4;
  text-align: left; /* 确保回复内容左对齐 */
}

.topic-info {
  margin-top: 10px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #f4f4f4;
  text-align: left; /* 确保回复内容左对齐 */
}
.replies {
  margin-top: 10px;
  padding-left: 20px;
}

.reply-form {
  margin-top: 20px;
}

.reply-form textarea {
  width: 100%;
  height: 60px;
  padding: 10px;
  margin-bottom: 10px;
}

.reply-form button {
  padding: 5px 10px;
}
.reply-image {
  margin-bottom: 15px; /* 每张图片下方的间距 */
  display: flex;
  justify-content: center; /* 居中图片 */
}

/* 设置图片自适应容器的大小，并控制最大尺寸 */
.responsive-image {
  width: 100%; /* 图片宽度占满容器 */
  max-width: 400px; /* 图片的最大宽度 */
  height: auto; /* 高度自适应，保持比例 */
  border-radius: 5px; /* 圆角效果，可选 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* 图片阴影效果 */
  transition: transform 0.3s ease; /* 图片放大效果 */
}

/* 鼠标悬停时图片稍微放大 */
.responsive-image:hover {
  transform: scale(3); /* 悬停时放大效果 */
}
/* 输入框和按钮样式 */
textarea {
  width: 100%;
  height: 80px;
  margin-bottom: 10px;
}

el-upload {
  margin-bottom: 10px;
}

button {
  background-color: #4CAF50;
  color: white;
  padding: 10px 20px;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}

</style>
