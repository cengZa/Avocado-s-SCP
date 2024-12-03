<template>
  <div v-if="isOpen" class="modal-overlay">
    <div class="modal-content">
      <h3>提交 {{ assignment.title }} 的作业</h3>

      <!-- 文本 -->
      <textarea v-model="submissionText" placeholder="请输入您的内容..."></textarea>
      <!-- 文件 -->
      <input type="file" @change="handleFileUpload"/>
      <!-- 提交和取消 -->
      <div class="modal-actions">
        <button @click="handleSubmit">提交</button>
        <button @click="$emit('close')">取消</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AssignmentModal',

  props: {
    isOpen: {
      type: Boolean,
      required: true
    },
    assignment: {
      type: Object,
      required: true
    }
  },

  data() {
    return {
      submissionText: '',
      selectedFile: null
    };
  },

  methods: {
    handleSubmit() {
      if (!this.submissionText.trim()) {
        alert('请填写内容后再提交!');
        return;
      }

      // 提交作业逻辑（可以根据实际需求进行扩展）
      this.$emit('submit', {text: this.submissionText, assignmentId: this.assignment.id});

      // 清空输入文本
      this.submissionText = '';
    }
  }
}
</script>
<style scoped lang="scss">
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;

  display: flex;
  justify-content: center;
  align-items: center;

  z-index: 9999;

  /* 设置为半透明黑色以使背景变暗 */
  background-color: rgba(0,0,0,0.3);
}

.modal-content {
  /* 设置为白色或其他不透明颜色 */
  background-color: #fff;

  padding :20px;
  border-radius :8px;

  /* 更新阴影以更清晰地显示在非透明背景上 */
  box-shadow :rgba(0,0,0,0.7) -1px -1px;

  width:auto;
  max-width :90%;
}

textarea {
  width :100%;
  height :80px;
  margin-bottom :10px;
  resize:none; //禁止调整大小
}

.modal-actions {
  display:flex;
  justify-content:flex-end;
  margin-top :10px;
}

.modal-actions button {
  margin-left :10px;
}
</style>
