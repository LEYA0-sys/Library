<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <div class="dashboard-title">系统概览</div>
      <el-button type="primary" @click="fetchDashboardData" :loading="loading">
        <el-icon><Refresh /></el-icon> 刷新数据
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">图书总数</div>
            <div class="stat-value">{{ stats.totalBooks }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon user-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">用户总数</div>
            <div class="stat-value">{{ stats.totalUsers }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon borrow-icon">
            <el-icon><SwitchButton /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">借阅总数</div>
            <div class="stat-value">{{ stats.totalBorrows }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon active-icon">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">当前借阅</div>
            <div class="stat-value">{{ stats.activeBorrows }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近借阅 -->
    <el-card class="recent-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>最近借阅记录</span>
          <el-button class="button" text @click="viewMore">查看更多</el-button>
        </div>
      </template>
      <el-table :data="recentBorrows" style="width: 100%" v-loading="loading">
        <el-table-column prop="bookName" label="图书名称" />
        <el-table-column prop="username" label="借阅人" />
        <el-table-column prop="borrowTime" label="借阅时间" />
        <el-table-column prop="returnTime" label="预计归还时间" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === '已归还' ? 'success' : 'warning'"
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 最新图书 -->
    <el-card class="recent-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>最新图书</span>
          <el-button class="button" text @click="viewMoreBooks"
            >查看更多</el-button
          >
        </div>
      </template>
      <el-table :data="recentBooks" style="width: 100%" v-loading="loading">
        <el-table-column prop="bookName" label="图书名称" />
        <el-table-column prop="author" label="作者" />
        <el-table-column prop="bookCode" label="图书编号" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === '可借阅' ? 'success' : 'info'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="入库时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getBookList } from "@/api/book";
import { getBorrowList } from "@/api/borrow";
import { getSystemStats } from "@/api/index";
import { Refresh } from "@element-plus/icons-vue";

const router = useRouter();
const loading = ref(false);

// 统计数据
const stats = ref({
  totalBooks: 0,
  totalUsers: 0,
  totalBorrows: 0,
  activeBorrows: 0,
});

// 最近借阅记录
const recentBorrows = ref([]);

// 最新图书
const recentBooks = ref([]);

// 获取仪表盘数据
const fetchDashboardData = async () => {
  loading.value = true;
  try {
    // 获取统计数据
    const statsRes = await getSystemStats();
    if (statsRes.code === 200) {
      stats.value = statsRes.data;
      console.log("系统统计数据:", statsRes.data);
    } else {
      ElMessage.error(statsRes.message || "获取统计数据失败");
    }

    // 获取最近借阅记录
    const borrowRes = await getBorrowList();
    if (borrowRes.code === 200) {
      // 排序并获取最近的5条借阅记录
      recentBorrows.value = (borrowRes.data || [])
        .sort(
          (a, b) => new Date(b.createTime || 0) - new Date(a.createTime || 0)
        )
        .slice(0, 5)
        .map((item) => {
          // 字段转换为前端所需格式
          return {
            ...item,
            bookName: item.bookName,
            username: item.username,
            borrowTime: item.startTime,
            returnTime: item.expectTime,
            status:
              item.borrowStatus === "borrowing"
                ? "借阅中"
                : item.borrowStatus === "returned"
                ? "已归还"
                : item.borrowStatus === "overdue"
                ? "已逾期"
                : item.borrowStatus,
          };
        });
    } else {
      ElMessage.error(borrowRes.message || "获取借阅记录失败");
    }

    // 获取最新图书
    const bookRes = await getBookList();
    if (bookRes.code === 200) {
      // 按创建时间排序，获取最新的5本书
      recentBooks.value = (bookRes.data || [])
        .sort(
          (a, b) => new Date(b.createTime || 0) - new Date(a.createTime || 0)
        )
        .slice(0, 5)
        .map((book) => {
          return {
            ...book,
            bookCode: book.isbn || book.bookCode,
            status: book.status === "available" ? "可借阅" : "已借出",
          };
        });
    } else {
      ElMessage.error(bookRes.message || "获取图书数据失败");
    }
  } catch (error) {
    ElMessage.error("获取数据失败");
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 查看更多借阅记录
const viewMore = () => {
  router.push("/admin/borrows");
};

// 查看更多图书
const viewMoreBooks = () => {
  router.push("/admin/books");
};

onMounted(() => {
  fetchDashboardData();
});
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.dashboard-title {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 20px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  background-color: #409eff;
  color: white;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 20px;
}

.stat-icon :deep(svg) {
  width: 30px;
  height: 30px;
}

.user-icon {
  background-color: #67c23a;
}

.borrow-icon {
  background-color: #e6a23c;
}

.active-icon {
  background-color: #f56c6c;
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 26px;
  font-weight: bold;
  color: #303133;
}

.recent-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 