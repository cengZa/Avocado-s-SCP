<template>
  <div class="assign-homework">
    <h2>发布作业</h2>
    <div class="homeworkdetail">
      <form @submit.prevent="submitHomework" class="homework-form">
        <div class="form-group">
          <label for="title">作业标题:</label>
          <input type="text" id="title" v-model="homework.title" required />
        </div>

        <div class="form-group">
          <label for="description">作业介绍:</label>
          <textarea id="description" v-model="homework.description" rows="4" required></textarea>
        </div>

        <div class="form-group">
          <label for="startTime">开始时间:</label>
          <input type="datetime-local" id="startTime" v-model="homework.startTime" required />
        </div>

        <div class="form-group">
          <label for="endTime">结束时间:</label>
          <input type="datetime-local" id="endTime" v-model="homework.endTime" required />
        </div>

        <div class="form-group">
          <label for="attachment">附件:</label>
          <input type="file" id="attachment" @change="handleFileUpload"/>
        </div>

        <!-- 提交按钮 -->
        <button type='submit'>发布作业</button>
      </form>

      <!-- 显示上传的文件名 -->
      <p v-if='uploadedFileName' class='uploaded-file-info'>已选择文件: {{ uploadedFileName }}</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      homework: {
        title: '',
        description: '', // 添加描述字段
        startTime: '',
        endTime: ''
      },
      uploadedFile: null,
      uploadedFileName: ''
    };
  },

  methods: {
    handleFileUpload(event) {
      const file = event.target.files[0];
      if (file) {
        this.uploadedFile = file;
        this.uploadedFileName = file.name;
      }
    },

    submitHomework() {
      const formData = new FormData();
      formData.append('title', this.homework.title);
      formData.append('description', this.homework.description); // 添加描述到表单数据
      formData.append('start_time', this.homework.startTime);
      formData.append('end_time', this.homework.endTime);

      if (this.uploadedFile) {
        formData.append('attachment', this.uploadedFile);
      }

      // 清空表单
      this.resetForm();
    },

    resetForm() {
      this.homework.title = '';
      this.homework.description = ''; // 重置描述字段
      this.homework.startTime = '';
      this.homework.endTime = '';
      this.uploadedFile = null;
      this.uploadedFileName = '';
    }
  }
};
</script>

<style scoped>
.assign-homework {
  width: calc(80vw);
  padding: 20px;

  display:flex;
  flex-direction :column ;

}

.homeworkdetail{
  display:flex ;
  flex-direction :column ;
  width:100%;
}

.homework-form {
  display:flex ;
  flex-direction :column ;
  gap :15px ;
}

.form-group label {
  font-weight:bold ;
}

input[type='text'],
input[type='datetime-local'],
input[type='file'],
textarea {
  padding :12px ;
  border-radius :5px ;
  border :1px solid #ccc ;
  outline:none ;
  transition:border-color .3s ease-in-out;
  width:100%;
}

textarea {
  resize:none;
  font-size :16px;
}

input[type='text']:focus,
input[type='datetime-local']:focus,
textarea:focus,
input[type='file']:focus{
  border-color:#00509e;
}

button[type='submit'] {
  background-color:#00509e;
  color:white ;
  border:none ;
  padding :12px ;
  border-radius :5px ;
  cursor:pointer ;
}

/* 上传文件信息样式 */
.uploaded-file-info {
  text-align:center ;
  margin-top :10 px ;
  color:#555 ;
}
</style>
