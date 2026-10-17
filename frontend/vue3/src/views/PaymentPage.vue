<template>
  <div class="payment-container">
    <div class="payment-shell">
      <el-card class="payment-card">
        <template #header>
          <div class="payment-card-header">
            <span class="payment-title">支付中心</span>
          </div>
        </template>

        <el-descriptions
          :column="2"
          border
          class="payment-descriptions"
        >
          <el-descriptions-item label="订单号">
            {{ orderNo || '—' }}
          </el-descriptions-item>
          <el-descriptions-item label="预约ID">
            {{ appointmentId }}
          </el-descriptions-item>
          <el-descriptions-item label="支付金额">
            <span class="payment-amount">¥ {{ amount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="statusTagType">
              {{ statusText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="过期时间">
            {{ expireTime || '—' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="payment-success-panel">
          <el-icon class="payment-success-icon">
            <CircleCheckFilled />
          </el-icon>
          <div>
            <div class="payment-success-title">
              支付成功
            </div>
            <div class="payment-success-text">
              订单已完成，请返回预约页面查看记录。
            </div>
          </div>
        </div>

        <div class="payment-actions">
          <el-button
            class="pay-button"
            type="success"
            size="large"
            disabled
            @click="handleMockPay"
          >
            支付成功
          </el-button>
          <el-button
            class="appointments-button"
            type="success"
            @click="goToAppointments"
          >
            查看我的预约
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheckFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const appointmentId = ref(route.query.appointmentId || '1')
const amount = ref(route.query.amount || '50.00')

const orderNo = ref(`PAY-${appointmentId.value}-${Date.now().toString().slice(-6)}`)
const expireTime = ref('已完成')
const paymentStatus = ref(1)

const statusText = computed(() => {
  switch (paymentStatus.value) {
    case 0: return '待支付'
    case 1: return '支付成功'
    case 2: return '支付失败'
    case 3: return '已取消'
    case 4: return '已过期'
    default: return '未知'
  }
})

const statusTagType = computed(() => {
  switch (paymentStatus.value) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'danger'
    case 3: return 'info'
    case 4: return 'info'
    default: return ''
  }
})

function handleMockPay() {
  ElMessage.success('支付成功')
}

function goToAppointments() {
  router.push('/my-appointments') // 跳转到我的预约页面
}

onMounted(() => {
  ElMessage.success('支付成功')
})
</script>

<style scoped>
.payment-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 120px);
  padding: 32px 20px;
}

.payment-shell {
  width: 100%;
  max-width: 760px;
  padding: 18px;
}

.payment-card {
  width: 100%;
  max-width: 100%;
  border: none;
  background: transparent;
}

.payment-card-header {
  min-height: 34px;
}

.payment-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--glass-text-primary);
  letter-spacing: 0;
}

.payment-descriptions {
  margin-top: 4px;
}

.payment-amount {
  color: var(--glass-danger);
  font-size: 24px;
  font-weight: 700;
}

.payment-success-panel {
  margin: 24px 0 0;
  padding: 18px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  border: 1px solid rgba(63, 185, 120, 0.28);
  border-radius: 14px;
  background: rgba(63, 185, 120, 0.1);
  color: #1f8f55;
}

.payment-success-icon {
  font-size: 34px;
}

.payment-success-title {
  font-size: 20px;
  font-weight: 700;
  line-height: 1.3;
}

.payment-success-text {
  margin-top: 4px;
  color: #4f9d71;
}

.payment-actions {
  margin-top: 32px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px;
}

.pay-button,
.appointments-button {
  min-width: 152px;
}

:deep(.payment-descriptions .el-descriptions__label) {
  width: 140px;
  color: var(--glass-text-secondary);
}

@media (max-width: 768px) {
  .payment-container {
    align-items: stretch;
    padding: 20px 12px;
  }

  .payment-shell {
    padding: 0;
  }

  :deep(.payment-descriptions .el-descriptions__body table tbody) {
    display: block;
  }

  :deep(.payment-descriptions .el-descriptions__body table tr) {
    display: grid;
    grid-template-columns: 1fr;
  }
}
</style>
