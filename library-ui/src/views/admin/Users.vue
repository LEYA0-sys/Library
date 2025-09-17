<template>
  <div class="users-container">
    <div class="page-title">用户管理</div>

    <!-- 搜索和操作区域 -->
    <div class="action-container">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入用户名或邮箱"
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
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加用户
      </el-button>
    </div>

    <!-- 用户列表 -->
    <el-table :data="userList" style="width: 100%" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="mobile" label="手机号" />
      <el-table-column prop="createTime" label="注册时间" width="180" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="handleEdit(scope.row)"
            >编辑</el-button
          >
          <el-button link type="danger" @click="handleDelete(scope.row)"
            >删除</el-button
          >
          <el-button link type="warning" @click="handleReset(scope.row)"
            >重置密码</el-button
          >
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

    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
            :disabled="dialogType === 'edit'"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="userForm.mobile" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            {{ dialogType === "add" ? "添加" : "更新" }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog
      v-model="resetPasswordVisible"
      title="重置密码"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="resetFormRef"
        :model="resetForm"
        :rules="resetRules"
        label-width="100px"
      >
        <el-form-item label="新密码" prop="password">
          <el-input
            v-model="resetForm.password"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="resetForm.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetPasswordVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="submitResetForm"
            :loading="submitting"
          >
            确认重置
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getUserList, createUser, updateUser, deleteUser } from "@/api/user";

// 用户列表
const userList = ref([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const searchKeyword = ref("");

// 用户表单对话框
const dialogVisible = ref(false);
const dialogType = ref("add"); // add, edit
const submitting = ref(false);
const userFormRef = ref(null);
const userForm = reactive({
  id: "",
  username: "",
  password: "",
  email: "",
  mobile: "",
});

// 重置密码对话框
const resetPasswordVisible = ref(false);
const resetFormRef = ref(null);
const resetForm = reactive({
  userId: "",
  password: "",
  confirmPassword: "",
});
const currentUser = ref(null);

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 20, message: "用户名长度应为3到20个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度应为6到20个字符", trigger: "blur" },
  ],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" },
  ],
  mobile: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
};

// 密码确认校验
const validatePass = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== resetForm.password) {
    callback(new Error("两次输入密码不一致"));
  } else {
    callback();
  }
};

// 重置密码表单验证规则
const resetRules = {
  password: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度应为6到20个字符", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请确认新密码", trigger: "blur" },
    { validator: validatePass, trigger: "blur" },
  ],
};

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true;
  try {
    const res = await getUserList(searchKeyword.value);
    if (res.code === 200) {
      userList.value = res.data || [];
      total.value = userList.value.length;
    } else {
      ElMessage.error(res.message || "获取用户列表失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取用户列表失败");
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchUserList();
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = "";
  handleSearch();
};

// 添加用户
const handleAdd = () => {
  resetForm();
  dialogType.value = "add";
  dialogVisible.value = true;
};

// 编辑用户
const handleEdit = (row) => {
  dialogType.value = "edit";
  resetUserForm();
  // 复制数据到表单
  Object.keys(userForm).forEach((key) => {
    if (key in row && key !== "password") {
      userForm[key] = row[key];
    }
  });
  dialogVisible.value = true;
};

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const res = await deleteUser(row.id);
        if (res.code === 200) {
          ElMessage.success("删除成功");
          fetchUserList();
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

// 重置密码
const handleReset = (row) => {
  currentUser.value = row;
  resetResetForm();
  resetForm.userId = row.id;
  resetPasswordVisible.value = true;
};

// 提交用户表单
const submitForm = async () => {
  if (!userFormRef.value) return;

  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        const api = dialogType.value === "add" ? createUser : updateUser;
        const res = await api(userForm);
        if (res.code === 200) {
          ElMessage.success(
            dialogType.value === "add" ? "添加成功" : "更新成功"
          );
          dialogVisible.value = false;
          fetchUserList();
        } else {
          ElMessage.error(
            res.message ||
              (dialogType.value === "add" ? "添加失败" : "更新失败")
          );
        }
      } catch (error) {
        console.error(error);
        ElMessage.error(dialogType.value === "add" ? "添加失败" : "更新失败");
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 提交重置密码表单
const submitResetForm = async () => {
  if (!resetFormRef.value) return;

  await resetFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        // 模拟API调用，实际项目中应该通过API修改密码
        setTimeout(() => {
          ElMessage.success("密码重置成功");
          resetPasswordVisible.value = false;
          submitting.value = false;
        }, 1000);
      } catch (error) {
        console.error(error);
        ElMessage.error("密码重置失败");
        submitting.value = false;
      }
    }
  });
};

// 重置用户表单
const resetUserForm = () => {
  if (userFormRef.value) {
    userFormRef.value.resetFields();
  }
  userForm.id = "";
  userForm.username = "";
  userForm.password = "";
  userForm.email = "";
  userForm.mobile = "";
};

// 重置重置密码表单
const resetResetForm = () => {
  if (resetFormRef.value) {
    resetFormRef.value.resetFields();
  }
  resetForm.userId = "";
  resetForm.password = "";
  resetForm.confirmPassword = "";
};

// 分页相关
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchUserList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchUserList();
};

onMounted(() => {
  fetchUserList();
});
</script>

<style scoped>
.users-container {
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
</style> 