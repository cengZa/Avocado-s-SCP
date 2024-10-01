<template>
  <div style="height: 100vh; display: flex; align-items: center; justify-content: center; background-color: #ADD8E6">
    <div style="background-color: white; width: 30%; border-radius: 5px; padding: 20px">
      <div style="font-size: 20px; font-weight: bold; text-align: center; margin-bottom: 20px">忘记密码</div>
      <el-form :model="forgetUserForm" :rules="rules" ref="forgetPasswordForm" style="width: 100%">
        <el-form-item label="账号" prop="username">
          <el-input v-model="forgetUserForm.username" autocomplete="off" placeholder="请输入账号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱号" prop="email">
          <el-input v-model="forgetUserForm.email" autocomplete="off" placeholder="请输入邮箱号"></el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="verificationCode">
          <el-input v-model="forgetUserForm.verificationCode" autocomplete="off" placeholder="请输入验证码"></el-input>
          <el-button type="primary" @click="getVerificationCode" :disabled="sendingCode">{{ sendingCode ? '已发送' : '获取验证码' }}</el-button>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="forgetUserForm.newPassword" type="password" autocomplete="off" placeholder="请输入新密码"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="forgetUserForm.confirmPassword" type="password" autocomplete="off" placeholder="请确认新密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="submitForm">确 定</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: center; margin-top: 10px">
        <router-link to="/login">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ForgetPassword",
  data() {
    return {
      forgetUserForm: {
        username: '',
        email: '',
        verificationCode: '',
        newPassword: '',
        confirmPassword: ''
      },
      sendingCode: false, // 控制验证码按钮状态
      rules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱号', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        verificationCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
        newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: this.validateConfirmPassword, trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    // 获取验证码
    getVerificationCode() {
      if (!this.forgetUserForm.email) {
        this.$message.error('请先输入邮箱号');
        return;
      }

      this.sendingCode = true;
      this.$request.post('/api/get-verification-code', { email: this.forgetUserForm.email }).then(res => {
        if (res.code === '200') {
          this.$message.success('验证码已发送，请检查您的邮箱');
        } else {
          this.$message.error(res.msg);
        }
      }).catch(error => {
        console.error('请求失败:', error);
        this.$message.error('请求失败，请稍后重试');
      }).finally(() => {
        this.sendingCode = false;
      });
    },
    // 提交表单
    submitForm() {
      this.$refs.forgetPasswordForm.validate((valid) => {
        if (valid) {
          // 校验通过，发送请求到后端
          this.$request.post('/api/reset-password', this.forgetUserForm).then(res => {
            if (res.code === '200') {
              this.$message.success('密码已重置');
              this.$router.push('/login'); // 跳转到登录页面
            } else {
              this.$message.error(res.msg);
            }
          }).catch(error => {
            console.error('请求失败:', error);
            this.$message.error('请求失败，请稍后重试');
          });
        } else {
          console.log("表单验证失败");
          return false;
        }
      });
    },
    // 校验确认密码是否一致
    validateConfirmPassword(rule, value, callback) {
      if (value !== this.forgetUserForm.newPassword) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    }
  }
};
</script>

<style scoped>
/* 根据需要添加样式 */
</style>
