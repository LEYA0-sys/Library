<template>
  <div class="app-container">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { useUserStore } from "@/store/user";
import { useBorrowStore } from "@/store/borrow";

const userStore = useUserStore();
const borrowStore = useBorrowStore();

onMounted(() => {
  // 如果用户已登录，初始化借阅状态
  if (userStore.isLogin && userStore.userId) {
    borrowStore.initialize(userStore.userId);
  }
});
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body {
  height: 100%;
  font-family: "Microsoft YaHei", sans-serif;
}

.app-container {
  height: 100vh;
  width: 100vw;
}

a {
  text-decoration: none;
}

.el-message-box {
  width: 380px !important;
}
</style> 