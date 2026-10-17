<template>
  <div class="home-container">
    <!-- 轮播图区域 -->
    <div class="banner-section">
      <el-carousel
        height="400px"
        indicator-position="outside"
      >
        <el-carousel-item
          v-for="item in bannerList"
          :key="item.id"
        >
          <div
            class="banner-content"
            :style="{ backgroundImage: `url(${item.imageUrl})` }"
          >
            <div class="banner-text">
              <h2>{{ item.title }}</h2>
              <p>{{ item.description }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 快捷服务区域 -->
    <div class="quick-service-section">
      <div class="section-title">
        <h2>快捷服务</h2>
      </div>
      <div class="service-cards">
        <div
          v-for="service in quickServices"
          :key="service.id"
          class="service-card"
          @click="handleServiceClick(service.route)"
        >
          <el-icon
            class="service-icon"
            :size="40"
          >
            <component :is="service.icon" />
          </el-icon>
          <div class="service-title">
            {{ service.title }}
          </div>
          <div class="service-desc">
            {{ service.description }}
          </div>
        </div>
      </div>
    </div>

    <!-- 预约指南区域 -->
    <div class="guide-section">
      <div class="section-title">
        <h2>预约挂号指南</h2>
      </div>
      <div class="guide-steps">
        <el-steps
          :active="1"
          finish-status="success"
          simple
        >
          <el-step
            title="注册登录"
            description="注册账号并完善个人信息"
          />
          <el-step
            title="选择科室医生"
            description="根据症状选择合适的科室和医生"
          />
          <el-step
            title="选择就诊时间"
            description="选择合适的就诊日期和时间段"
          />
          <el-step
            title="确认预约信息"
            description="核对并确认预约信息"
          />
          <el-step
            title="按时就诊"
            description="凭预约号在指定时间前往就诊"
          />
        </el-steps>
        <div class="guide-action">
          <el-button
            type="primary"
            size="large"
            round
            @click="handleServiceClick('/appointment')"
          >
            立即预约
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

// 正确导入图片
import banner1 from '../../assets/banner/banner1.png'
import banner2 from '../../assets/banner/banner2.png'
import banner3 from '../../assets/banner/banner3.png'
import banner4 from '../../assets/banner/banner4.png'

const router = useRouter()

// 轮播图数据
const bannerList = ref([
  {
    id: 1,
    title: '专业医疗团队，贴心守护您的健康',
    description: '我们的医疗团队将以专业的技术和温暖的关怀，为您提供全方位的健康服务',
    imageUrl: banner1
  },
  {
    id: 2,
    title: '舒适就医环境，温馨呵护每一位患者',
    description: '我们精心打造温馨舒适的就医环境，让您在治疗过程中倍感关怀与尊重',
    imageUrl: banner2
  },
  {
    id: 3,
    title: '便捷医疗服务，智慧医院尽在掌握',
    description: '通过智能预约系统，轻松安排就诊时间，减少等待，提升您的就医体验',
    imageUrl: banner3
  },
  {
    id: 4,
    title: '专注健康管理，呵护您的幸福生活',
    description: '我们致力于为您提供全面的健康管理服务，让健康成为您生活的坚实保障',
    imageUrl: banner4
  }
])

// 快捷服务数据
const quickServices = ref([
  {
    id: 1,
    title: '预约挂号',
    description: '在线预约，避免排队等候，轻松安排就诊时间',
    icon: 'Calendar',
    route: '/appointment'
  },
  {
    id: 2,
    title: '就诊记录',
    description: '随时查询历史就诊记录和处方信息，健康档案一目了然',
    icon: 'Notification',
    route: '/medical-record'
  },
  {
    id: 3,
    title: '科室导航',
    description: '了解各科室位置及专业领域，轻松找到对应科室',
    icon: 'Location',
    route: '/department'
  },
  {
    id: 4,
    title: '名医团队',
    description: '查看医生详情及专长领域，选择适合您的专业医生',
    icon: 'User',
    route: '/doctor'
  }
])

// 处理服务点击
const handleServiceClick = (route) => {
  router.push(route)
}

</script>

<style lang="scss" scoped>
.home-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 25px;
  background-color: transparent;
  font-family: inherit;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(181, 204, 232, 0.34);

  h2 {
    font-size: 24px;
    color: var(--text-primary);
    margin: 0;
    position: relative;
    padding-left: 15px;
    font-weight: 600;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 5px;
      height: 24px;
      background: linear-gradient(180deg, var(--glass-primary), var(--glass-cyan));
      border-radius: 10px;
    }
  }

  .view-more {
    color: var(--soft-blue);
    text-decoration: none;
    font-size: 15px;
    font-weight: 500;
    transition: all 0.3s ease;
    
    &:hover {
      color: #3f74e0;
    }
  }
}

