<template>
  <div class="credit-container">
    <div class="page-title">
      我的信用
      <div class="page-actions">
        <el-button type="primary" @click="refreshCredit" :loading="refreshing">
          刷新信用积分
        </el-button>
        <el-button
          type="warning"
          @click="checkOverdue"
          :loading="checking"
          v-if="userStore.isAdmin"
        >
          检查逾期图书
        </el-button>
      </div>
    </div>

    <!-- 信用积分展示 -->
    <el-card class="credit-score-card">
      <div class="credit-score">
        <span class="label">当前信用积分：</span>
        <span class="value">{{ userStore.userInfo.credit || 0 }}</span>
      </div>
      <div class="credit-info">
        <el-alert
          :title="getCreditLevel().title"
          :type="getCreditLevel().type"
          :description="getCreditLevel().description"
          :closable="false"
          show-icon
        ></el-alert>
      </div>
      <div class="credit-rules">
        <div class="rules-title">信用规则：</div>
        <ul class="rules-list">
          <li>按时归还图书：<span class="positive">+2分</span></li>
          <li>逾期归还图书：<span class="negative">-10分</span></li>
          <li>
            图书逾期未归还（系统自动检测）：<span class="negative"
              >-5分/本</span
            >
          </li>
        </ul>
      </div>
    </el-card>

    <!-- 信用记录列表 -->
    <div class="credit-record-list">
      <div class="list-title">信用记录</div>
      <el-table :data="records" style="width: 100%" v-loading="loading" border>
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
      </el-table>

      <div class="empty-data" v-if="!loading && records.length === 0">
        <el-empty description="暂无信用记录"></el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getUserCreditRecords, checkOverdueBooks } from "@/api/credit";
import { getUserInfo } from "@/api/user";
import { useUserStore } from "@/store/user";

const userStore = useUserStore();
const loading = ref(false);
const refreshing = ref(false);
const checking = ref(false);
const records = ref([]);

// 获取信用等级
const getCreditLevel = () => {
  const credit = userStore.userInfo.credit || 0;

  if (credit >= 100) {
    return {
      title: "信用优秀",
      type: "success",
      description: "您的信用状况优秀，可以同时借阅更多图书",
    };
  } else if (credit >= 80) {
    return {
      title: "信用良好",
      type: "success",
      description: "您的信用状况良好，请继续保持",
    };
  } else if (credit >= 60) {
    return {
      title: "信用一般",
      type: "warning",
      description: "您的信用状况一般，请按时归还图书",
    };
  } else {
    return {
      title: "信用较差",
      type: "error",
      description:
        "您的信用状况较差，可能会限制借阅服务，请及时归还图书并遵守规则",
    };
  }
};

// 获取用户信用记录
const fetchCreditRecords = async () => {
  if (!userStore.userId) return;

  loading.value = true;
  try {
    const res = await getUserCreditRecords(userStore.userId);
    if (res.code === 200) {
      records.value = res.data || [];
    } else {
      ElMessage.error(res.message || "获取信用记录失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取信用记录失败");
  } finally {
    loading.value = false;
  }
};

// 刷新用户信用积分
const refreshCredit = async () => {
  if (!userStore.userId) return;

  refreshing.value = true;
  try {
    const res = await getUserInfo(userStore.userId);
    if (res.code === 200 && res.data) {
      // 更新本地存储的用户信息
      const updatedUserInfo = {
        ...userStore.userInfo,
        credit: res.data.credit,
      };
      localStorage.setItem("userInfo", JSON.stringify(updatedUserInfo));
      userStore.userInfo = updatedUserInfo;

      // 刷新信用记录
      fetchCreditRecords();

      ElMessage.success("信用积分已更新");
    } else {
      ElMessage.error(res.message || "刷新信用积分失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("刷新信用积分失败");
  } finally {
    refreshing.value = false;
  }
};

// 检查逾期图书
const checkOverdue = async () => {
  checking.value = true;
  try {
    const res = await checkOverdueBooks();
    if (res.code === 200) {
      ElMessage.success(res.data.message || "逾期检查完成");
      // 刷新信用积分和记录
      refreshCredit();
    } else {
      ElMessage.error(res.message || "检查逾期图书失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("检查逾期图书失败");
  } finally {
    checking.value = false;
  }
};

onMounted(() => {
  fetchCreditRecords();
});
</script>

<style scoped>
.credit-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-actions {
  display: flex;
  gap: 10px;
}

.credit-score-card {
  margin-bottom: 30px;
}

.credit-score {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.label {
  font-size: 16px;
  color: #606266;
}

.value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  margin-left: 10px;
}

.credit-info {
  margin-top: 10px;
}

.credit-rules {
  margin-top: 20px;
  padding: 15px;
  background-color: #f8f8f8;
  border-radius: 4px;
}

.rules-title {
  font-weight: bold;
  margin-bottom: 10px;
}

.rules-list {
  padding-left: 20px;
  margin: 0;
}

.rules-list li {
  margin-bottom: 5px;
}

.credit-record-list {
  margin-top: 30px;
}

.list-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
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