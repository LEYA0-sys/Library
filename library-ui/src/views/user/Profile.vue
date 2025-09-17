<template>
  <div class="profile-container">
    <div class="page-title">个人信息</div>

    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
          <el-button type="primary" @click="handleEdit">编辑信息</el-button>
        </div>
      </template>

      <div class="profile-content">
        <div class="avatar-container">
          <el-avatar
            :size="100"
            :src="userInfo.avatar || '/default-avatar.png'"
          />
        </div>

        <div class="info-container">
          <div class="info-item">
            <span class="label">用户名：</span>
            <span class="value">{{ userInfo.username }}</span>
          </div>
          <div class="info-item">
            <span class="label">邮箱：</span>
            <span class="value">{{ userInfo.email }}</span>
          </div>
          <div class="info-item">
            <span class="label">手机号：</span>
            <span class="value">{{ userInfo.mobile || "未设置" }}</span>
          </div>
          <div class="info-item">
            <span class="label">注册时间：</span>
            <span class="value">{{ userInfo.createTime }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>借阅统计</span>
        </div>
      </template>

      <div class="stats-container">
        <div class="stat-item">
          <div class="stat-value">{{ stats.totalBorrows }}</div>
          <div class="stat-label">总借阅量</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.currentBorrows }}</div>
          <div class="stat-label">当前借阅</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.overdueBorrows }}</div>
          <div class="stat-label">逾期未还</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ userInfo.readingCount || 0 }}</div>
          <div class="stat-label">在读书数</div>
        </div>
      </div>

      <div class="recent-borrow">
        <div class="recent-title">最近借阅</div>
        <el-table :data="recentBorrows" style="width: 100%">
          <el-table-column prop="bookName" label="图书名称" min-width="150" />
          <el-table-column prop="startTime" label="借阅时间" width="180" />
          <el-table-column prop="borrowStatus" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getBorrowStatusType(scope.row.borrowStatus)">
                {{ getStatusText(scope.row.borrowStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button
                link
                type="primary"
                @click="viewBorrowDetail(scope.row)"
              >
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="view-more">
          <el-button type="primary" text @click="goToMyBorrows">
            查看全部借阅记录
            <el-icon class="el-icon--right">
              <ArrowRight />
            </el-icon>
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>账户安全</span>
        </div>
      </template>

      <div class="security-container">
        <div class="security-item">
          <div class="security-info">
            <div class="security-title">登录密码</div>
            <div class="security-desc">
              建议您定期更换密码，设置一个包含字母、数字和特殊字符的密码会更安全
            </div>
          </div>
          <el-button type="primary" @click="handleChangePassword"
            >修改密码</el-button
          >
        </div>

        <div class="security-item">
          <div class="security-info">
            <div class="security-title">绑定邮箱</div>
            <div class="security-desc">
              {{ userInfo.email || "您还未绑定邮箱，绑定后可用于找回密码" }}
            </div>
          </div>
          <el-button type="primary" @click="handleChangeEmail">
            {{ userInfo.email ? "修改邮箱" : "绑定邮箱" }}
          </el-button>
        </div>

        <div class="security-item">
          <div class="security-info">
            <div class="security-title">绑定手机</div>
            <div class="security-desc">
              {{
                userInfo.mobile ||
                "您还未绑定手机，绑定后可用于找回密码和接收借阅通知"
              }}
            </div>
          </div>
          <el-button type="primary" @click="handleChangeMobile">
            {{ userInfo.mobile ? "修改手机" : "绑定手机" }}
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 编辑用户信息对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑个人信息"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" disabled />
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
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="submitUserForm"
            :loading="submitting"
          >
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="submitPasswordForm"
            :loading="submitting"
          >
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getUserInfo, updateUserInfo, changePassword } from "@/api/user";
import { getBorrowList, getUserBorrows } from "@/api/borrow";
import { useUserStore } from "@/store/user";

const router = useRouter();
const userStore = useUserStore();

// 用户信息
const userInfo = ref({
  id: "",
  username: "",
  email: "",
  mobile: "",
  avatar: "",
  createTime: "",
});

// 借阅统计
const stats = ref({
  totalBorrows: 0,
  currentBorrows: 0,
  overdueBorrows: 0,
});

// 最近借阅
const recentBorrows = ref([]);
const allBorrows = ref([]);

// 编辑用户信息
const editDialogVisible = ref(false);
const userFormRef = ref(null);
const userForm = reactive({
  id: "",
  email: "",
  mobile: "",
});

// 修改密码
const passwordDialogVisible = ref(false);
const passwordFormRef = ref(null);
const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

// 提交状态
const submitting = ref(false);

// 验证规则
const userRules = {
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" },
  ],
  mobile: [
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
  } else if (value !== passwordForm.newPassword) {
    callback(new Error("两次输入密码不一致"));
  } else {
    callback();
  }
};

// 密码验证规则
const passwordRules = {
  oldPassword: [{ required: true, message: "请输入当前密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度应为6到20个字符", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请确认新密码", trigger: "blur" },
    { validator: validatePass, trigger: "blur" },
  ],
};

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const res = await getUserInfo(userStore.userId);
    if (res.code === 200) {
      userInfo.value = res.data;
    } else {
      ElMessage.error(res.message || "获取用户信息失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取用户信息失败");
  }
};

// 获取借阅统计
const fetchBorrowStats = async () => {
  try {
    console.log("开始获取借阅数据，用户ID:", userStore.userId);
    // 使用专门的API获取用户借阅记录
    const res = await getUserBorrows(userStore.userId);
    if (res.code === 200) {
      console.log("用户借阅数据:", res.data);
      allBorrows.value = res.data || [];

      // 检查借阅记录中的图书名称
      if (allBorrows.value.length > 0) {
        console.log(
          "第一条借阅记录详细信息:",
          JSON.stringify(allBorrows.value[0])
        );
        allBorrows.value.forEach((item, index) => {
          console.log(
            `借阅记录${index + 1} - 图书ID: ${item.bookId}, 图书名称: ${
              item.bookName
            }`
          );
        });
      }

      // 统计数据
      stats.value.totalBorrows = allBorrows.value.length;
      stats.value.currentBorrows = allBorrows.value.filter(
        (item) => item.borrowStatus === "borrowing"
      ).length;
      stats.value.overdueBorrows = allBorrows.value.filter(
        (item) => item.borrowStatus === "overdue"
      ).length;

      // 最近借阅（最多5条）
      recentBorrows.value = allBorrows.value
        .sort((a, b) => new Date(b.startTime || 0) - new Date(a.startTime || 0))
        .slice(0, 5);

      console.log("最近借阅详情:", recentBorrows.value);
    } else {
      ElMessage.error(res.message || "获取借阅统计失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取借阅统计失败");
  }
};

// 获取借阅状态类型
const getBorrowStatusType = (status) => {
  switch (status) {
    case "borrowing":
      return "warning";
    case "returned":
      return "success";
    case "overdue":
      return "danger";
    default:
      return "info";
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case "borrowing":
      return "借阅中";
    case "returned":
      return "已归还";
    case "overdue":
      return "逾期未还";
    default:
      return "未知状态";
  }
};

// 编辑用户信息
const handleEdit = () => {
  userForm.id = userInfo.value.id;
  userForm.email = userInfo.value.email;
  userForm.mobile = userInfo.value.mobile;
  editDialogVisible.value = true;
};

// 提交用户信息
const submitUserForm = async () => {
  if (!userFormRef.value) return;

  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        console.log("提交用户信息:", userForm);
        const res = await updateUserInfo(userForm);
        console.log("更新结果:", res);
        if (res.code === 200) {
          ElMessage.success("保存成功");
          editDialogVisible.value = false;

          // 更新本地用户信息
          const updatedUserInfo = {
            ...userStore.userInfo,
            email: userForm.email,
            mobile: userForm.mobile,
          };
          localStorage.setItem("userInfo", JSON.stringify(updatedUserInfo));
          userStore.userInfo = updatedUserInfo;

          fetchUserInfo();
        } else {
          ElMessage.error(res.message || "保存失败");
        }
      } catch (error) {
        console.error(error);
        ElMessage.error("保存失败");
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 修改密码
const handleChangePassword = () => {
  passwordForm.oldPassword = "";
  passwordForm.newPassword = "";
  passwordForm.confirmPassword = "";
  passwordDialogVisible.value = true;
};

// 提交密码修改
const submitPasswordForm = async () => {
  if (!passwordFormRef.value) return;

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        const res = await changePassword(
          userStore.userId,
          passwordForm.oldPassword,
          passwordForm.newPassword
        );
        if (res.code === 200) {
          ElMessage.success("密码修改成功");
          passwordDialogVisible.value = false;
        } else {
          ElMessage.error(res.message || "密码修改失败");
        }
      } catch (error) {
        console.error(error);
        ElMessage.error("密码修改失败");
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 修改邮箱
const handleChangeEmail = () => {
  handleEdit();
};

// 修改手机
const handleChangeMobile = () => {
  handleEdit();
};

// 查看借阅详情
const viewBorrowDetail = (row) => {
  router.push({
    path: "/user/my-borrows",
    query: { id: row.id },
  });
};

// 查看全部借阅记录
const goToMyBorrows = () => {
  router.push("/user/my-borrows");
};

onMounted(() => {
  fetchUserInfo();
  fetchBorrowStats();
});
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.profile-content {
  display: flex;
  padding: 20px 0;
}

.avatar-container {
  margin-right: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.info-container {
  flex: 1;
}

.info-item {
  margin-bottom: 15px;
  font-size: 16px;
}

.info-item .label {
  display: inline-block;
  width: 100px;
  font-weight: bold;
}

.stats-container {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.recent-borrow {
  margin-top: 20px;
}

.recent-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
}

.view-more {
  margin-top: 15px;
  text-align: right;
}

.security-container {
  padding: 10px 0;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.security-item:last-child {
  border-bottom: none;
}

.security-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.security-desc {
  font-size: 14px;
  color: #909399;
}
</style> 