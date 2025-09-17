<template>
  <div class="borrows-container">
    <div class="page-title">借阅管理</div>

    <!-- 搜索和筛选区域 -->
    <div class="action-container">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入用户名或图书名称"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="statusFilter"
          placeholder="借阅状态"
          clearable
          class="ml-10"
        >
          <el-option label="全部" value="" />
          <el-option label="借阅中" value="借阅中" />
          <el-option label="已归还" value="已归还" />
          <el-option label="已逾期" value="已逾期" />
        </el-select>
        <el-button type="primary" @click="handleSearch" class="ml-10"
          >搜索</el-button
        >
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="success" @click="fetchBorrowList" class="ml-10">
          <el-icon><Refresh /></el-icon>刷新
        </el-button>
      </div>
    </div>

    <!-- 借阅列表 -->
    <el-table :data="borrowList" style="width: 100%" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="bookName" label="图书名称" min-width="150" />
      <el-table-column prop="username" label="借阅人" min-width="120" />
      <el-table-column prop="borrowTime" label="借阅时间" width="180" />
      <el-table-column prop="returnTime" label="预计归还时间" width="180" />
      <el-table-column prop="actualReturnTime" label="实际归还时间" width="180">
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
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="handleReturn(scope.row)"
            v-if="scope.row.status === '借阅中'"
          >
            归还
          </el-button>
          <el-button
            link
            type="warning"
            @click="handleExtend(scope.row)"
            v-if="scope.row.status === '借阅中'"
          >
            延期
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleLost(scope.row)"
            v-if="scope.row.status === '借阅中'"
          >
            挂失
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

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

    <!-- 挂失对话框 -->
    <el-dialog
      v-model="lostDialogVisible"
      title="图书挂失"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="lost-dialog-content">
        <p>确定要将《{{ currentBorrow?.bookName }}》设置为挂失状态吗？</p>
        <p class="warning-text">
          注意：挂失后将产生相应的赔偿费用，且无法撤销操作。
        </p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="lostDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="submitLost" :loading="submitting">
            确认挂失
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getBorrowList, returnBook, extendBorrowTime } from "@/api/borrow";
import { Refresh } from "@element-plus/icons-vue";

// 借阅列表
const borrowList = ref([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const searchKeyword = ref("");
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

// 挂失对话框
const lostDialogVisible = ref(false);

// 提交状态
const submitting = ref(false);

// 获取借阅列表
const fetchBorrowList = async () => {
  loading.value = true;
  ElMessage.info("正在从服务器获取借阅数据...");
  ElMessage.info("正在从服务器获取借阅数据...");
  try {
    const res = await getBorrowList();
    if (res.code === 200) {
      // 应用筛选
      let list = (res.data || []).map((item) => {
        // 将字段名称规范化，确保前后端字段一致
        return {
          ...item,
          // 状态转换
          status:
            item.borrowStatus === "borrowing"
              ? "借阅中"
              : item.borrowStatus === "returned"
              ? "已归还"
              : item.borrowStatus === "overdue"
              ? "已逾期"
              : item.borrowStatus,
          // 时间字段映射
          borrowTime: item.startTime,
          returnTime: item.expectTime,
          actualReturnTime: item.endTime,
        };
      });

      // 状态筛选
      if (statusFilter.value) {
        list = list.filter((item) => item.status === statusFilter.value);
      }

      // 关键词搜索
      if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase();
        list = list.filter(
          (item) =>
            (item.bookName && item.bookName.toLowerCase().includes(keyword)) ||
            (item.username && item.username.toLowerCase().includes(keyword))
        );
      }

      borrowList.value = list;
      total.value = list.length;
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

// 获取借阅状态类型
const getBorrowStatusType = (status) => {
  switch (status) {
    case "借阅中":
      return "warning";
    case "已归还":
      return "success";
    case "已逾期":
      return "danger";
    default:
      return "info";
  }
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchBorrowList();
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = "";
  statusFilter.value = "";
  handleSearch();
};

// 归还图书
const handleReturn = (row) => {
  ElMessageBox.confirm(`确定要归还《${row.bookName}》吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        // 使用正确的ID字段
        const borrowId = row.id.toString();
        const res = await returnBook(borrowId);
        if (res.code === 200) {
          ElMessage.success("归还成功");
          fetchBorrowList();
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
    // 确保ID是字符串类型
    const borrowId = extendForm.id.toString();
    const res = await extendBorrowTime(borrowId, extendForm.days);
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

// 挂失图书
const handleLost = (row) => {
  currentBorrow.value = row;
  lostDialogVisible.value = true;
};

// 提交挂失
const submitLost = async () => {
  submitting.value = true;
  try {
    // 模拟API调用，实际项目中应该通过API处理挂失
    setTimeout(() => {
      ElMessage.success("图书挂失成功");
      lostDialogVisible.value = false;
      submitting.value = false;
      fetchBorrowList();
    }, 1000);
  } catch (error) {
    console.error(error);
    ElMessage.error("图书挂失失败");
    submitting.value = false;
  }
};

// 分页相关
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchBorrowList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchBorrowList();
};

onMounted(() => {
  fetchBorrowList();
});
</script>

<style scoped>
.borrows-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.action-container {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.warning-text {
  color: #f56c6c;
  font-size: 14px;
}
</style> 