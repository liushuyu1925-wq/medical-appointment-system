<template>
  <div class="frontend-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="container">
        <div class="logo-container">
          <div class="logo">
            <el-icon
              color="#5d8df7"
              :size="32"
            >
              <FirstAidKit />
            </el-icon>
            <span class="logo-text">瑾瑜慧诊</span>
          </div>
          <div class="slogan">
            专业服务 · 悉心照料
          </div>
        </div>
          
        <el-menu
          :default-active="activeIndex"
          mode="horizontal"
          :ellipsis="false"
          class="nav-menu"
          background-color="transparent"
          text-color="#5f7087"
          active-text-color="#3f74e0"
          router
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/department">
            <el-icon><OfficeBuilding /></el-icon>
            <span>科室介绍</span>
          </el-menu-item>
          <el-menu-item index="/doctor">
            <el-icon><User /></el-icon>
            <span>医生介绍</span>
          </el-menu-item>
          <el-menu-item
            v-if="isLoggedIn"
            index="/appointment"
          >
            <el-icon><Calendar /></el-icon>
            <span>预约挂号</span>
          </el-menu-item>
          <el-menu-item
            v-if="isLoggedIn"
            index="/my-appointments"
          >
            <el-icon><Tickets /></el-icon>
            <span>我的预约</span>
          </el-menu-item>
          <el-menu-item
            v-if="isLoggedIn"
            index="/medical-record"
          >
            <el-icon><DocumentChecked /></el-icon>
            <span>就诊记录</span>
          </el-menu-item>
          <el-menu-item
            v-if="isLoggedIn"
            index="/profile"
          >
            <el-icon><UserFilled /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>
          
        <div class="user-actions">
          <template v-if="isLoggedIn">
            <el-dropdown trigger="click">
              <span class="user-profile">
                <el-avatar
                  :size="36"
                  :src="userAvatar"
                />
                <span class="username">{{ userName }}</span>
                <el-icon><CaretBottom /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/profile')">
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item
                    divided
                    @click="handleLogout"
                  >
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button
              type="primary"
              round
              @click="router.push('/login')"
            >
              <el-icon><Key /></el-icon>登录
            </el-button>
            <el-button
              round
              @click="router.push('/register')"
            >
              <el-icon><User /></el-icon>注册
            </el-button>
          </template>
        </div>
      </div>
    </el-header>
  
    <!-- 主要内容区域 -->
    <el-main class="main-content">
      <div class="container">
        <router-view />
      </div>
    </el-main>
  
    <!-- 页脚 -->
    <el-footer class="footer">
      <div class="container">
        <div class="copyright">
          <p>&copy; {{ new Date().getFullYear() }} 瑾瑜慧诊 版权所有</p>
        </div>
      </div>
    </el-footer>
  </div>
