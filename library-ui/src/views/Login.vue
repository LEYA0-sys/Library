<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">图书管理系统</div>
      <div class="login-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="用户登录" name="user"></el-tab-pane>
          <el-tab-pane label="管理员登录" name="admin"></el-tab-pane>
        </el-tabs>
      </div>
      <el-form
        :model="loginForm"
        :rules="loginRules"
        ref="loginFormRef"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            class="login-button"
            @click="handleLogin"
            :loading="loading"
            >登录</el-button
          >
        </el-form-item>
        <div class="login-options">
          <span v-if="activeTab === 'user'" @click="goToRegister"
            >没有账号？点击注册</span
          >
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store/user";

const router = useRouter();
const userStore = useUserStore();

const activeTab = ref("user");
const loginFormRef = ref(null);
const loading = ref(false);

const loginForm = reactive({
  username: "",
  password: "",
});

const loginRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 20, message: "用户名长度应为3到20个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度应为6到20个字符", trigger: "blur" },
  ],
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        if (activeTab.value === "admin") {
          await userStore.adminLogin(loginForm);
          ElMessage.success("管理员登录成功");
        } else {
          await userStore.userLogin(loginForm);
          ElMessage.success("登录成功");
        }
      } catch (error) {
        ElMessage.error(error || "登录失败");
      } finally {
        loading.value = false;
      }
    }
  });
};

const goToRegister = () => {
  router.push("/register");
};
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--background-color);
}

.login-box {
  width: 400px;
  padding: 40px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: var(--primary-color);
  margin-bottom: 30px;
}

.login-tabs {
  margin-bottom: 20px;
}

.login-form {
  margin-top: 20px;
}

.login-button {
  width: 100%;
}

.login-options {
  margin-top: 15px;
  text-align: right;
  font-size: 14px;
  color: var(--primary-color);
  cursor: pointer;
}

.login-options span:hover {
  text-decoration: underline;
}
</style> 