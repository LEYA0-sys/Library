<template>
  <div class="home-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h1 class="welcome-title">欢迎使用书籍订阅系统</h1>
        <p class="welcome-desc">
          在这里，您可以浏览图书、借阅图书、管理个人借阅记录
        </p>
        <el-button type="primary" size="large" @click="goToBooks"
          >立即浏览图书</el-button
        >
      </div>
    </div>

    <!-- 最新图书推荐 -->
    <div class="section">
      <div class="section-header">
        <h2 class="section-title">最新上架</h2>
        <el-button text @click="goToBooks">查看更多</el-button>
      </div>
      <el-row :gutter="20" class="book-list">
        <el-col :span="4" v-for="book in latestBooks" :key="book.id">
          <el-card
            class="book-card"
            shadow="hover"
            @click="viewBookDetail(book)"
          >
            <div class="book-cover">
              <img :src="book.cover || '/default-book.png'" alt="封面" />
            </div>
            <div class="book-info">
              <div class="book-name">{{ book.bookName }}</div>
              <div class="book-author">{{ book.author }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 热门图书推荐 -->
    <div class="section">
      <div class="section-header">
        <h2 class="section-title">热门借阅</h2>
        <el-button text @click="goToBooks">查看更多</el-button>
      </div>
      <el-row :gutter="20" class="book-list">
        <el-col :span="4" v-for="book in popularBooks" :key="book.id">
          <el-card
            class="book-card"
            shadow="hover"
            @click="viewBookDetail(book)"
          >
            <div class="book-cover">
              <img :src="book.cover || '/default-book.png'" alt="封面" />
            </div>
            <div class="book-info">
              <div class="book-name">{{ book.bookName }}</div>
              <div class="book-author">{{ book.author }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getBookList } from "@/api/book";
import { useUserStore } from "@/store/user";

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);

// 最新图书
const latestBooks = ref([]);
// 热门图书
const popularBooks = ref([]);

// 获取数据
const fetchData = async () => {
  loading.value = true;
  try {
    // 获取最新图书
    const bookRes = await getBookList();
    if (bookRes.code === 200 && bookRes.data) {
      // 按创建时间排序，取前6本
      latestBooks.value = [...bookRes.data]
        .sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
        .slice(0, 6);

      // 按借阅次数排序，取前6本
      popularBooks.value = [...bookRes.data]
        .sort((a, b) => b.borrowCount - a.borrowCount)
        .slice(0, 6);
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取数据失败");
  } finally {
    loading.value = false;
  }
};

// 跳转到图书浏览页
const goToBooks = () => {
  router.push("/user/books");
};

// 查看图书详情
const viewBookDetail = (book) => {
  router.push({
    path: "/user/books",
    query: { id: book.id },
  });
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.home-container {
  padding: 0;
}

.welcome-banner {
  height: 300px;
  background: linear-gradient(135deg, #4a69bd, #1e3799);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  margin-bottom: 30px;
}

.welcome-content {
  max-width: 800px;
  padding: 0 20px;
}

.welcome-title {
  font-size: 32px;
  margin-bottom: 10px;
}

.welcome-desc {
  font-size: 16px;
  margin-bottom: 30px;
  opacity: 0.8;
}

.section {
  margin-bottom: 30px;
  padding: 0 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 22px;
  font-weight: bold;
  margin: 0;
}

.book-list {
  margin-bottom: 20px;
}

.book-card {
  cursor: pointer;
  transition: all 0.3s;
  height: 280px;
  overflow: hidden;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.book-cover {
  height: 180px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
}

.book-cover img {
  height: 100%;
  object-fit: cover;
}

.book-info {
  padding: 0 10px;
}

.book-name {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-author {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style> 