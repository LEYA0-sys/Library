<template>
  <div class="books-container">
    <div class="page-title">图书管理</div>

    <!-- 搜索和操作区域 -->
    <div class="action-container">
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
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加图书
      </el-button>
    </div>

    <!-- 图书列表 -->
    <el-table :data="bookList" style="width: 100%" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="bookName" label="图书名称" min-width="150" />
      <el-table-column prop="author" label="作者" min-width="120" />
      <el-table-column prop="bookCode" label="图书编号" min-width="150" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'available' ? 'success' : 'info'">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="borrowCount" label="借阅次数" width="100" />
      <el-table-column prop="createTime" label="入库时间" min-width="150" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="handleEdit(scope.row)"
            >编辑</el-button
          >
          <el-button link type="primary" @click="handleView(scope.row)"
            >查看</el-button
          >
          <el-button link type="danger" @click="handleDelete(scope.row)"
            >删除</el-button
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

    <!-- 图书表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="
        dialogType === 'add'
          ? '添加图书'
          : dialogType === 'edit'
          ? '编辑图书'
          : '查看图书'
      "
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="bookFormRef"
        :model="bookForm"
        :rules="bookRules"
        label-width="100px"
        :disabled="dialogType === 'view'"
      >
        <el-form-item label="图书名称" prop="bookName">
          <el-input v-model="bookForm.bookName" placeholder="请输入图书名称" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="图书编号" prop="bookCode">
          <el-input v-model="bookForm.bookCode" placeholder="请输入图书编号" />
        </el-form-item>
        <el-form-item label="图书分类" prop="bookTypeIds">
          <el-select
            v-model="bookForm.bookTypeIds"
            multiple
            placeholder="请选择图书分类"
            style="width: 100%"
          >
            <el-option
              v-for="item in bookTypeOptions"
              :key="item.id"
              :label="item.typeName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="图书封面" prop="cover">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
          >
            <img v-if="bookForm.cover" :src="bookForm.cover" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="图书简介" prop="description">
          <el-input
            v-model="bookForm.description"
            type="textarea"
            placeholder="请输入图书简介"
            :rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            v-if="dialogType !== 'view'"
            type="primary"
            @click="submitForm"
            :loading="submitting"
          >
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
import { getBookList, createBook, updateBook, deleteBook } from "@/api/book";
import { getTypeList } from "@/api/bookType";

// 图书列表
const bookList = ref([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const searchKeyword = ref("");

// 图书类型选项
const bookTypeOptions = ref([]);

// 状态值映射表
const statusMap = {
  available: "可借阅",
  unavailable: "已借出",
};

// 将状态码转换为显示文本
const getStatusText = (status) => {
  return statusMap[status] || status;
};

// 对话框相关
const dialogVisible = ref(false);
const dialogType = ref("add"); // add, edit, view
const submitting = ref(false);
const bookFormRef = ref(null);
const bookForm = reactive({
  id: "",
  bookName: "",
  author: "",
  bookCode: "",
  status: "available", // 改为使用英文状态值
  borrowCount: 0,
  cover: "",
  description: "",
  bookTypeIds: [],
});

// 表单验证规则
const bookRules = {
  bookName: [{ required: true, message: "请输入图书名称", trigger: "blur" }],
  author: [{ required: true, message: "请输入作者", trigger: "blur" }],
  bookCode: [{ required: true, message: "请输入图书编号", trigger: "blur" }],
};

// 获取图书列表
const fetchBookList = async () => {
  loading.value = true;
  try {
    const res = await getBookList(searchKeyword.value);
    if (res.code === 200) {
      bookList.value = res.data || [];
      total.value = bookList.value.length;
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

// 获取图书类型列表
const fetchBookTypes = async () => {
  try {
    const res = await getTypeList();
    if (res.code === 200) {
      bookTypeOptions.value = res.data || [];
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取图书类型失败");
  }
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchBookList();
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = "";
  handleSearch();
};

// 添加图书
const handleAdd = () => {
  resetForm();
  dialogType.value = "add";
  dialogVisible.value = true;
};

// 编辑图书
const handleEdit = (row) => {
  dialogType.value = "edit";
  resetForm();
  // 复制数据到表单
  Object.keys(bookForm).forEach((key) => {
    if (key in row) {
      bookForm[key] = row[key];
    }
  });
  // 设置图书类型
  if (row.bookTypes) {
    bookForm.bookTypeIds = row.bookTypes.map((type) => type.id);
  }
  dialogVisible.value = true;
};

// 查看图书
const handleView = (row) => {
  dialogType.value = "view";
  resetForm();
  // 复制数据到表单
  Object.keys(bookForm).forEach((key) => {
    if (key in row) {
      bookForm[key] = row[key];
    }
  });
  // 设置图书类型
  if (row.bookTypes) {
    bookForm.bookTypeIds = row.bookTypes.map((type) => type.id);
  }
  dialogVisible.value = true;
};

// 删除图书
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除图书 "${row.bookName}" 吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const res = await deleteBook(row.id);
        if (res.code === 200) {
          ElMessage.success("删除成功");
          fetchBookList();
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
  if (!bookFormRef.value) return;

  await bookFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        // 准备提交的数据
        const submitData = { ...bookForm };

        // 确保bookTypeIds字段存在且正确
        if (!submitData.bookTypeIds || !Array.isArray(submitData.bookTypeIds)) {
          submitData.bookTypeIds = [];
        }

        // 打印表单数据，便于调试
        console.log("提交的图书数据:", JSON.stringify(submitData));

        const api = dialogType.value === "add" ? createBook : updateBook;
        const res = await api(submitData);
        if (res.code === 200) {
          ElMessage.success(
            dialogType.value === "add" ? "添加成功" : "更新成功"
          );
          dialogVisible.value = false;
          fetchBookList();
        } else {
          ElMessage.error(
            res.message ||
              (dialogType.value === "add" ? "添加失败" : "更新失败")
          );
        }
      } catch (error) {
        console.error("提交图书表单错误:", error);
        ElMessage.error(dialogType.value === "add" ? "添加失败" : "更新失败");
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 重置表单
const resetForm = () => {
  if (bookFormRef.value) {
    bookFormRef.value.resetFields();
  }
  bookForm.id = "";
  bookForm.bookName = "";
  bookForm.author = "";
  bookForm.bookCode = "";
  bookForm.status = "available"; // 修改为英文状态值
  bookForm.borrowCount = 0;
  bookForm.cover = "";
  bookForm.description = "";
  bookForm.bookTypeIds = [];
};

// 图书封面上传成功
const handleCoverSuccess = (response) => {
  if (response.code === 200) {
    bookForm.cover = response.data;
    ElMessage.success("上传成功");
  } else {
    ElMessage.error(response.message || "上传失败");
  }
};

// 图书封面上传前校验
const beforeCoverUpload = (file) => {
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

// 分页相关
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchBookList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchBookList();
};

onMounted(() => {
  fetchBookList();
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

.avatar-uploader {
  text-align: center;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
}
</style> 