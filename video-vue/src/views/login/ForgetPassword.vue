<template>
  <div style="height: 100vh; display: flex; align-items: center; justify-content: center; background-color: #ADD8E6">
    <div style="background-color: white; width: 30%; border-radius: 5px; padding: 20px">
      <div style="font-size: 20px; font-weight: bold; text-align: center; margin-bottom: 20px">忘记密码</div>
      <el-form :model="forgetUserForm" :rules="rules" ref="forgetPasswordForm" style="width: 100%">

        <el-form-item label="邮箱号" prop="email">
          <el-input v-model="forgetUserForm.email" autocomplete="off" placeholder="请输入邮箱号"></el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="verificationCode">
          <el-input v-model="forgetUserForm.verificationCode" autocomplete="off" placeholder="请输入验证码"></el-input>
          <el-button type="primary" @click="getVerificationCode" :disabled="sendingCode">{{ sendingCode ? `已发送 (${countdown})` : '获取验证码' }}</el-button>
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
import axios from 'axios';

export default {
  name: "ForgetPassword",
  data() {
    return {
      forgetUserForm: {
        email: '',
        verificationCode: '',
        newPassword: '',
        confirmPassword: ''
      },
      sendingCode: false, // 控制验证码按钮状态
      countdown: 0, // 倒计时秒数
      rules: {
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
    async getVerificationCode() {
      if (!this.forgetUserForm.email) {
        this.$message.error('请先输入邮箱号');
        return;
      }

      this.sendingCode = true; // 开始发送验证码
      this.countdown = 60; // 设置倒计时60秒
      try {
        const response = await axios.post('/api/user/get-verification-email', {
          email: this.forgetUserForm.email
        });
        console.log('data',response.data); // 调试信息
        if (response.data.data === 200) {
          this.$message.success('验证码已发送，请检查您的邮箱');
          this.startCountdown(); // 启动倒计时
        } else {
           this.startCountdown(); // 启动倒计时
          this.$message.error(response.data.msg);
        }
      } catch (error) {
        console.error('请求失败:', error);
        this.$message.error('请求失败，请稍后重试');
      }
    },

    // 倒计时逻辑
    startCountdown() {
    console.log('开始倒计时'); // 调试信息
      const interval = setInterval(() => {
        if (this.countdown <= 0) {
          clearInterval(interval);
          this.sendingCode = false; // 恢复按钮状态
        } else {
          this.countdown--; // 倒计时减一
        }
      }, 1000);
    },

    // 提交表单
    async submitForm() {
      this.$refs.forgetPasswordForm.validate(async (valid) => {
        if (valid) {
          try {
            const response = await axios.post('/api/user/reset-password', {
              email: this.forgetUserForm.email,
              code: this.forgetUserForm.verificationCode,
              newPassword: this.forgetUserForm.newPassword
            });

            if (response.data.data=== 200) {
              this.$message.success('密码已重置');
              this.$router.push('/login'); // 跳转到登录页面
            } else {
              this.$message.error(response.data.msg);
            }
          } catch (error) {
            console.error('请求失败:', error);
            this.$message.error('请求失败，请稍后重试');
          }
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
