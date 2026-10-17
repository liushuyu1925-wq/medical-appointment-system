<template>
  <div
    class="sidebar-container"
    :class="{ 'is-collapsed': isCollapsed }"
  >
    <div class="logo">
      <span class="logo-icon">
        <el-icon><FirstAidKit /></el-icon>
      </span>
      <span
        v-show="!isCollapsed"
        class="logo-text"
      >瑾瑜慧诊系统</span>
    </div>

    <div class="menu-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        mode="vertical"
        class="sidebar-menu"
        text-color="#5f7087"
        active-text-color="#172033"
        router
      >
        <el-menu-item index="/back/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>
            首页
          </template>
        </el-menu-item>

        <el-menu-item index="/back/user">
          <el-icon><User /></el-icon>
          <template #title>
            用户管理
          </template>
        </el-menu-item>

        <el-menu-item index="/back/patient">
          <el-icon><UserFilled /></el-icon>
          <template #title>
            患者管理
          </template>
        </el-menu-item>

        <el-menu-item
          v-if="userStore.isAdmin"
          index="/back/notice"
        >
          <el-icon><Bell /></el-icon>
          <template #title>
            通知管理
          </template>
        </el-menu-item>

        

        <el-menu-item index="/back/profile">
          <el-icon><UserFilled /></el-icon>
          <template #title>
            个人信息
          </template>
        </el-menu-item>

        <el-menu-item index="/back/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>
            统计分析
          </template>
        </el-menu-item>

        <el-menu-item index="/back/department">
          <el-icon><OfficeBuilding /></el-icon>
          <template #title>
            科室管理
          </template>
        </el-menu-item>

        <el-menu-item index="/back/doctor">
          <el-icon><UserFilled /></el-icon>
          <template #title>
            医生管理
          </template>
        </el-menu-item>

        <el-menu-item index="/back/medicine">
          <el-icon><FirstAidKit /></el-icon>
          <template #title>
            药品管理
          </template>
        </el-menu-item>

        <el-menu-item index="/back/medicine-category">
          <el-icon><Menu /></el-icon>
          <template #title>
            药品分类管理
          </template>
        </el-menu-item>

        <el-menu-item index="/back/schedule">
          <el-icon><Calendar /></el-icon>
          <template #title>
            排班管理
          </template>
        </el-menu-item>

        <el-menu-item index="/back/appointment">
          <el-icon><Tickets /></el-icon>
          <template #title>
            预约管理
          </template>
        </el-menu-item>

        <el-menu-item index="/back/medical-record">
          <el-icon><Document /></el-icon>
          <template #title>
            就诊记录管理
          </template>
        </el-menu-item>

        <el-menu-item index="/back/follow-up">
          <el-icon><Message /></el-icon>
          <template #title>
            随访记录管理
          </template>
        </el-menu-item>

        <el-menu-item index="/back/report">
          <el-icon><DocumentCopy /></el-icon>
          <template #title>
            报告管理
          </template>
        </el-menu-item>

        <el-menu-item index="/back/prescription">
          <el-icon><List /></el-icon>
          <template #title>
            处方管理
          </template>
        </el-menu-item>
      </el-menu>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/store/app'
import { useUserStore } from '@/store/user'
import {
  Bell,
  Calendar,
  DataAnalysis,
  Document,
  DocumentCopy,
  FirstAidKit,
  HomeFilled,
  List,
  Menu,
  Message,
  OfficeBuilding,
  Tickets,
  User,
  UserFilled
} from '@element-plus/icons-vue'

const route = useRoute()
const appStore = useAppStore()
const userStore = useUserStore()

const isCollapsed = computed(() => appStore.sidebarCollapsed)
const activeMenu = computed(() => route.meta.activeMenu || route.path)
</script>

<style lang="scss" scoped>
$primary-color: #5d8df7;
$primary-light: #dbe8ff;
$active-indicator: #5d8df7;

.sidebar-container {
  height: 100%;
  min-height: 100vh;
  background: rgba(255, 255, 255, 0.58);
  border-right: 1px solid rgba(181, 204, 232, 0.34);
  box-shadow: 14px 0 36px rgba(66, 92, 129, 0.08), inset -1px 0 0 rgba(255, 255, 255, 0.62);
  backdrop-filter: blur(26px) saturate(150%);
  -webkit-backdrop-filter: blur(26px) saturate(150%);
  display: flex;
  flex-direction: column;
  width: 220px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &.is-collapsed {
    width: 64px;

    .logo {
      padding: 0;
      justify-content: center;

      .logo-icon {
        margin: 0;
      }
    }

    :deep(.el-menu) {
      .el-sub-menu__title span,
      .el-menu-item span {
        opacity: 0;
        transition: opacity 0.2s;
      }
    }
  }

  .logo {
    height: 60px;
    flex-shrink: 0;
    line-height: 60px;
    text-align: center;
    background: rgba(255, 255, 255, 0.46);
    backdrop-filter: blur(18px);
    -webkit-backdrop-filter: blur(18px);
    border-bottom: 1px solid rgba(181, 204, 232, 0.3);
    display: flex;
    align-items: center;
    padding: 0 16px;
    overflow: hidden;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    .logo-icon {
      font-size: 24px;
      margin-right: 8px;
      transition: margin 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      color: $primary-color;
    }

    .logo-text {
      color: #172033;
      font-size: 18px;
      font-weight: 700;
      white-space: nowrap;
      opacity: 1;
      transition: opacity 0.2s;
    }
  }

  .menu-wrapper {
    flex: 1;
    overflow-y: auto;
    overflow-x: hidden;
  }

  :deep(.sidebar-menu) {
    border: none;
    background: transparent;
    padding: 10px 8px;

    .el-menu-item,
    .el-sub-menu__title {
      height: 46px;
      line-height: 46px;
      color: #5f7087;
      background: transparent;
      border-radius: 14px;
      margin: 4px 0;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

      &:hover {
        background: rgba($primary-color, 0.1) !important;
        color: #3f74e0 !important;
      }
    }

    .el-menu-item.is-active {
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(219, 232, 255, 0.7)) !important;
      color: #172033 !important;
      box-shadow: 0 10px 24px rgba(93, 141, 247, 0.14), inset 0 1px 0 rgba(255, 255, 255, 0.76);

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        width: 3px;
        height: 100%;
        background: $active-indicator;
        border-radius: 999px;
      }
    }

    .el-sub-menu .el-menu {
      background: rgba(255, 255, 255, 0.34);
    }
  }

  .el-icon {
    vertical-align: middle;
    margin-right: 5px;
    width: 24px;
    text-align: center;
    color: inherit;
  }
}
</style>
