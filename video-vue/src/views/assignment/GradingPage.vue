<template>
  <div class="grading">
    <h2>批改作业 {{ homeworkId }} - 学号: {{ id }}</h2>

    <div class="content">
      <!-- 文件预览区域 -->
      <div class="file-preview">
        <h3>文件预览</h3>
        <iframe v-if="fileUrl" :src="fileUrl" frameborder="0"></iframe>
        <p v-else>正在加载文档，请稍候...</p>
      </div>

      <!-- 评语和给分区域 -->
      <div class="feedback">
        <h3>评语</h3>
        <textarea v-model="comments" placeholder="请输入评语..." rows="5"></textarea>

        <h3>给分</h3>
        <input type="number" v-model.number="score" placeholder="请输入分数..." min="0" />
      </div>
    </div>

    <!-- 按钮组 -->
    <div class="button-group">
      <button @click.prevent="previousHomework">上一份</button>
      <button @click.prevent="submitFeedback">提交</button>
      <button @click.prevent="nextHomework">下一份</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      id: null,
      homeworkId: null,
      comments: '',
      score: null,
      fileUrl: '', // 用于存储从后端获取的文件URL
    };
  },

  mounted() {
    // 从路由参数中获取学生ID和作业ID
    this.id = this.$route.query.id;
    this.homeworkId = this.$route.query.homeworkId;

    // 调用方法获取文件URL
    this.fetchFileUrl();

    console.log(this.id); // 输出以检查ID是否有效
  },

  methods: {
    async fetchFileUrl() {
      try {
        const response = await fetch(`/api/get-file?homeworkId=${this.homeworkId}&studentId=${this.id}`);

        if (!response.ok) {
          throw new Error('网络响应不是OK');
        }

        const data = await response.json();

        // 假设返回的数据结构包含一个url字段
        this.fileUrl = data.url;
      } catch (error) {
        console.error('获取文件失败:', error);
      }
    },

    previousHomework() {
      console.log('查看上一份作业');
    },

    submitFeedback() {
      console.log('提交评语:', this.comments, '给分:', this.score);

      // 在这里可以调用API或者其他逻辑来处理提交数据
    },

    nextHomework() {
      console.log('查看下一份作业');
    }
  }
};
</script>

<style scoped>
body {
  margin: 0;
  padding: 0;
  height: 100vh;
}

.grading {
  display: flex;
  flex-direction: column;
}

.content {
  display: flex;
  height: calc(90vh - 60px);
}

.file-preview{
  height: calc(100%);
  width: calc(70%);
}

.feedback {
  height: calc(100%);
  width: calc(30%);
}

.file-preview, .feedback {
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: rgba(0,0,0,0.1) 0px 4px 12px;
}

.file-preview h3, .feedback h3 {
  margin-top:10px;
}

.file-preview iframe {
  width: calc(100% -30px);
  height: calc(100% - (40px + var(--header-height)));
  border-radius:8px;
  border:none;
}

.feedback textarea, .feedback input[type='number'] {
  width: calc(100% -20px);
  margin-bottom :10px ;
  padding :10px ;
  border-radius :5px ;
  border:none ;
  box-shadow :-1px -1px #eee inset ,1px -1px #eee inset ,-1px -1px #ddd inset ,1px -1px #ddd inset ;
}

.button-group {
  display:flex ;
  justify-content:center ;
  padding:.5em ;
  height :10vh;
  width :100vw;
}
.button-group button {
  background-color:#007bff ;
  color:white ;
  border:none ;
  padding:.5em ;
  border-radius:.25em ;
  cursor:pointer ;
  flex-grow :0.3 ;
}
.button-group button:hover {
  background-color:#0056b3 ;
}
.button-group button:not(:last-child) {
  margin-right:.5em ;
}
</style>
