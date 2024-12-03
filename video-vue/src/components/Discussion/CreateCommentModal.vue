<template>
  <el-dialog :visible.sync="visible" title="发表评论" @close="handleClose">
    <el-form :model="form" ref="formRef" label-width="80px">
      <!-- 评论内容 -->
      <el-form-item label="评论内容" prop="content" :rules="[{ required: true, message: '请输入评论内容', trigger: 'blur' }]">
        <el-input type="textarea" v-model="form.content" placeholder="请输入评论内容..." rows="4" />
      </el-form-item>

      <!-- 提交按钮 -->
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleCreate">发表评论</el-button>
      </span>
    </el-form>
  </el-dialog>
</template>

<script>
export default {
  props: {
    visible: Boolean,  // 控制模态框的显示与隐藏
  },
  data() {
    return {
      form: {
        content: '',  // 存储评论内容
      },
    };
  },
  methods: {
    // 关闭模态框
    handleClose() {
      this.$emit('close');  // 触发关闭事件，父组件接收到后，隐藏模态框
    },

    // 提交评论
    handleCreate() {
      // 验证表单
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          // 触发父组件的create事件，传递评论内容
          this.$emit('create', this.form);
          this.form.content = '';  // 清空评论内容
        } else {
          this.$message.error('请输入评论内容');
        }
      });
    },
  },
};
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>
