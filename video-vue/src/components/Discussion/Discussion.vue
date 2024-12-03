<template>
  <div class="discussion-board">
    <!-- 头部 -->
    <div class="header">
      <h2>讨论区</h2>
      <p>平台讨论区使用规则，请仔细阅读。</p>
      <!-- 公告栏 -->
      <el-card class="announcement" shadow="hover">
        <span class="announcement-icon">📣</span>
        <span>{{ announcement.content }}</span>
        <span class="announcement-time">{{ announcement.time }}</span>
      </el-card>
    </div>

    <!-- 创建板块按钮，仅教师可见 -->
    <div class="new-board" v-if="role === 'Teacher'">
      <el-button type="primary" @click="openCreateBoardDialog">创建板块</el-button>
    </div>

    <div class="new-topic">
      <el-button type="success" @click="openCreateTopicDialog">创建主题</el-button>
    </div>

    <!-- 子板块展示区 -->
    <div class="sub-forums">
      <h3>子板块</h3>
      <el-card
        v-for="subForum in subForums"
        :key="subForum.boardId"
        class="sub-forum"
        shadow="hover">
        <span>{{ subForum.boardName }}</span>

        <el-button v-if="role === 'Teacher'"
                   type="danger"
                   size="mini"
                   @click="deleteBoard(subForum.boardId)">删除</el-button>
      </el-card>
    </div>
 <div class="recent-topics" v-if="paginatedTopics.length > 0">
    <h3>全部主题</h3>
    <el-card
      v-for="topic in paginatedTopics"
      :key="topic.topicId"
      class="topic"
      shadow="hover"
        @click="goToTopicDetail(topic.topicId)" >

      <div class="avatar-title-container">
        <div class="avatar-container">
          <img :src="topic.authorAvatar || '/default-avatar.png'" alt="Author Avatar" class="avatar" />
        </div>

        <div class="topic-title-container">
          <!-- 点击标题跳转至主题详情 -->
          <div class="topic-title">
            {{ topic.topicName }}
          </div>
          <div class="topic-author-info">
            <!-- 发布人姓名和发布时间放在同一行 -->
            <p class="topic-author">{{ topic.topicAuthor }}</p>
            <p class="topic-date">{{ formatDate(topic.topicTime) }} 发布</p>
          </div>
        </div>
      </div>

      <!-- 主题描述 -->
      <div class="topic-description">
        <p>{{ topic.description }}</p>
      </div>

      <div class="topic-tags" v-if="topic.tags && topic.tags.length > 0">
             <el-tag
               v-for="(tag, index) in topic.tags.split('/')"
               :key="index"
               class="topic-tag"
               type="info"
             >
               {{ tag }}
             </el-tag>
      </div>
    </el-card>
  </div>



    <!-- 分页按钮 -->
    <div class="pagination">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="topics.length"
        @current-change="goToPage"
        layout="prev, pager, next, jumper">
      </el-pagination>
    </div>

    <!-- 创建板块的对话框 -->
    <el-dialog
      title="创建板块"
      v-model="createBoardDialogVisible"
      width="30%">
      <el-form :model="newBoard">
        <el-form-item label="板块名称">
          <el-input v-model="newBoard.boardName" placeholder="请输入板块名称"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="createBoardDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createBoard">确定</el-button>
      </span>
    </el-dialog>


 <!-- 创建主题的对话框 -->
    <el-dialog
      title="创建主题"
      v-model="createTopicDialogVisible"
      width="50%">
      <el-form :model="newTopic">
        <el-form-item label="选择板块">
          <el-select v-model="newTopic.boardId" placeholder="请选择板块">
            <el-option
              v-for="subForum in subForums"
              :key="subForum.boardId"
              :label="subForum.boardName"
              :value="subForum.boardId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="主题标题">
          <el-input v-model="newTopic.title" placeholder="请输入主题标题"></el-input>
        </el-form-item>
        <el-form-item label="主题内容">
          <el-input type="textarea" v-model="newTopic.description" placeholder="请输入主题内容" rows="4"></el-input>
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="newTopic.tags" placeholder="请输入所带标签，多个标签用/隔开"></el-input>
        </el-form-item>

        <el-form-item label="文件上传">
              <el-upload
                   ref="fileUpload"
                   action="/upload"
                   multiple
                   :auto-upload="false"
                   :on-change="handleFileChange"
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
                   :on-change="handleImageChange"
                   :before-upload="beforeUploadImage">
                   <el-button>点击选择图片</el-button>
                 </el-upload>
           </el-form-item>
         </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="createTopicDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createTopic">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";
import { ElButton, ElCard, ElPagination, ElDivider, ElDialog, ElForm, ElInput } from "element-plus";

