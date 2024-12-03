<template>
  <div class="post-container">
    <h1>{{ postData.title }}</h1>
    <p class="author-info">发帖人: {{ user.name }} <img :src="user.avatar" alt="头像" /></p>
    <p class="content">{{ postData.content }}</p>
    <p class="stats">点赞数: {{ postData.likes }} | 收藏数: {{ postData.favoriate }}</p>
    <p class="timestamp">发帖时间: {{ formatTimestamp(postData.timestamp) }}</p>

    <hr />

    <h3>评论</h3>
    <ul class="comments-list">
      <li v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-author-info">
          <img :src="comment.user.avatar" alt="头像" />
          {{ comment.user.name }}
        </div>

        <!-- 父级评论信息 -->
        <div v-if="comment.fatherid !== null">
          {{ comment.user.name }} 回复 {{ comment.parentUser?.name || '未知用户' }}: "{{ comment.parentComment?.content || '已删除'}}"
        </div>

        <!-- 评论内容及点赞数 -->
        <div>{{ comment.content }}</div>
        <p class="stats">点赞数: {{ comment.likes }}</p>
        <p class="comment-timestamp">{{ formatTimestamp(comment.timestamp) }}</p>
      </li>
    </ul>

    <!-- 添加评论区域 -->
    <div class="add-comment-section">
      <textarea v-model="newCommentContent" placeholder="写下你的评论..." rows="4"></textarea>
      <button @click="submitComment">发表</button>
    </div>

    <!-- 创建新主题的区域 -->
    <div class="create-topic">
      <h3>发布新主题</h3>
      <input v-model="newTopic.title" placeholder="输入主题标题" />
      <textarea v-model="newTopic.content" placeholder="输入主题内容" rows="4"></textarea>
      <input type="file" @change="handleFileUpload" multiple />
      <button @click="submitNewTopic">提交新主题</button>
    </div>

    <!-- 主题列表 -->
    <h3>主题列表</h3>
    <div v-for="topic in topics" :key="topic.topicId" class="topic-item">
      <h4>{{ topic.title }}</h4>
      <p>{{ topic.content }}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    post: {
      type: Object,
      required: true
    },
    boardId: {
      type: Number,
      required: true
    }
  },

  data() {
    return {
      user: {},
      comments: [],
      postData: {},
      newCommentContent: '',
      newTopic: {
        title: '',
        content: ''
      },
      files: [],
      topics: []
    };
  },

  created() {
    this.fetchPost();
    this.fetchComments();
    this.fetchTopics(); // 获取主题列表
  },

  methods: {
    // 获取帖子详情
    //TODO:后端中获取帖子详情和评论列表的api是什么
    async fetchPost() {
      const response = await axios.get(`http://localhost:8080/board?boardId=${this.post.id}`);//TODO:可能需要修改
      this.postData = response.data;

      const authorResponse = await axios.get(`http://localhost:8080/user?userId=${this.postData.authorid}`);//TODO:可能需要修改
      this.user = authorResponse.data;
    },

    // 获取评论列表
    async fetchComments() {
      const commentsResponse = await axios.get(`http://localhost:8080/comments?postId=${this.post.id}`);//TODO:可能需要修改
      this.comments = await Promise.all(commentsResponse.data.map(async (comment) => {
        const userResponse = await axios.get(`http://localhost:8080/user?userId=${comment.authorid}`);//TODO:可能需要修改
        return { ...comment, user: userResponse.data };
      }));
    },

    // 获取主题列表
    async fetchTopics() {
      const response = await axios.get(`http://localhost:8080/topics/board/${this.boardId}`);
      this.topics = response.data;
    },

    // 提交评论
    async submitComment() {
      if (!this.newCommentContent.trim()) return;

      try {
        const payload = {
          content: this.newCommentContent,
          userid: 3, // 假设userid为3
          postid: this.post.id,
        };

        await axios.post('http://localhost:8080/comments', payload);//TODO:可能需要修改

        this.newCommentContent = '';
        await this.fetchComments();
      } catch (error) {
        console.error("Error submitting comment:", error);
      }
    },

    // 提交新主题
    async submitNewTopic() {
      if (!this.newTopic.title || !this.newTopic.content) {
        alert("标题和内容不能为空");
        return;
      }

      const formData = new FormData();
      formData.append('topicDTO.title', this.newTopic.title);
      formData.append('topicDTO.content', this.newTopic.content);

      // 添加文件
      this.files.forEach(file => {
        formData.append('files', file);
      });

      try {
        const response = await axios.post('http://localhost:8080/topics/create', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        alert("主题发布成功");
        this.newTopic.title = '';
        this.newTopic.content = '';
        this.fetchTopics(); // 刷新主题列表
      } catch (error) {
        console.error("Error submitting topic:", error);
        alert("发布失败");
      }
    },

    // 处理文件上传
    handleFileUpload(event) {
      this.files = event.target.files;
    },

    // 格式化时间戳
    formatTimestamp(timestamp) {
      return new Date(timestamp).toLocaleString();
    }
  },
};
</script>

<style scoped>
/* 样式部分 */

.post-container {
  padding: 20px;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: rgba(0, 0, 0, .1) 0px 4px 12px;
}

.author-info img,
.comment-author-info img {
  width: 30px;
  height: 30px;
  border-radius: 50%;
}

.stats, .timestamp {
  color: #555;
  font-size: .9em;
}

.create-topic input,
.create-topic textarea {
  width: 100%;
  margin-bottom: 10px;
  padding: 8px;
}

.create-topic button {
  padding: 10px;
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
}

.create-topic button:hover {
  background-color: #45a049;
}

.topic-item {
  padding: 10px;
  margin-bottom: 10px;
  background-color: #f0f0f0;
  border-radius: 5px;
}

.add-comment-section textarea {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
}

.add-comment-section button {
  padding: 10px 20px;
  background-color: #007BFF;
  color: white;
  border: none;
  cursor: pointer;
}

.add-comment-section button:hover {
  background-color: #0056b3;
}
</style>
