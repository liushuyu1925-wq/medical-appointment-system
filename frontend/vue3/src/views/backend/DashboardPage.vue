<template>
  <div class="dashboard">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card">
      <template #header>
        <div class="welcome-header">
          <el-avatar
            :size="64"
            :src="avatarUrl"
          >
            {{ userInfo?.name?.charAt(0) }}
          </el-avatar>
          <div class="welcome-info">
            <h2>欢迎回来, {{ userInfo?.name || userInfo?.username }}</h2>
            <p>{{ currentTime }}</p>
          </div>
        </div>
      </template>
      <div class="role-info">
        <el-tag>{{ roleLabel }}</el-tag>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
// 头像URL
const baseAPI = import.meta.env.VITE_API_BASE_URL || '/api'
// 角色标签
const roleLabel = computed(() => {
  const roleMap = {
    'ADMIN': '系统管理员',
    'USER': '普通用户'
  }
  return roleMap[userInfo.value?.roleCode] || '未知角色'
})

const avatarUrl = computed(() => {
  if (!userStore.userInfo?.avatar) return ''
  return baseAPI + userStore.userInfo.avatar
})

// 当前时间
const currentTime = ref('')
let timeInterval = null // 保存定时器引用

const updateTime = () => {
  const now = new Date()
  const options = { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric', 
    weekday: 'long',
    hour: '2-digit',
    minute: '2-digit'
  }
  currentTime.value = now.toLocaleDateString('zh-CN', options)
}

onMounted(() => {
  updateTime()
  // 每分钟更新一次时间
  timeInterval = setInterval(updateTime, 60000)
})

onUnmounted(() => {
  // 清除定时器
  if (timeInterval) {
    clearInterval(timeInterval)
    timeInterval = null
  }
})
</script>

<style lang="scss" scoped>
.dashboard {
  .welcome-card {
    margin-bottom: 20px;
    transition: transform 0.24s ease, box-shadow 0.24s ease;
    
    &:hover {
      transform: translateY(-3px);
      box-shadow: var(--glass-shadow);
    }
    
    .welcome-header {
      display: flex;
      align-items: center;
      gap: 20px;
      
      .el-avatar {
        border: 1px solid rgba(255, 255, 255, 0.72);
        box-shadow: 0 12px 28px rgba(66, 92, 129, 0.12);
        transition: transform 0.24s ease;
        
        &:hover {
          transform: scale(1.04);
        }
      }
      
      .welcome-info {
        h2 {
          margin: 0 0 8px 0;
          font-size: 24px;
          color: var(--glass-text-primary);
          letter-spacing: 0;
        }
        p {
          margin: 0;
          color: var(--glass-text-secondary);
        }
      }
    }
    
    .role-info {
      margin-top: 16px;

      :deep(.el-tag) {
        padding-inline: 14px;
      }
    }
  }
}


</style> 
