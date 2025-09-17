<template>
  <div class="comments-container">
    <div class="page-title">留言板</div>

    <!-- 留言区域 -->
    <div class="comment-form">
      <el-form :model="commentForm" ref="commentFormRef">
        <el-form-item>
          <el-input
            v-model="commentForm.content"
            type="textarea"
            :rows="4"
            placeholder="写下您的留言或建议..."
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="submitComment"
            :loading="submitting"
          >
            提交留言
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 留言列表 -->
    <div class="comment-list" v-loading="loading">
      <div v-if="comments.length === 0" class="no-comment">
        <el-empty description="暂无留言" />
      </div>
      <div v-else>
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <span class="username">{{ comment.username }}</span>
            <span class="time">{{ comment.commentTime }}</span>
            <el-button
              v-if="userStore.userId === comment.userId || userStore.isAdmin"
              link
              type="danger"
              @click="handleDelete(comment)"
              size="small"
            >
              删除
            </el-button>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getCommentList, createComment, deleteComment } from "@/api/comment";
import { useUserStore } from "@/store/user";

const userStore = useUserStore();
const loading = ref(false);
const submitting = ref(false);
const comments = ref([]);
const commentFormRef = ref(null);

// 留言表单
const commentForm = reactive({
  content: "",
  userId: userStore.userId,
});

// 获取留言列表
const fetchComments = async () => {
  loading.value = true;
  try {
    const res = await getCommentList();
    if (res.code === 200) {
      comments.value = res.data || [];
    } else {
      ElMessage.error(res.message || "获取留言列表失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取留言列表失败");
  } finally {
    loading.value = false;
  }
};

// 提交留言
const submitComment = async () => {
  if (!userStore.isLogin) {
    ElMessage.warning("请先登录");
    return;
  }

  if (!commentForm.content.trim()) {
    ElMessage.warning("留言内容不能为空");
    return;
  }

  submitting.value = true;
  try {
    const data = {
      content: commentForm.content,
      userId: userStore.userId,
    };

    const res = await createComment(data);
    if (res.code === 200) {
      ElMessage.success("留言成功");
      commentForm.content = "";
      fetchComments();
    } else {
      ElMessage.error(res.message || "留言失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("留言失败");
  } finally {
    submitting.value = false;
  }
};

// 删除留言
const handleDelete = (comment) => {
  ElMessageBox.confirm(`确定要删除此留言吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const res = await deleteComment(comment.id);
        if (res.code === 200) {
          ElMessage.success("删除成功");
          fetchComments();
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
  fetchComments();
});
</script>

<style scoped>
.comments-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.comment-form {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 15px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.username {
  font-weight: bold;
  color: #409eff;
}

.time {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  line-height: 1.6;
  white-space: pre-wrap;
}

.no-comment {
  text-align: center;
  padding: 30px 0;
}
</style> 