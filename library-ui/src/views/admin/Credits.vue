<template>
  <div class="credits-container">
    <div class="page-title">信用记录管理</div>

    <!-- 操作区域 -->
    <div class="operation-area">
      <el-button type="primary" @click="openCreditDialog"
        >调整用户信用</el-button
      >
    </div>

    <!-- 信用记录列表 -->
    <el-table
      :data="creditRecords"
      style="width: 100%"
      v-loading="loading"
      border
    >
      <el-table-column
        prop="username"
        label="用户名"
        width="150"
      ></el-table-column>
      <el-table-column
        prop="changeTime"
        label="时间"
        min-width="160"
      ></el-table-column>
      <el-table-column prop="creditChange" label="积分变动" width="100">
        <template #default="scope">
          <span
            :class="{
              positive: scope.row.creditChange > 0,
              negative: scope.row.creditChange < 0,
            }"
          >
            {{ scope.row.creditChange > 0 ? "+" : ""
            }}{{ scope.row.creditChange }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        prop="reason"
        label="变动原因"
        min-width="200"
      ></el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="danger"
            size="small"
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="empty-data" v-if="!loading && creditRecords.length === 0">
      <el-empty description="暂无信用记录"></el-empty>
    </div>

    <!-- 调整信用积分对话框 -->
    <el-dialog
      title="调整用户信用积分"
      v-model="creditDialogVisible"
      width="500px"
    >
      <el-form
        :model="creditForm"
        ref="creditFormRef"
        :rules="creditRules"
        label-width="100px"
      >
        <el-form-item label="用户" prop="userId">
          <el-select
            v-model="creditForm.userId"
            placeholder="请选择用户"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="user in users"
              :key="user.id"
              :label="user.username"
              :value="user.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="调整积分" prop="change">
          <el-input-number
            v-model="creditForm.change"
            :min="-100"
            :max="100"
            style="width: 100%"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input
            v-model="creditForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入调整原因"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="creditDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="submitCreditChange"
          :loading="submitting"
        >
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getCreditRecordList,
  deleteCreditRecord,
  updateUserCredit,
} from "@/api/credit";
import { getUserList } from "@/api/user";

const loading = ref(false);
const submitting = ref(false);
const creditRecords = ref([]);
const users = ref([]);
const creditDialogVisible = ref(false);
const creditFormRef = ref(null);

// 调整信用表单
const creditForm = reactive({
  userId: "",
  change: 0,
  reason: "",
});

// 表单验证规则
const creditRules = {
  userId: [{ required: true, message: "请选择用户", trigger: "change" }],
  change: [{ required: true, message: "请输入调整积分", trigger: "blur" }],
  reason: [{ required: true, message: "请输入调整原因", trigger: "blur" }],
};

// 获取信用记录列表
const fetchCreditRecords = async () => {
  loading.value = true;
  try {
    const res = await getCreditRecordList();
    if (res.code === 200) {
      creditRecords.value = res.data || [];
    } else {
      ElMessage.error(res.message || "获取信用记录列表失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取信用记录列表失败");
  } finally {
    loading.value = false;
  }
};

// 获取用户列表
const fetchUsers = async () => {
  try {
    const res = await getUserList();
    if (res.code === 200) {
      users.value = res.data || [];
    } else {
      ElMessage.error(res.message || "获取用户列表失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取用户列表失败");
  }
};

// 打开调整信用对话框
const openCreditDialog = () => {
  creditForm.userId = "";
  creditForm.change = 0;
  creditForm.reason = "";
  creditDialogVisible.value = true;
};

// 提交信用调整
const submitCreditChange = async () => {
  if (!creditFormRef.value) return;

  await creditFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        const res = await updateUserCredit(
          creditForm.userId,
          creditForm.change,
          creditForm.reason
        );

        if (res.code === 200) {
          ElMessage.success("调整信用积分成功");
          creditDialogVisible.value = false;
          fetchCreditRecords();
        } else {
          ElMessage.error(res.message || "调整信用积分失败");
        }
      } catch (error) {
        console.error(error);
        ElMessage.error("调整信用积分失败");
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 删除信用记录
const handleDelete = (row) => {
  ElMessageBox.confirm("确认删除该信用记录?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const res = await deleteCreditRecord(row.id);
        if (res.code === 200) {
          ElMessage.success("删除成功");
          fetchCreditRecords();
        } else {
          ElMessage.error(res.message || "删除失败");
        }
      } catch (error) {
        console.error(error);
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {});
};

onMounted(() => {
  fetchCreditRecords();
  fetchUsers();
});
</script>

<style scoped>
.credits-container {
  padding: 20px;
}

.page-title {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 20px;
}

.operation-area {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.positive {
  color: #67c23a;
  font-weight: bold;
}

.negative {
  color: #f56c6c;
  font-weight: bold;
}

.empty-data {
  margin-top: 20px;
  text-align: center;
}
</style> 