export default {
  components: { ElButton, ElCard, ElPagination, ElDivider, ElDialog, ElForm, ElInput },
   data() {
      return {
        announcement: { content: "平台公告内容", time: "2024/11/04 17:07" },
        subForums: [],
        topics: [],
        currentPage: 1,
        pageSize: 10,
        role: "",
        classId: null,
        createBoardDialogVisible: false,
        createTopicDialogVisible: false,
        newBoard: { boardName: "" },
        newTopic: {
          title: "",
          boardId: null,
          topicerId: "", // will set this based on the user
          tags: "",
          description: "",
          files: [],  // for files
          images: [],  // for images
          authorAvatar:"",
          topicAuthor:""
        },
            author: { nick_name: '', avatar: '' }, // 发布人信息
      };
    },
  computed: {
    paginatedTopics() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = this.currentPage * this.pageSize;
      return this.topics.slice(start, end);
    }
  },
  mounted() {
    this.loadUserInfo();
    if (this.classId) {
      this.getBoardByClassId();
      this.getTopics();
      this.getAnnouncement();
    } else {
      console.error("班级ID未找到");
    }
  },
  methods: {
    openCreateBoardDialog() {
      this.createBoardDialogVisible = true;
    },
    openCreateTopicDialog() {
      this.createTopicDialogVisible = true;
    },
    loadUserInfo() {
      const userInfo = JSON.parse(sessionStorage.getItem("userInfo"));
      this.newTopic.topicerId = JSON.parse(sessionStorage.getItem('userInfo'))?.id;
      console.log("发布人id1",this.topicerId);
      this.role = userInfo?.role || "";
      const courseInfo = JSON.parse(sessionStorage.getItem("selectedCourse"));
      this.classId = courseInfo?.classId || null;
    },
    getBoardByClassId() {
      axios.get(`/api/board/class/${this.classId}`).then((res) => {
        this.subForums = res.data.data;
      });
    },
     async getTopics() {
        try {
           const response = await axios.get(`/api/topics/getAllTopics/${this.classId}`);
           if (Array.isArray(response.data.data)) {
             this.topics = await Promise.all(response.data.data.map(async (topic) => {
               const author = await this.fetchAuthorData(topic.topicerId);
               console.log("author",author);
               return {
               topicId:topic.topicId,
                 topicName: topic.topicName,
                 topicTime: topic.topicTime,
                 description: topic.description,
                 topicerId: topic.topicerId,
                 tags:topic.tags,
                  topicAuthor: author.nick_name,
                 authorAvatar: author.avatar,
               };
             }));
           } else {
             console.error("Invalid topics data format:", response.data);
             this.topics = [];
           }
        } catch (error) {
           console.error("Failed to fetch topics:", error);
        }
     },

    goToPage(page) {
      this.currentPage = page;
    },
    goToTopicDetail(topicId) {
      this.$router.push({ name: "topic-detail", params: { topicId } });
    },
  createBoard() {
    // 确保板块名称存在
    if (!this.newBoard.boardName) {
      return alert("请输入板块名称");
    }

    // 创建 FormData 对象
    const formData = new FormData();

    // 添加表单字段
    formData.append('classId', this.classId);
    formData.append('boardName', this.newBoard.boardName);


    // 打印调试信息
    console.log("板块名称", this.newBoard.boardName);
    console.log("课堂id", this.classId);

    // 使用 axios 发送 POST 请求
    axios.post("/api/board/create", formData, {
      headers: {
        'Content-Type': 'multipart/form-data'  // 设置请求头为 multipart/form-data
      }
    })
    .then(response => {
      // 请求成功后的操作
      this.createBoardDialogVisible = false; // 关闭对话框
      this.getBoardByClassId();  // 获取板块数据
    })
    .catch(error => {
      // 请求失败的处理
      console.error("创建板块失败", error);
      alert("创建板块失败，请稍后重试");
    });
  },
    getAnnouncement() {
      axios.get('/api/announcement')
        .then(response => {
          this.announcement = response.data;
        })
        .catch(error => {
          console.error('获取公告失败:', error);
        });
    },
     // 删除板块
      deleteBoard(boardId) {
        if (!this.classId) {
          alert('班级ID不存在');
          return;
        }
      axios.delete(`/api/board/${boardId}`)
          .then(response => {
            alert('删除板块成功');
            this.getBoardByClassId(this.classId);
          })
          .catch(error => {
            console.error('删除板块失败:', error);
          });
      },
    createTopic() {
       const formData = new FormData();

       formData.append("boardId", this.newTopic.boardId);
       formData.append("topicerId", this.newTopic.topicerId);
       formData.append("topicName", this.newTopic.title);
       formData.append("tags", this.newTopic.tags);
       formData.append("description", this.newTopic.description);
       console.log("发布人id", this.newTopic.topicerId);

       this.newTopic.files.forEach(file => formData.append('files', file.raw));
             this.newTopic.images.forEach(image => formData.append('images', image.raw));
             console.log(formData);

       axios.post('/api/topics/create', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
         .then((response) => {
           this.createTopicDialogVisible = false;
           this.getTopics();
         })
         .catch((error) => {
           console.error(error);
         });
     },
    // 文件选择事件
       handleFileChange(file, fileList) {
         this.newTopic.files = fileList; // 将文件列表存入 data
       },
       handleImageChange(file, fileList) {
         this.newTopic.images = fileList; // 将图片列表存入 data
       },
     beforeUploadFile(file) {
        const validTypes = ['image/png', 'image/jpg', 'image/jpeg', 'text/plain', 'application/pdf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
         const isFileTypeValid = validTypes.includes(file.type);
         const isLimit = file.size / 1024 / 1024 < 2;  // 2MB limit

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

         formatDate(timestamp) {
           const date = new Date(timestamp * 1000);
           const year = date.getFullYear();
           const month = String(date.getMonth() + 1).padStart(2, '0');
           const day = String(date.getDate()).padStart(2, '0');
           const hours = String(date.getHours()).padStart(2, '0');
           const minutes = String(date.getMinutes()).padStart(2, '0');
           return `${year}/${month}/${day} ${hours}:${minutes}`;
         },
        async fetchAuthorData(authorId) {
          try {
            // 获取发布人的详细信息
            const authorResponse = await axios.get(`/api/user/profile/${authorId}`);
            this.author = authorResponse.data.data; // 假设返回的数据结构包含 name 和 avatar 字段
            console.log("发布人信息",this.author);
            return this.author;
          } catch (error) {
            console.error('Error fetching author data:', error);
            this.error = '加载发布人信息失败';
          }
        }
  }
};
</script>


<style scoped>
.recent-topics {
  padding: 20px;
}

.topic {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
  border: 1px solid #1890FF; /* 蓝色边框 */
  border-radius: 10px; /* 圆角边框 */
  padding: 10px;
  background-color: #fff; /* 背景颜色 */
}

.avatar-title-container {
  display: flex;
  align-items: center;  /* 确保头像和标题在同一行且垂直居中 */
  margin-bottom: 8px;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start; /* 左对齐  */
  margin-right: 10px; /* 控制头像和标题之间的间距 */
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
}

.topic-title-container {
  display: flex;
  flex-direction: column;
  justify-content: flex-start; /* 保证标题和时间在顶部 */
  flex-grow: 1; /* 占据剩余空间 */
}

.topic-title {
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  text-align: left; /* 让发布时间与姓名对齐 */
  margin-bottom: 5px;
}

.topic-author-info {
  display: flex;
  align-items: center; /* 姓名和发布时间在同一行 */
}

.topic-author {
  font-size: 14px;
  color: #555;
  margin-right: 10px; /* 控制姓名和发布时间之间的间距 */
}

.topic-date {
  font-size: 14px;
  color: #888;
  text-align: left; /* 让发布时间与姓名对齐 */
}

.topic-description {
  font-size: 16px;
  color: #333;
  margin-top: 5px;
  line-height: 1.5;
  text-align: left; /* 左对齐 */
}

.topic-tags {
  margin-top: 10px; /* 标签区域顶部的间距 */
}

.topic-tag {
  margin-right: 5px; /* 控制标签之间的间距 */
}
.sub-forum {
  display: flex;
  flex-direction: row; /* 水平布局子元素 */
  justify-content: space-between; /* 两端对齐 */
  align-items: center; /* 垂直居中 */
  margin-bottom: 10px; /* 子板块间隔 */
  padding: 10px;
  border-radius: 10px;
}

.sub-forum-content {
  display: flex;
  justify-content: flex-start; /* 确保内容左对齐 */
  align-items: center; /* 确保内容垂直居中 */
  width: 100%; /* 确保内容占满整个容器宽度 */
}

.sub-forum-name {
  font-size: 16px;
  font-weight: bold;
  margin-right: 10px; /* 子板块名称和按钮之间的间距 */
}

.delete-btn {
  margin-left: 10px; /* 删除按钮与名称之间的间距 */
}
.new-board{
  margin-bottom: 15px; /* 给按钮添加底部间距 */
  display: flex;
  justify-content: flex-start; /* 确保创建板块按钮左对齐 */
}
.new-topic{
  margin-bottom: 15px; /* 给按钮添加底部间距 */
  display: flex;
  justify-content: flex-start; /* 确保创建板块按钮左对齐 */
}
.el-button {
  margin-left: 10px; /* 确保按钮与内容间距适当 */
}
</style>
