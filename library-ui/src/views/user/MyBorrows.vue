<template>
  <div class="my-borrows-container">
    <div class="page-title">我的借阅</div>

    <!-- 筛选区域 -->
    <div class="filter-container">
      <div class="filter-left">
        <el-select
          v-model="statusFilter"
          placeholder="借阅状态"
          clearable
          @change="handleFilterChange"
        >
          <el-option label="全部" value="" />
          <el-option label="借阅中" value="借阅中" />
          <el-option label="已归还" value="已归还" />
          <el-option label="已逾期" value="已逾期" />
        </el-select>
      </div>
      <div class="filter-right">
        <el-button type="primary" @click="fetchBorrowList" :loading="loading">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>
    </div>

    <!-- 借阅列表 -->
    <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick">
      <el-tab-pane label="全部借阅" name="all">
        <el-table
          :data="filteredBorrows"
          style="width: 100%"
          v-loading="loading"
          border
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="bookName" label="图书名称" min-width="150" />
          <el-table-column prop="borrowTime" label="借阅时间" width="180" />
          <el-table-column prop="returnTime" label="预计归还时间" width="180" />
          <el-table-column
            prop="actualReturnTime"
            label="实际归还时间"
            width="180"
          >
            <template #default="scope">
              {{ scope.row.actualReturnTime || "-" }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getBorrowStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <el-button
                link
                type="primary"
                @click="handleReturn(scope.row)"
                v-if="
                  scope.row.status === '借阅中' ||
                  scope.row.borrowStatus === 'borrowing'
                "
              >
                归还
              </el-button>
              <el-button
                link
                type="warning"
                @click="handleExtend(scope.row)"
                v-if="
                  scope.row.status === '借阅中' ||
                  scope.row.borrowStatus === 'borrowing'
                "
              >
                延期
              </el-button>
              <el-button link type="primary" @click="handleViewBook(scope.row)">
                查看图书
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 无数据 -->
        <el-empty
          v-if="filteredBorrows.length === 0"
          description="暂无借阅记录"
          :image-size="200"
        />
      </el-tab-pane>

      <el-tab-pane label="借阅中" name="borrowing">
        <el-table
          :data="borrowingList"
          style="width: 100%"
          v-loading="loading"
          border
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="bookName" label="图书名称" min-width="150" />
          <el-table-column prop="borrowTime" label="借阅时间" width="180" />
          <el-table-column prop="returnTime" label="预计归还时间" width="180" />
          <el-table-column label="剩余天数" width="100">
            <template #default="scope">
              <el-tag :type="getRemainingDaysType(scope.row.remainingDays)">
                {{ scope.row.remainingDays }}天
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <el-button
                link
                type="primary"
                @click="handleReturn(scope.row)"
                v-if="
                  scope.row.status === '借阅中' ||
                  scope.row.borrowStatus === 'borrowing'
                "
              >
                归还
              </el-button>
              <el-button
                link
                type="warning"
                @click="handleExtend(scope.row)"
                v-if="
                  scope.row.status === '借阅中' ||
                  scope.row.borrowStatus === 'borrowing'
                "
              >
                延期
              </el-button>
              <el-button link type="primary" @click="handleViewBook(scope.row)">
                查看图书
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 无数据 -->
        <el-empty
          v-if="borrowingList.length === 0"
          description="暂无借阅中的图书"
          :image-size="200"
        />
      </el-tab-pane>

      <el-tab-pane label="已归还" name="returned">
        <el-table
          :data="returnedList"
          style="width: 100%"
          v-loading="loading"
          border
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="bookName" label="图书名称" min-width="150" />
          <el-table-column prop="borrowTime" label="借阅时间" width="180" />
          <el-table-column prop="returnTime" label="预计归还时间" width="180" />
          <el-table-column
            prop="actualReturnTime"
            label="实际归还时间"
            width="180"
          />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="handleViewBook(scope.row)">
                查看图书
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 无数据 -->
        <el-empty
          v-if="returnedList.length === 0"
          description="暂无已归还图书记录"
          :image-size="200"
        />
      </el-tab-pane>
    </el-tabs>

    <!-- 延期对话框 -->
    <el-dialog
      v-model="extendDialogVisible"
      title="延长借阅时间"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form ref="extendFormRef" :model="extendForm" label-width="100px">
        <el-form-item label="图书名称">
          <div>{{ currentBorrow?.bookName }}</div>
        </el-form-item>
        <el-form-item label="当前到期时间">
          <div>{{ currentBorrow?.returnTime }}</div>
        </el-form-item>
        <el-form-item label="延长天数" prop="days">
          <el-input-number v-model="extendForm.days" :min="1" :max="30" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="extendDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitExtend" :loading="submitting">
            确认延长
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 信用提示 -->
    <el-card class="credit-tips-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span><i class="el-icon-info"></i> 信用积分提示</span>
        </div>
      </template>

      <div class="credit-tips">
        <p>亲爱的读者，请注意维护您的信用积分：</p>
        <ul>
          <li>
            按时归还图书可获得 <span class="positive">+2分</span> 信用积分
          </li>
          <li>
            逾期归还图书将扣除 <span class="negative">-10分</span> 信用积分
          </li>
          <li>
            系统每天检查，每本逾期未归还的图书将扣除
            <span class="negative">-5分</span> 信用积分
          </li>
          <li>信用积分低于60分可能会限制您的借阅权限</li>
        </ul>
        <p>
          当前信用积分：<strong>{{ userStore.userInfo.credit || 0 }}</strong>
        </p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { getBorrowList, returnBook, extendBorrowTime } from "@/api/borrow";
import { getBookDetail } from "@/api/book";
import { useUserStore } from "@/store/user";
import { useBorrowStore } from "@/store/borrow";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const borrowStore = useBorrowStore();

// 借阅列表
const borrows = ref([]);
const loading = ref(false);
const activeTab = ref("all");
const statusFilter = ref("");

// 当前操作的借阅记录
const currentBorrow = ref(null);

// 延期对话框
const extendDialogVisible = ref(false);
const extendFormRef = ref(null);
const extendForm = reactive({
  id: "",
  days: 7,
});

// 提交状态
const submitting = ref(false);

// 计算借阅中和已归还的列表
const borrowingList = computed(() => {
  return borrows.value.filter(
    (item) => item.status === "借阅中" || item.borrowStatus === "borrowing"
  );
});

const returnedList = computed(() => {
  return borrows.value.filter(
    (item) => item.status === "已归还" || item.borrowStatus === "returned"
  );
});

// 筛选后的借阅列表
const filteredBorrows = computed(() => {
  if (!statusFilter.value) {
    return borrows.value;
  }
  if (statusFilter.value === "借阅中") {
    return borrows.value.filter(
      (item) => item.status === "借阅中" || item.borrowStatus === "borrowing"
    );
  }
  if (statusFilter.value === "已归还") {
    return borrows.value.filter(
      (item) => item.status === "已归还" || item.borrowStatus === "returned"
    );
  }
  return borrows.value.filter(
    (item) =>
      item.status === statusFilter.value ||
      item.borrowStatus === statusFilter.value
  );
});

// 获取借阅列表
const fetchBorrowList = async () => {
  loading.value = true;
  try {
    const res = await getBorrowList();
    if (res.code === 200) {
      console.log("原始借阅数据:", res.data);

      // 只显示当前用户的借阅记录，并进行数据处理
      const filteredBorrows = (res.data || [])
        .filter((item) => item.userId == userStore.userId)
        .map((item) => {
          // 将字段名称规范化，确保前后端字段一致
          const processedItem = {
            ...item,
            // 状态转换 - 明确转换所有可能的状态值
            status:
              item.borrowStatus === "borrowing"
                ? "借阅中"
                : item.borrowStatus === "returned"
                ? "已归还"
                : item.borrowStatus === "overdue"
                ? "已逾期"
                : item.borrowStatus || "未知状态",
            // 时间字段映射 - 确保所有时间字段都有正确赋值
            borrowTime: item.startTime || "",
            returnTime: item.expectTime || "",
            actualReturnTime: item.endTime || "",
          };

          // 计算剩余天数
          if (
            (processedItem.status === "借阅中" ||
              item.borrowStatus === "borrowing") &&
            processedItem.returnTime
          ) {
            try {
              const returnDate = new Date(processedItem.returnTime);
              const today = new Date();
              const diffTime = returnDate.getTime() - today.getTime();
              const diffDays = Math.ceil(diffTime / (1000 * 3600 * 24));
              processedItem.remainingDays = diffDays > 0 ? diffDays : 0;
            } catch (err) {
              console.error("计算剩余天数出错:", err);
              processedItem.remainingDays = 0;
            }
          }

          console.log("处理后的借阅项:", processedItem);
          return processedItem;
        });

      borrows.value = filteredBorrows;

      // 添加调试信息
      console.log("处理后的所有借阅记录:", borrows.value);

      // 异步加载每本书的详细信息
      for (const borrow of filteredBorrows) {
        if (!borrow.bookName && borrow.bookId) {
          fetchBookDetail(borrow);
        }
      }
    } else {
      ElMessage.error(res.message || "获取借阅列表失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取借阅列表失败");
  } finally {
    loading.value = false;
  }
};

// 获取图书详情
const fetchBookDetail = async (borrow) => {
  try {
    const res = await getBookDetail(borrow.bookId);
    if (res.code === 200 && res.data) {
      // 更新借阅记录中的图书信息
      const index = borrows.value.findIndex((item) => item.id === borrow.id);
      if (index !== -1) {
        borrows.value[index].bookName = res.data.bookName;
        borrows.value[index].cover = res.data.cover;
      }
    }
  } catch (error) {
    console.error("获取图书详情失败:", error);
  }
};

// 获取借阅状态类型
const getBorrowStatusType = (status) => {
  switch (status) {
    case "借阅中":
    case "borrowing":
      return "warning";
    case "已归还":
    case "returned":
      return "success";
    case "已逾期":
    case "overdue":
      return "danger";
    default:
      return "info";
  }
};

// 获取剩余天数标签类型
const getRemainingDaysType = (days) => {
  if (days <= 0) {
    return "danger";
  } else if (days <= 3) {
    return "warning";
  } else {
    return "success";
  }
};

// 筛选变化
const handleFilterChange = () => {
  // 当筛选条件变化时，切换到全部标签页
  activeTab.value = "all";
};

// 标签页切换
const handleTabClick = (tab) => {
  if (tab.paneName !== "all") {
    // 切换到特定标签页时，清除筛选条件
    statusFilter.value = "";
  }
};

// 归还图书
const handleReturn = (row) => {
  if (!row.bookName) {
    ElMessage.warning("无法获取图书信息");
    return;
  }

  ElMessageBox.confirm(`确定要归还《${row.bookName}》吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const res = await returnBook(row.id);
        if (res.code === 200) {
          ElMessage.success("归还成功");

          // 更新全局借阅状态
          borrowStore.returnBook(row.bookId);

          // 直接在当前列表中更新状态，避免重新加载
          const index = borrows.value.findIndex((item) => item.id === row.id);
          if (index !== -1) {
            borrows.value[index].status = "已归还";
            borrows.value[index].borrowStatus = "returned";
            borrows.value[index].actualReturnTime = new Date()
              .toISOString()
              .slice(0, 19)
              .replace("T", " ");
          }

          // 延迟一点时间后获取最新数据，确保后端处理完成
          setTimeout(() => {
            fetchBorrowList();
          }, 500);
        } else {
          ElMessage.error(res.message || "归还失败");
        }
      } catch (error) {
        console.error(error);
        ElMessage.error("归还失败");
      }
    })
    .catch(() => {});
};

// 延长借阅时间
const handleExtend = (row) => {
  currentBorrow.value = row;
  extendForm.id = row.id;
  extendForm.days = 7;
  extendDialogVisible.value = true;
};

// 提交延期
const submitExtend = async () => {
  submitting.value = true;
  try {
    const res = await extendBorrowTime(extendForm.id, extendForm.days);
    if (res.code === 200) {
      ElMessage.success("延长借阅时间成功");
      extendDialogVisible.value = false;
      fetchBorrowList();
    } else {
      ElMessage.error(res.message || "延长借阅时间失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("延长借阅时间失败");
  } finally {
    submitting.value = false;
  }
};

// 查看图书
const handleViewBook = (row) => {
  router.push({
    path: "/user/books",
    query: { id: row.bookId },
  });
};

onMounted(() => {
  // 初始化借阅状态管理
  if (userStore.isLogin) {
    borrowStore.initialize(userStore.userId);
  }

  fetchBorrowList();

  // 检查是否有延期操作参数
  if (route.query.id && route.query.action === "extend") {
    const borrowId = route.query.id;
    // 获取数据后再处理延期操作
    setTimeout(() => {
      const borrow = borrows.value.find((item) => item.id == borrowId);
      if (borrow && borrow.status === "借阅中") {
        handleExtend(borrow);
      }
    }, 500);
  }
});
</script>

<style scoped>
.my-borrows-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.filter-container {
  margin-bottom: 20px;
}

.filter-left {
  float: left;
}

.filter-right {
  float: right;
}

.credit-tips-card {
  margin-top: 20px;
  padding: 10px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.credit-tips {
  margin-top: 10px;
}

.positive {
  color: #67c23a;
}

.negative {
  color: #f56c6c;
}
</style> 