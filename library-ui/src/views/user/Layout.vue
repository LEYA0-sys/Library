<template>
  <div class="user-layout">
    <el-container class="layout-container">
      <el-header class="header">
        <div class="logo">
          <!-- <img src="@/assets/logo.png" alt="Logo" class="logo-img" /> -->
          <span class="logo-text">书籍订阅系统</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="menu"
          mode="horizontal"
          router
          background-color="#fff"
          text-color="#303133"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/user/home">
            <el-icon><HomeFilled /></el-icon>首页
          </el-menu-item>
          <el-menu-item index="/user/books">
            <el-icon><Reading /></el-icon>图书浏览
          </el-menu-item>
          <el-menu-item index="/user/my-borrows">
            <el-icon><List /></el-icon>我的借阅
          </el-menu-item>
          <el-menu-item index="/user/comments">
            <el-icon><ChatDotRound /></el-icon>留言板
          </el-menu-item>
          <el-menu-item index="/user/credit">
            <el-icon><Medal /></el-icon>我的信用
          </el-menu-item>
        </el-menu>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="el-dropdown-link">
              <el-avatar :size="30" :src="userInfo.avatar || '/avatar.png'" />
              <span class="username">{{ userInfo.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided
                  >退出登录</el-dropdown-item
                >
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
      <el-footer class="footer">
        图书管理系统 &copy; {{ new Date().getFullYear() }}
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { ElMessageBox } from "element-plus";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const userInfo = computed(() => userStore.userInfo || {});

const activeMenu = computed(() => route.path);

const handleCommand = (command) => {
  if (command === "logout") {
    ElMessageBox.confirm("确定要退出登录吗?", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    })
      .then(() => {
        userStore.logout();
      })
      .catch(() => {});
  } else if (command === "profile") {
    // 跳转到个人信息页面
    router.push("/user/profile");
  }
};
</script>

<style scoped>
.user-layout {
  height: 100vh;
  width: 100vw;
}

.layout-container {
  height: 100%;
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.logo {
  display: flex;
  align-items: center;
  margin-right: 40px;
}

.logo-img {
  width: 30px;
  height: 30px;
  margin-right: 10px;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  white-space: nowrap;
}

.menu {
  flex: 1;
  border-bottom: none;
}

.header-right {
  margin-left: auto;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin: 0 5px;
  color: #606266;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

.footer {
  background-color: #fff;
  text-align: center;
  color: #909399;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
}
</style> 