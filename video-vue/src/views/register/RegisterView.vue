<template>
  <!-- 背景和整体布局 -->
  <div class="register-container">
    <!-- 右边注册信息部分 -->
    <div class="register-form">
      <el-card class="box-card">
        <h2>注册</h2>
        <el-form
          :model="ruleForm"
          status-icon
          :rules="rules"
          ref="ruleForm"
          label-position="left"
          label-width="80px"
          class="demo-ruleForm"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="ruleForm.username"></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input type="email" v-model="ruleForm.email" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="pass">
            <el-input type="password" v-model="ruleForm.pass" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="password">
            <el-input type="password" v-model="ruleForm.password" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
        <div class="btnGroup">
          <el-button type="primary" @click="submitForm('ruleForm')" v-loading="loading">提交</el-button>
          <el-button @click="resetForm('ruleForm')">重置</el-button>
          <el-button @click="goBack">返回</el-button>
        </div>
      </el-card>

      <!-- 新框体用于第二步输入验证码 -->
      <el-card class="box-card" v-if="step === 2" style="margin-top: 20px;">
        <h2>输入验证码</h2>
        <el-form :model="ruleForm">
          <el-form-item label="验证码" prop="code">
            <el-input v-model="ruleForm.code" autocomplete="off"></el-input>
          </el-form-item>
          <el-button type="primary" @click="submitCode">验证</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请输入密码"));
      } else {
        if (this.ruleForm.password !== "") {
          this.$refs.ruleForm.validateField("password");
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
      } else if (value !== this.ruleForm.pass) {
        callback(new Error("两次输入密码不一致!"));
      } else {
        callback();
      }
    };
    return {
      step: 1,
      ruleForm: {
        username: "",
        pass: "",
        password: "",
        email: "",
        code: "",
      },
      rules: {
        username: [{ required: true, message: "用户名不能为空！", trigger: "blur" }],
        pass: [{ required: true, validator: validatePass, trigger: "blur" }],
        password: [{ required: true, validator: validatePass2, trigger: "blur" }],
        email: [{ type: "email", message: "请输入正确的邮箱地址", required: true, trigger: "blur" }],
        code: []
      },
      loading: false
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        this.loading = true;
        if (valid && this.step === 1) {
          this.axios({
            url: "/api/user/register",
            method: "post",
            headers: {
              "Content-Type": "application/json",
            },
            data: {
              username: this.ruleForm.username,
              password: this.ruleForm.password,
              email: this.ruleForm.email
            }
          }).then((res) => {
            if (res.data.code === '0') {
              this.$message({
                message: "邮件已发送，请检查您的邮箱进行验证。",
                type: "success",
              });
              this.step = 2;  // 切换到验证码输入步骤
              this.rules.code = [{ required: true, message: "请输入验证码", trigger: "blur" }];
            } else {
              this.$message({
                message: res.data.msg,
                type: "warning",
              });
            }
            this.loading = false;
          });
        } else {
          console.log("表单填写有误，请检查！");
          this.loading = false;
          return false;
        }
      });
    },
    submitCode() {
      this.$refs.ruleForm.validateField('code', (valid) => {
        if (valid) {
          this.loading = true;
          this.axios({
            url: "/api/user/verify-code",  // 确保 URL 是正确的，这里添加了 '/api' 前缀
            method: "post",
            headers: {
              "Content-Type": "application/json",
            },
            data: JSON.stringify({  // 使用 JSON.stringify 确保数据格式为 JSON
              email: this.ruleForm.email,
              code: this.ruleForm.code
            })
          }).then((res) => {
            if (res.data.code === '0') {
              this.$message({
                message: "邮箱验证成功！",
                type: "success",
              });

               this.$router.push('/login');
            } else {
              this.$message({
                message: res.data.msg,
                type: "warning",
              });
            }
            this.loading = false;
          });
        } else {
         this.$router.push('/home');
            console.log("验证码输入有误，请重新输入！");
            this.loading = false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    goBack() {
      this.$router.go(-1);
    },
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #ADD8E6; /* 浅蓝色背景 */
}

.register-form {
  flex: 1;
}

.box-card {
  width: 400px;
  margin: auto;
  background-color: white;
}
</style>
