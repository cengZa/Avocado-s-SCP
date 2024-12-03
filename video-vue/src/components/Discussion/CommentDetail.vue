<template>
  <div class="comments-section">
    <h3>回复</h3>

    <!-- 显示评论 -->
    <div v-for="comment in comments" :key="comment.id" class="comment">
      <div class="comment-header">
        <img :src="comment.authorAvatar" alt="作者头像" class="comment-avatar"/>
        <div class="comment-author-info">
          <p><strong>{{ comment.authorName }}</strong></p>
          <p class="comment-time">{{ formatDate(comment.commentTime) }}</p>
        </div>
      </div>

      <p class="comment-content">{{ comment.content }}</p>

      <!-- 图片附件预览 -->
      <div v-if="comment.images && comment.images.length > 0" class="comment-images">
        <div v-for="(image, index) in comment.images" :key="index" class="comment-image">
          <img :src="image" alt="评论图片" />
        </div>
      </div>

      <!-- 文件附件 -->
      <div v-if="comment.files && comment.files.length > 0" class="comment-files">
        <div v-for="(file, index) in comment.files" :key="index" class="comment-file">
          <a :href="file.url" download>{{ file.name }}</a>
        </div>
      </div>

      <!-- 回复区域（子评论） -->
      <div v-if="comment.replies && comment.replies.length > 0" class="replies">
        <div v-for="reply in comment.replies" :key="reply.id" class="comment reply">
          <div class="comment-header">
            <img :src="reply.authorAvatar" alt="回复作者头像" class="comment-avatar"/>
            <div class="comment-author-info">
              <p><strong>{{ reply.authorName }}</strong></p>
              <p class="comment-time">{{ formatDate(reply.timestamp) }}</p>
            </div>
          </div>
          <p class="comment-content">{{ reply.content }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    comments: {
      type: Array,
      required: true
    },
    topicId: {
      type: String,
      required: true
    }
  },
  methods: {
    formatDate(timestamp) {
      const date = new Date(timestamp * 1000);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}/${month}/${day}`;
    }
  }
};
</script>

<style scoped>
.comment {
  margin-top: 20px;
}

.comment-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
}

.comment-content {
  margin-top: 10px;
}

.comment-header {
  display: flex;
  align-items: center;
}

.comment-author-info {
  margin-left: 10px;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-images img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  margin-right: 5px;
}

.comment-files a {
  color: #007bff;
  text-decoration: none;
}

.replies {
  margin-left: 20px;
}

.replies .comment {
  margin-top: 10px;
}
</style>
