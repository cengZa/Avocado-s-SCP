 import ValidCode from "@/components/ValidCode
<template>
  <div style="height: 100vh; display: flex; align-items: center; justify-content: center; background-color:#ADD8E6">
    <div style="display: flex; background-color: white; width: 50%; border-radius: 5px; overflow: hidden">
      <div style="flex: 1">
      </div>
      <div style="flex: 1; display: flex; align-items: center; justify-content: center">
        <el-form :model="user" style="width: 80%" :rules="rules" ref="loginRef">
          <div style="font-size: 20px; font-weight: bold; text-align: center; margin-bottom: 20px">欢迎登录</div>
          <el-form-item prop="username">
            <el-input prefix-icon="el-icon-user" size="medium" placeholder="请输入账号" v-model="user.username"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input prefix-icon="el-icon-lock" size="medium" show-password placeholder="请输入密码" v-model="user.password"></el-input>
          </el-form-item>
          <el-form-item prop="code">
            <div style="display: flex">
              <el-input placeholder="请输入验证码" prefix-icon="el-icon-circle-check" size="medium" style="flex: 1" v-model="user.code"></el-input>
              <div style="flex: 1; height: 36px">
                <valid-code @update:value="getCode" />
              </div>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" style="width: 100%" @click="login">登 录</el-button>
          </el-form-item>
          <div style="display: flex">
            <div style="flex: 1">还没有账号？请 <span style="color: #ADD8E6; cursor: pointer" @click="$router.push('/register')">注册</span></div>
            <div style="flex: 1; text-align: right"><span style="color:#ADD8E6; cursor: pointer" @click="$router.push('/forget')">忘记密码</span></div>
          </div>
        </el-form>
      </div>
    </div>

  </div>
</template>
<script>
// 在components属性中，将ValidCode作为一个组件注册进来。这样就可以在模板中使用<valid-code>标签。
import ValidCode from "@/components/ValidCode";
export default {
  name: "Login",
  components: {
    ValidCode
  },
  data() {
    const validateCode = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入验证码'))
      } else if (value.toLowerCase() !== this.code) {
        callback(new Error('验证码错误'))
      } else {
        callback()
      }
    };

    return {
      code: '', // 验证码组件传递过来的code
      user: {
        code: '', // 表单里用户输入的验证码
        username: '', // 用户名
        password: '' // 密码
      },
      // 表单验证规则
      rules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        code: [{ validator: validateCode, trigger: 'blur' }],
      }
    };
  },
  methods: {

    getCode(code) { // 获取验证码
      this.code = code.toLowerCase(); // 转小写
    },
    login() { // 登录逻辑
      this.$refs['loginRef'].validate((valid) => {
        if (valid) {
          let _this = this;
          console.log('params:', _this.user.username);
          console.log('params:', _this.user.password);

          // 使用 axios 将登录信息发送到后端
          this.axios({
            url: "api/user/login", // 请求地址
            method: "post", // 请求方法
            headers: { "Content-Type": "application/json" }, // 请求头
            params: {
              username: _this.user.username,
              password: _this.user.password,
            },
          }).then((res) => {

            console.log('User data:', res.data.data); // 打印用户信息
            if (res.data.code === "0") { // 登录成功
              sessionStorage.setItem("userInfo", JSON.stringify(res.data.data)); // 存储用户信息
              this.$router.push('/menu'); // 跳转到课程菜单界面
              this.$message.success(res.data.msg); // 显示成功信息
            } else { // 登录失败
              this.$message.warning(res.data.msg);
            }
          }).catch(error => {
            console.log('Error:', error);
          });
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    }
  }
};
</script>

<style scoped>
/* 可根据需要在此添加样式 */
</style>