// 轮播图样式
.banner-section {
  margin-bottom: 35px;
  border-radius: var(--radius-large);
  overflow: hidden;
  box-shadow: var(--shadow-soft);

  .banner-content {
    height: 100%;
    background-size: cover;
    background-position: center;
    display: flex;
    align-items: center;
    padding: 0 60px;
  }

  .banner-text {
    background: rgba(255, 255, 255, 0.64);
    padding: 25px 30px;
    border-radius: var(--radius-medium);
    max-width: 500px;
    border: 1px solid rgba(255, 255, 255, 0.72);
    box-shadow: var(--shadow-soft), inset 0 1px 0 rgba(255, 255, 255, 0.78);
    backdrop-filter: blur(24px) saturate(145%);
    -webkit-backdrop-filter: blur(24px) saturate(145%);

    h2 {
      font-size: 28px;
      margin-bottom: 12px;
      color: var(--text-primary);
      font-weight: 600;
    }

    p {
      font-size: 16px;
      color: var(--text-secondary);
      margin: 0;
      line-height: 1.6;
    }
  }
}

// 快捷服务样式
.quick-service-section {
  margin-bottom: 35px;

  .service-cards {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 25px;
  }

  .service-card {
    background: linear-gradient(145deg, rgba(255, 255, 255, 0.76), rgba(248, 251, 255, 0.48));
    border-radius: var(--radius-medium);
    padding: 25px;
    text-align: center;
    box-shadow: var(--shadow-soft);
    transition: all 0.3s ease;
    cursor: pointer;
    border: 1px solid rgba(255, 255, 255, 0.72);
    backdrop-filter: blur(20px) saturate(140%);
    -webkit-backdrop-filter: blur(20px) saturate(140%);

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 18px 45px rgba(66, 92, 129, 0.12), inset 0 1px 0 rgba(255, 255, 255, 0.78);
    }

    .service-icon {
      color: var(--glass-primary-strong);
      margin-bottom: 18px;
      background-color: rgba(93, 141, 247, 0.1);
      padding: 15px;
      border-radius: 50%;
      display: inline-block;
    }

    .service-title {
      font-size: 19px;
      font-weight: 600;
      margin-bottom: 12px;
      color: var(--text-primary);
    }

    .service-desc {
      font-size: 15px;
      color: var(--text-secondary);
      line-height: 1.5;
    }
  }
}

// 预约指南样式
.guide-section {
  margin-bottom: 35px;
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.76), rgba(248, 251, 255, 0.48));
  border-radius: var(--radius-medium);
  padding: 30px;
  box-shadow: var(--shadow-soft);
  border: 1px solid rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(20px) saturate(140%);
  -webkit-backdrop-filter: blur(20px) saturate(140%);

  .guide-steps {
    margin-top: 25px;
  }

  :deep(.el-steps--simple) {
    display: grid;
    grid-template-columns: repeat(5, minmax(0, 1fr));
    align-items: center;
    gap: 0;
    padding: 18px 24px;
  }

  :deep(.el-steps--simple .el-step) {
    min-width: 0;
    justify-content: center;
  }

  :deep(.el-steps--simple .el-step__main) {
    min-width: 0;
    flex-shrink: 1;
    text-align: center;
  }

  :deep(.el-steps--simple .el-step__head) {
    flex-shrink: 0;
    justify-content: center;
  }

  :deep(.el-steps--simple .el-step__arrow) {
    flex-shrink: 0;
    margin: 0 8px;
  }

  :deep(.el-step__title) {
    font-size: 15px;
    font-weight: 500;
    color: var(--text-primary);
    white-space: nowrap;
  }

  :deep(.el-step__description) {
    font-size: 14px;
    color: var(--text-secondary);
  }

  :deep(.el-step__icon) {
    background-color: #5d8df7;
    color: white;
  }

  .guide-action {
    text-align: center;
    margin-top: 35px;
    
    .el-button {
      border-radius: 30px;
      padding: 12px 30px;
      font-size: 16px;
      font-weight: 600;
      background: linear-gradient(135deg, #76a7ff, #5d8df7);
      border-color: rgba(93, 141, 247, 0.62);
      color: white;
      box-shadow: 0 12px 24px rgba(93, 141, 247, 0.22);
      
      &:hover {
        background: linear-gradient(135deg, #8bb7ff, #3f74e0);
        border-color: rgba(93, 141, 247, 0.74);
      }
    }
  }
}

// 响应式调整
@media (max-width: 768px) {
  .banner-section {
    .banner-text {
      h2 {
        font-size: 22px;
      }
      
      p {
        font-size: 14px;
      }
    }
  }
  
  .quick-service-section {
    .service-cards {
      grid-template-columns: repeat(2, 1fr);
    }
  }
  
  .guide-section {
    padding: 20px;
    
    :deep(.el-steps) {
      padding: 14px 12px;
    }

    :deep(.el-steps--simple .el-step__arrow) {
      margin: 0 4px;
    }

    :deep(.el-step__title) {
      font-size: 13px;
    }
  }
}

@media (max-width: 576px) {
  .home-container {
    padding: 15px;
  }

  .quick-service-section {
    .service-cards {
      grid-template-columns: 1fr;
    }
  }
  
  .banner-section {
    .banner-content {
      padding: 0 20px;
    }
    
    .banner-text {
      padding: 15px;
      max-width: 100%;
    }
  }

  .guide-section {
    :deep(.el-steps) {
      padding: 12px 8px;
    }

    :deep(.el-steps--simple .el-step__icon) {
      width: 20px;
      height: 20px;
      font-size: 12px;
    }

    :deep(.el-steps--simple .el-step__arrow) {
      margin: 0 2px;
    }

    :deep(.el-step__title) {
      font-size: 12px;
    }
  }
}
</style>
