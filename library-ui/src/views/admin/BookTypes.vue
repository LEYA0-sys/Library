<template>
  <div class="book-types-container">
    <div class="page-title">图书分类管理</div>

    <!-- 搜索和操作区域 -->
    <div class="action-container">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加分类
      </el-button>
    </div>

    <!-- 分类列表 -->
    <el-table :data="typeList" style="width: 100%" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="typeName" label="分类名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="handleEdit(scope.row)"
            >编辑</el-button
          >
          <el-button link type="danger" @click="handleDelete(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 分类表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加分类' : '编辑分类'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="typeFormRef"
        :model="typeForm"
        :rules="typeRules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="typeName">
          <el-input v-model="typeForm.typeName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="typeForm.description"
            type="textarea"
            placeholder="请输入描述"
            :rows="4"
          />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getTypeList,
  createType,
  updateType,
  deleteType,
} from "@/api/bookType";

// 分类列表
const typeList = ref([]);
const loading = ref(false);

// 对话框相关
const dialogVisible = ref(false);
const dialogType = ref("add"); // add, edit
const submitting = ref(false);
const typeFormRef = ref(null);
const typeForm = reactive({
  id: "",
  typeName: "",
  description: "",
});

// 表单验证规则
const typeRules = {
  typeName: [{ required: true, message: "请输入分类名称", trigger: "blur" }],
};

// 获取分类列表
const fetchTypeList = async () => {
  loading.value = true;
  try {
    const res = await getTypeList();
    if (res.code === 200) {
      typeList.value = res.data || [];
    } else {
      ElMessage.error(res.message || "获取分类列表失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取分类列表失败");
  } finally {
    loading.value = false;
  }
};

// 添加分类
const handleAdd = () => {
  resetForm();
  dialogType.value = "add";
  dialogVisible.value = true;
};

// 编辑分类
const handleEdit = (row) => {
  dialogType.value = "edit";
  resetForm();
  // 复制数据到表单
  Object.keys(typeForm).forEach((key) => {
    if (key in row) {
      typeForm[key] = row[key];
    }
  });
  dialogVisible.value = true;
};

// 删除分类
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除分类 "${row.typeName}" 吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const res = await deleteType(row.id);
        if (res.code === 200) {
          ElMessage.success("删除成功");
          fetchTypeList();
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

// 提交表单
const submitForm = async () => {
  if (!typeFormRef.value) return;

  await typeFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        const api = dialogType.value === "add" ? createType : updateType;
        const res = await api(typeForm);
        if (res.code === 200) {
          ElMessage.success(
            dialogType.value === "add" ? "添加成功" : "更新成功"
          );
          dialogVisible.value = false;
          fetchTypeList();
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

// 重置表单
const resetForm = () => {
  if (typeFormRef.value) {
    typeFormRef.value.resetFields();
  }
  typeForm.id = "";
  typeForm.typeName = "";
  typeForm.description = "";
};

onMounted(() => {
  fetchTypeList();
});
</script>

<style scoped>
.book-types-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.action-container {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}
</style> 