<template>
  <el-dialog :visible.sync="visible" title="发起主题" @close="handleClose">
    <el-form :model="form" ref="formRef" label-width="80px">
      <el-form-item label="主题名称" prop="topicName" :rules="[{ required: true, message: '请输入主题名称', trigger: 'blur' }]">
        <el-input v-model="form.topicName" />
      </el-form-item>

      <el-form-item label="主题内容" prop="content" :rules="[{ required: true, message: '请输入主题内容', trigger: 'blur' }]">
        <el-input type="textarea" v-model="form.content" />
      </el-form-item>

      <el-form-item label="上传附件">
        <el-upload
          action="/upload"
          multiple
          :limit="3"
          :on-success="handleUploadSuccess"
          :before-upload="beforeUpload"
        >
          <el-button slot="trigger" size="small" type="primary">点击上传</el-button>
          <el-button slot="upload" size="small" type="success">开始上传</el-button>
        </el-upload>
      </el-form-item>

      <span slot="footer" class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleCreate">创建</el-button>
      </span>
    </el-form>
  </el-dialog>
</template>

<script>
export default {
  props: {
    visible: Boolean
  },
  data() {
    return {
      form: {
        topicName: '',
        content: '',
        attachments: []
      }
    };
  },
  methods: {
    handleClose() {
      this.$emit('close');
    },
    handleCreate() {
      this.$emit('create', this.form);
      this.form = { topicName: '', content: '', attachments: [] };
    },
    handleUploadSuccess(response, file, fileList) {
      this.form.attachments = fileList.map(f => f.response.fileUrl);
    },
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/');
      if (!isImage) {
        this.$message.error('只能上传图片文件');
      }
      return isImage;
    }
  }
};
</script>