</template>
  
  <script setup>
  import { computed, ref, watchEffect } from 'vue'
  import { useUserStore } from '@/store/user'
  import { useRouter, useRoute } from 'vue-router'
  import { 
    User, 
    SwitchButton, 
    CaretBottom, 
    HomeFilled,
    Calendar,
    Tickets,
    DocumentChecked,
    UserFilled,
    FirstAidKit,
    OfficeBuilding,
    Key
  } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'
  
  const userStore = useUserStore()
  const router = useRouter()
  const route = useRoute()
  
  const isLoggedIn = computed(() => !!userStore.token)
  const userName = computed(() => userStore.userInfo?.name || userStore.userInfo?.username || '用户')
  const userAvatar = computed(() => {
    const baseAPI = import.meta.env.VITE_APP_BASE_API || '/api'
    return userStore.userInfo?.avatar ? baseAPI + userStore.userInfo.avatar : ''
  })
  
  const activeIndex = ref('/')
  
  watchEffect(() => {
    activeIndex.value = route.path
  })
  
  const handleLogout = () => {
    userStore.clearUserInfo()
    ElMessage.success('退出登录成功')
    router.push('/login')
  }
  </script>
  
  <style scoped>
  .frontend-layout {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    background:
      radial-gradient(circle at 12% 8%, rgba(126, 177, 255, 0.2), transparent 28%),
      linear-gradient(135deg, #f8fbff 0%, #eef4fb 52%, #f7f9fc 100%);
  }
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    width: 100%;
  }
  
  .header {
    box-shadow: 0 14px 34px rgba(66, 92, 129, 0.08), inset 0 -1px 0 rgba(255, 255, 255, 0.76);
    background: rgba(255, 255, 255, 0.64);
    backdrop-filter: blur(24px) saturate(150%);
    -webkit-backdrop-filter: blur(24px) saturate(150%);
    border-bottom: 1px solid rgba(181, 204, 232, 0.28);
    position: sticky;
    top: 0;
    z-index: 1000;
    padding: 0 20px;
    height: 70px;
    line-height: 70px;
    transition: all 0.3s ease;
  }
  
  .header .container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 100%;
  }
  
  .logo-container {
    display: flex;
    flex-direction: column;
    line-height: normal;
  }
  
  .logo {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .logo-text {
    font-size: 24px;
    font-weight: 700;
    color: #172033;
    background: linear-gradient(135deg, #172033, #5d8df7);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    letter-spacing: 0;
  }
  
  .slogan {
    font-size: 12px;
    color: #7a8798;
    margin-top: 2px;
    letter-spacing: 0;
  }
  
  .nav-menu {
    flex-grow: 1;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    border-bottom: none;
  }
  
  .nav-menu :deep(.el-menu-item) {
    font-size: 16px;
    height: 44px;
    line-height: 44px;
    padding: 0 18px;
    font-weight: 500;
    border-radius: 999px;
    margin: 0 4px;
  }
  
  .nav-menu :deep(.el-menu-item.is-active) {
    font-weight: 600;
    color: #3f74e0;
    background-color: rgba(93, 141, 247, 0.12);
    border-bottom: none;
  }
  
  .nav-menu :deep(.el-menu-item:hover) {
    background-color: rgba(93, 141, 247, 0.08);
  }
  
  .nav-menu :deep(.el-menu-item .el-icon) {
    margin-right: 6px;
    font-size: 18px;
    vertical-align: middle;
  }
  
  .user-actions {
    display: flex;
    gap: 12px;
    align-items: center;
  }
  
  .user-profile {
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;
    padding: 6px 12px;
    border-radius: 999px;
    transition: all 0.3s ease;
    background-color: rgba(255, 255, 255, 0.56);
    border: 1px solid rgba(181, 204, 232, 0.42);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.7);
  }
  
  .user-profile:hover {
    background-color: rgba(255, 255, 255, 0.76);
  }
  
  .username {
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    color: #172033;
    font-weight: 500;
  }
  
  .user-actions :deep(.el-button) {
    border-radius: 20px;
    font-weight: 500;
  }
  
  .user-actions :deep(.el-button--primary) {
    background: linear-gradient(135deg, #76a7ff, #5d8df7);
    border-color: rgba(93, 141, 247, 0.62);
  }
  
  .user-actions :deep(.el-button--primary:hover) {
    background: linear-gradient(135deg, #8bb7ff, #3f74e0);
    border-color: rgba(93, 141, 247, 0.74);
  }
  
  .user-actions :deep(.el-button .el-icon) {
    margin-right: 4px;
  }
  
  .main-content {
    flex: 1;
    padding-top: 2rem;
    padding-bottom: 2rem;
    background: transparent;
  }
  
  .footer {
    background: rgba(255, 255, 255, 0.52);
    color: #5f7087;
    padding: 20px 0;
    border-top: 1px solid rgba(181, 204, 232, 0.32);
    backdrop-filter: blur(22px) saturate(140%);
    -webkit-backdrop-filter: blur(22px) saturate(140%);
  }
  
  .copyright {
    text-align: center;
    color: #7a8798;
    font-size: 14px;
  }

  @media (max-width: 960px) {
    .header {
      height: auto;
      line-height: normal;
      padding: 12px 16px;
    }

    .header .container {
      flex-wrap: wrap;
      gap: 10px;
    }

    .nav-menu {
      order: 3;
      width: 100%;
      height: auto;
      overflow-x: auto;
      justify-content: flex-start;
    }

    .nav-menu :deep(.el-menu-item) {
      height: 46px;
      line-height: 46px;
      flex-shrink: 0;
    }
  }
  </style>
