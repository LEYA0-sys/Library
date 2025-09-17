<template>
  <div class="books-container">
    <div class="page-title">图书浏览</div>

    <!-- 搜索和筛选 -->
    <div class="filter-container">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入图书名称或作者"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch" class="ml-10"
          >搜索</el-button
        >
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="category-filter">
        <span class="filter-label">分类：</span>
        <el-radio-group v-model="selectedType" @change="handleTypeChange">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button
            v-for="type in bookTypes"
            :key="type.id"
            :label="type.id"
          >
            {{ type.typeName }}
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 图书列表 -->
    <div v-loading="loading">
      <template v-if="displayMode === 'grid'">
        <el-row :gutter="20">
          <el-col
            :xs="12"
            :sm="8"
            :md="6"
            :lg="4"
            v-for="book in books"
            :key="book.id"
            class="book-col"
          >
            <el-card class="book-card" shadow="hover" @click="openDetail(book)">
              <div class="book-cover">
                <img :src="book.cover || '/default-book.png'" alt="封面" />
                <div
                  class="book-status"
                  :class="{
                    'status-available':
                      book.status === '可借阅' || book.status === 'available',
                  }"
                >
                  {{ book.status === "available" ? "可借阅" : book.status }}
                </div>
              </div>
              <div class="book-info">
                <div class="book-name">{{ book.bookName }}</div>
                <div class="book-author">{{ book.author }}</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 无数据 -->
        <el-empty v-if="books.length === 0" description="暂无图书数据" />
      </template>

      <template v-else>
        <el-table :data="books" style="width: 100%" border>
          <el-table-column prop="bookName" label="图书名称" min-width="150" />
          <el-table-column prop="author" label="作者" min-width="120" />
          <el-table-column prop="bookCode" label="图书编号" min-width="150" />
          <el-table-column label="分类" min-width="120">
            <template #default="scope">
              <el-tag
                v-for="type in scope.row.bookTypes"
                :key="type.id"
                class="mr-5"
              >
                {{ type.typeName }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag
                :type="
                  scope.row.status === '可借阅' ||
                  scope.row.status === 'available'
                    ? 'success'
                    : 'info'
                "
              >
                {{
                  scope.row.status === "available" ? "可借阅" : scope.row.status
                }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="openDetail(scope.row)"
                >详情</el-button
              >
              <el-button
                link
                type="success"
                @click="handleBorrow(scope.row)"
                v-if="
                  scope.row.status === '可借阅' ||
                  scope.row.status === 'available'
                "
              >
                借阅
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </div>

    <!-- 切换显示模式 -->
    <div class="view-toggle">
      <el-switch
        v-model="displayMode"
        active-value="grid"
        inactive-value="list"
        active-text="网格视图"
        inactive-text="列表视图"
      />
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[12, 24, 36, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 图书详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="图书详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <div class="book-detail" v-if="currentBook">
        <div class="book-detail-header">
          <div class="book-detail-cover">
            <img :src="currentBook.cover || '/default-book.png'" alt="封面" />
          </div>
          <div class="book-detail-info">
            <h2 class="book-detail-name">{{ currentBook.bookName }}</h2>
            <div class="book-detail-item">
              <span class="label">作者：</span>
              <span>{{ currentBook.author }}</span>
            </div>
            <div class="book-detail-item">
              <span class="label">图书编号：</span>
              <span>{{ currentBook.bookCode }}</span>
            </div>
            <div class="book-detail-item">
              <span class="label">分类：</span>
              <span>
                <template
                  v-if="
                    currentBook.bookTypes && currentBook.bookTypes.length > 0
                  "
                >
                  <el-tag
                    v-for="type in currentBook.bookTypes"
                    :key="type.id"
                    class="mr-5"
                  >
                    {{ type.typeName }}
                  </el-tag>
                </template>
                <span v-else>暂无分类</span>
              </span>
            </div>
            <div class="book-detail-item">
              <span class="label">状态：</span>
              <span>
                <el-tag
                  :type="currentBook.status === '可借阅' ? 'success' : 'info'"
                >
                  {{ currentBook.status }}
                </el-tag>
              </span>
            </div>
            <div class="book-detail-item">
              <span class="label">借阅次数：</span>
              <span>{{ currentBook.borrowCount }}</span>
            </div>
            <div class="book-detail-actions">
              <el-button
                type="primary"
                @click="handleBorrow(currentBook)"
                v-if="
                  currentBook.status === '可借阅' ||
                  currentBook.status === 'available'
                "
              >
                借阅此书
              </el-button>
            </div>
          </div>
        </div>
        <div class="book-detail-description">
          <h3>图书简介</h3>
          <p>{{ currentBook.description || "暂无简介" }}</p>
        </div>
      </div>
    </el-dialog>

    <!-- 借阅确认对话框 -->
    <el-dialog
      v-model="borrowVisible"
      title="借阅确认"
      width="500px"
      :close-on-click-modal="false"
    >
      <div v-if="currentBook" class="borrow-form">
        <p class="borrow-title">
          您确定要借阅《{{ currentBook.bookName }}》吗？
        </p>

        <div class="borrow-options">
          <div class="borrow-option-item">
            <span class="option-label">借阅时长：</span>
            <el-select v-model="borrowDays" placeholder="请选择借阅时长">
              <el-option :value="7" label="7天" />
              <el-option :value="14" label="14天" />
              <el-option :value="30" label="30天" />
              <el-option :value="60" label="60天" />
              <el-option :value="90" label="90天" />
            </el-select>
          </div>

          <div class="borrow-option-item">
            <span class="option-label">借阅日期：</span>
            <el-date-picker
              v-model="borrowStartDate"
              type="date"
              placeholder="选择借阅日期"
              :disabled-date="disabledDate"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </div>

          <div class="borrow-info">
            <p>
              借阅起始日期：<strong>{{ borrowStartDate || "今天" }}</strong>
            </p>
            <p>
              预计归还日期：<strong>{{ getExpectedReturnDate() }}</strong>
            </p>
          </div>

          <div class="credit-notice">
            <div class="notice-title">
              <i class="el-icon-info"></i> 信用积分提醒
            </div>
            <ul class="notice-list">
              <li>按时归还图书将 <span class="positive">+2分</span></li>
              <li>逾期归还图书将 <span class="negative">-10分</span></li>
              <li>图书逾期未归还每天将 <span class="negative">-5分</span></li>
              <li>
                当前信用积分：<strong>{{
                  userStore.userInfo.credit || 0
                }}</strong>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="borrowVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="confirmBorrow"
            :loading="submitting"
          >
            确认借阅
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getBookList, getBooksByType, getBookDetail } from "@/api/book";
import { getTypeList } from "@/api/bookType";
import { borrowBook } from "@/api/borrow";
import { useUserStore } from "@/store/user";
import { useBorrowStore } from "@/store/borrow";

const userStore = useUserStore();
const borrowStore = useBorrowStore();

// 图书列表
const books = ref([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(12);
const searchKeyword = ref("");
const selectedType = ref("");
const displayMode = ref("grid"); // grid or list

// 图书分类
const bookTypes = ref([]);

// 详情对话框
const detailVisible = ref(false);
const currentBook = ref(null);

// 借阅对话框
const borrowVisible = ref(false);
const submitting = ref(false);
const borrowDays = ref(30);
const borrowStartDate = ref(null);

// 获取图书列表
const fetchBooks = async () => {
  loading.value = true;
  try {
    let res;
    if (selectedType.value) {
      res = await getBooksByType(selectedType.value);
    } else {
      res = await getBookList(searchKeyword.value);
    }

    if (res.code === 200) {
      // 处理返回的图书列表，使用全局状态管理检查借阅状态
      books.value = (res.data || []).map((book) => {
        // 如果图书已经被当前用户借阅，则强制设置状态为不可借阅
        if (borrowStore.isBookBorrowed(book.id)) {
          book.status = "已借阅";
        }
        return book;
      });
      total.value = books.value.length;
    } else {
      ElMessage.error(res.message || "获取图书列表失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取图书列表失败");
  } finally {
    loading.value = false;
  }
};

// 获取图书分类
const fetchBookTypes = async () => {
  try {
    const res = await getTypeList();
    if (res.code === 200) {
      bookTypes.value = res.data || [];
    } else {
      ElMessage.error(res.message || "获取图书分类失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取图书分类失败");
  }
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchBooks();
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = "";
  selectedType.value = "";
  handleSearch();
};

// 分类筛选变化
const handleTypeChange = () => {
  currentPage.value = 1;
  fetchBooks();
};

// 打开图书详情
const openDetail = async (book) => {
  // 先设置基本信息，防止页面闪烁
  currentBook.value = book;

  // 确保分类字段存在，防止渲染错误
  if (!currentBook.value.bookTypes) {
    currentBook.value.bookTypes = [];
  }

  detailVisible.value = true;

  // 获取完整的图书详情信息，包括分类
  try {
    const res = await getBookDetail(book.id);
    if (res.code === 200 && res.data) {
      // 调试信息
      console.log("获取到图书详情:", res.data);
      console.log("图书分类信息:", res.data.bookTypes);

      // 确保分类数据为数组
      if (!res.data.bookTypes) {
        res.data.bookTypes = [];
      }

      currentBook.value = res.data;
    }
  } catch (error) {
    console.error("获取图书详情失败:", error);
    // 错误处理，显示基本信息而不是报错
    ElMessage.error("获取图书详情失败，显示基本信息");
  }
};

// 借阅图书
const handleBorrow = (book) => {
  currentBook.value = book;

  // 重置借阅表单
  borrowDays.value = 30; // 默认30天
  borrowStartDate.value = new Date().toISOString().split("T")[0]; // 默认今天

  borrowVisible.value = true;
};

// 确认借阅
const confirmBorrow = async () => {
  if (!userStore.isLogin) {
    ElMessage.warning("请先登录");
    borrowVisible.value = false;
    return;
  }

  // 验证借阅参数
  if (!borrowDays.value) {
    ElMessage.warning("请选择借阅时长");
    return;
  }

  submitting.value = true;
  try {
    const startDate =
      borrowStartDate.value || new Date().toISOString().split("T")[0]; // 如果没选择则默认今天
    const res = await borrowBook(
      userStore.userId,
      currentBook.value.id,
      startDate,
      borrowDays.value
    );
    if (res.code === 200) {
      ElMessage.success("借阅成功");

      // 使用全局状态管理记录已借阅图书
      borrowStore.borrowBook(currentBook.value.id);

      // 关闭借阅对话框
      borrowVisible.value = false;

      // 获取最新的图书详情（包括更新后的状态和借阅次数）
      try {
        const bookRes = await getBookDetail(currentBook.value.id);
        if (bookRes.code === 200 && bookRes.data) {
          // 更新当前图书信息
          currentBook.value = bookRes.data;

          // 更新列表中对应的图书信息
          const bookIndex = books.value.findIndex(
            (book) => book.id === currentBook.value.id
          );
          if (bookIndex !== -1) {
            books.value[bookIndex] = bookRes.data;
          }
        }
      } catch (error) {
        console.error("获取最新图书信息失败:", error);
      }

      // 如果在详情页，关闭详情对话框
      if (detailVisible.value) {
        setTimeout(() => {
          detailVisible.value = false;
        }, 500);
      }

      // 延迟重新获取图书列表
      setTimeout(() => {
        fetchBooks();
      }, 1000);
    } else {
      ElMessage.error(res.message || "借阅失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("借阅失败");
  } finally {
    submitting.value = false;
  }
};

// 分页相关
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchBooks();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchBooks();
};

const disabledDate = (date) => {
  return date.getTime() < Date.now();
};

const getExpectedReturnDate = () => {
  if (borrowStartDate.value) {
    const startDate = new Date(borrowStartDate.value);
    const endDate = new Date(startDate);
    endDate.setDate(startDate.getDate() + borrowDays.value);
    return endDate.toISOString().split("T")[0];
  }
  return "今天";
};

onMounted(() => {
  // 初始化借阅状态管理
  if (userStore.isLogin) {
    borrowStore.initialize(userStore.userId);
  }

  fetchBooks();
  fetchBookTypes();
});
</script>

<style scoped>
.books-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.filter-container {
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.category-filter {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.filter-label {
  margin-right: 10px;
  font-weight: bold;
}

.book-col {
  margin-bottom: 20px;
}

.book-card {
  cursor: pointer;
  transition: all 0.3s;
  height: 320px;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.book-cover {
  height: 220px;
  position: relative;
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

.book-status {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  background-color: #909399;
  color: white;
}

.book-status.status-available {
  background-color: #67c23a;
}

.book-info {
  padding: 0 10px;
}

.book-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-author {
  font-size: 14px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.view-toggle {
  display: flex;
  justify-content: flex-end;
  margin: 20px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 图书详情样式 */
.book-detail-header {
  display: flex;
  margin-bottom: 30px;
}

.book-detail-cover {
  width: 200px;
  height: 280px;
  margin-right: 30px;
  flex-shrink: 0;
}

.book-detail-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.book-detail-info {
  flex: 1;
}

.book-detail-name {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 24px;
}

.book-detail-item {
  margin-bottom: 15px;
  font-size: 16px;
}

.book-detail-item .label {
  font-weight: bold;
  margin-right: 10px;
}

.book-detail-actions {
  margin-top: 30px;
}

.book-detail-description {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.book-detail-description h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 18px;
}

.book-detail-description p {
  line-height: 1.8;
  color: #606266;
}

/* 借阅确认对话框样式 */
.borrow-form {
  padding: 0;
}

.borrow-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
}

.borrow-options {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 20px;
}

.borrow-option-item {
  width: 100%;
  display: flex;
  align-items: center;
}

.option-label {
  display: inline-block;
  width: 100px;
  font-weight: bold;
}

.el-select,
.el-date-picker {
  width: 100%;
}

.borrow-info {
  margin-top: 20px;
  padding: 15px;
  border-top: 1px solid #ebeef5;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.borrow-info p {
  margin: 5px 0;
  font-size: 14px;
}

.credit-notice {
  margin-top: 20px;
  padding: 15px;
  border-top: 1px solid #ebeef5;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.notice-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
}

.notice-list {
  list-style: none;
  padding: 0;
}

.notice-list li {
  margin-bottom: 5px;
}

.positive {
  color: #67c23a;
}

.negative {
  color: #f56c6c;
}
</style> 