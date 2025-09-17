<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" @click="handleEdit">编辑信息</el-button>
        </div>
      </template>

      <div class="profile-content">
        <div class="avatar-container">
          <el-avatar :size="100" :src="userInfo.avatar || '/avatar.png'" />
          <div class="upload-btn" v-if="isEditing">
            <el-upload
              class="avatar-uploader"
              action="/api/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <el-button size="small">更换头像</el-button>
            </el-upload>
          </div>
        </div>

        <div class="info-container">
          <el-form
            ref="formRef"
            :model="form"
            label-width="100px"
            :disabled="!isEditing"
          >
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="form.realName" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item label="电话">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="注册时间">
              <el-input v-model="form.createTime" disabled />
            </el-form-item>

            <el-form-item v-if="isEditing">
              <div class="form-actions">
                <el-button @click="cancelEdit">取消</el-button>
                <el-button
                  type="primary"
                  @click="submitForm"
                  :loading="submitting"
                  >保存</el-button
                >
              </div>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>

    <el-card class="password-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>

      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入旧密码"
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
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="changePassword"
            :loading="changingPassword"
          >
            修改密码
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useUserStore } from "@/store/user";
import { ElMessage } from "element-plus";
import {
  getUserInfo,
  updateUserInfo,
  changePassword as apiChangePassword,
} from "@/api/user";

const userStore = useUserStore();
const userInfo = computed(() => userStore.userInfo || {});

const isEditing = ref(false);
const submitting = ref(false);
const changingPassword = ref(false);
const formRef = ref(null);
const passwordFormRef = ref(null);

// 个人信息表单
const form = reactive({
  id: "",
  username: "",
  realName: "",
  email: "",
  phone: "",
  avatar: "",
  createTime: "",
});

// 密码表单
const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

// 密码表单验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: "请输入旧密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6位", trigger: "blur" },
  ],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6位", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请确认新密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6位", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
};

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    // 直接使用 store 中的用户信息
    const storeUserInfo = userStore.userInfo;
    if (storeUserInfo && storeUserInfo.id) {
      Object.keys(form).forEach((key) => {
        if (key in storeUserInfo) {
          form[key] = storeUserInfo[key];
        }
      });
    } else {
      // 如果 store 中没有完整信息，则从 API 获取
      const res = await getUserInfo();
      if (res.code === 200 && res.data) {
        const apiUserInfo = res.data;
        Object.keys(form).forEach((key) => {
          if (key in apiUserInfo) {
            form[key] = apiUserInfo[key];
          }
        });
        // 更新 store 中的用户信息
        userStore.setUserInfo(apiUserInfo);
      }
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
    ElMessage.error("获取用户信息失败");
  }
};

// 进入编辑模式
const handleEdit = () => {
  isEditing.value = true;
};

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false;
  fetchUserInfo(); // 重新获取信息，恢复原始数据
};

// 提交表单
const submitForm = async () => {
  submitting.value = true;
  try {
    const updateData = {
      id: form.id,
      realName: form.realName,
      email: form.email,
      phone: form.phone,
      avatar: form.avatar,
    };

    const res = await updateUserInfo(updateData);
    if (res.code === 200) {
      ElMessage.success("更新成功");
      isEditing.value = false;

      // 更新 store 中的用户信息
      userStore.setUserInfo({
        ...userStore.userInfo,
        ...updateData,
      });
    } else {
      ElMessage.error(res.message || "更新失败");
    }
  } catch (error) {
    console.error("更新用户信息失败:", error);
    ElMessage.error("更新用户信息失败");
  } finally {
    submitting.value = false;
  }
};

// 修改密码
const changePassword = async () => {
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changingPassword.value = true;
      try {
        const res = await apiChangePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword,
        });

        if (res.code === 200) {
          ElMessage.success("密码修改成功");
          // 清空表单
          passwordForm.oldPassword = "";
          passwordForm.newPassword = "";
          passwordForm.confirmPassword = "";
          passwordFormRef.value.resetFields();
        } else {
          ElMessage.error(res.message || "密码修改失败");
        }
      } catch (error) {
        console.error("密码修改失败:", error);
        ElMessage.error("密码修改失败");
      } finally {
        changingPassword.value = false;
      }
    }
  });
};

// 头像上传成功
const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    form.avatar = response.data;
    // 更新 store 中的头像
    userStore.setUserInfo({
      ...userStore.userInfo,
      avatar: response.data,
    });
    ElMessage.success("头像上传成功");
  } else {
    ElMessage.error(response.message || "头像上传失败");
  }
};

// 头像上传前校验
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith("image/");
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error("只能上传图片文件!");
  }
  if (!isLt2M) {
    ElMessage.error("图片大小不能超过 2MB!");
  }

  return isImage && isLt2M;
};

onMounted(() => {
  fetchUserInfo();
});
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
}

.profile-card,
.password-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-content {
  display: flex;
  align-items: flex-start;
  padding: 10px 0;
}

.avatar-container {
  text-align: center;
  margin-right: 30px;
}

.upload-btn {
  margin-top: 10px;
}

.info-container {
  flex: 1;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
}
</style> 