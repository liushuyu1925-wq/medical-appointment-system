<template>
  <div class="auth-container">
    <div class="auth-background">
      <div class="bg-circle circle-1" />
      <div class="bg-circle circle-2" />
      <div class="bg-circle circle-3" />
      <div class="bg-shape shape-1" />
      <div class="bg-shape shape-2" />
      <div class="bg-shape shape-3" />
      <div class="bg-pattern" />
    </div>
    
    <div class="auth-box">
      <div
        v-if="showHeader"
        class="auth-header"
      >
        <div class="logo">
          <el-icon
            size="42"
            color="#5d8df7"
          >
            <FirstAidKit />
          </el-icon>
        </div>
        <h1 class="title">瑾瑜慧诊</h1>
        <div class="subtitle">
          专业服务 · 悉心照料
        </div>
      </div>
      
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        class="auth-form"
      >
        <slot name="form-items" />
        
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="auth-button"
            @click="handleSubmit"
          >
            {{ submitText }}
          </el-button>
        </el-form-item>
        
        <div class="auth-links">
          <slot name="auth-links" />
        </div>
      </el-form>
      
      <div class="caring-message">
        <el-icon><InfoFilled /></el-icon>
        <span>您的健康是我们最大的牵挂，专业医护团队随时为您服务</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { FirstAidKit, InfoFilled } from '@element-plus/icons-vue'

defineProps({
  formData: {
    type: Object,
    required: true
  },
  rules: {
    type: Object,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  submitText: {
    type: String,
    default: '提交'
  },
  showHeader: {
    type: Boolean,
    default: true
  }
})

const formRef = ref(null)

const emit = defineEmits(['submit'])

const handleSubmit = () => {
  formRef.value.validate(valid => {
    if (valid) {
      emit('submit', formRef.value)
    }
  })
}

defineExpose({
  formRef
})
</script>

<style lang="scss" scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at 18% 18%, rgba(126, 177, 255, 0.24), transparent 28%),
    radial-gradient(circle at 82% 12%, rgba(255, 255, 255, 0.9), transparent 24%),
    linear-gradient(135deg, #f8fbff 0%, #eef4fb 52%, #f7f9fc 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.auth-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  overflow: hidden;
  opacity: 0.46;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  
  &.circle-1 {
    width: 300px;
    height: 300px;
    background: radial-gradient(circle, rgba(93, 141, 247, 0.2) 0%, rgba(93, 141, 247, 0) 70%);
    top: -100px;
    right: -50px;
  }
  
  &.circle-2 {
    width: 500px;
    height: 500px;
    background: radial-gradient(circle, rgba(119, 189, 232, 0.16) 0%, rgba(119, 189, 232, 0) 70%);
    bottom: -200px;
    left: -100px;
  }
  
  &.circle-3 {
    width: 200px;
    height: 200px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.72) 0%, rgba(255, 255, 255, 0) 70%);
    top: 30%;
    left: 10%;
  }
}

.bg-shape {
  position: absolute;
  
  &.shape-1 {
    width: 130px;
    height: 130px;
    background-color: rgba(255, 255, 255, 0.12);
    border: 1px solid rgba(255, 255, 255, 0.42);
    border-radius: 32px;
    top: 20%;
    right: 15%;
    animation: floating 15s infinite ease-in-out;
  }
  
  &.shape-2 {
    width: 88px;
    height: 88px;
    background-color: rgba(93, 141, 247, 0.08);
    border-radius: 28px;
    bottom: 20%;
    right: 30%;
    animation: floating 12s infinite ease-in-out 2s;
  }
  
  &.shape-3 {
    width: 104px;
    height: 104px;
    background-color: rgba(119, 189, 232, 0.08);
    border-radius: 28px;
    top: 60%;
    left: 20%;
    animation: floating 20s infinite ease-in-out 1s;
  }
}

.bg-pattern {
  position: absolute;
  width: 100%;
  height: 100%;
  background-image: radial-gradient(rgba(93, 141, 247, 0.07) 1px, transparent 1px);
  background-size: 34px 34px;
  background-position: 0 0, 15px 15px;
  animation: fadePattern 24s infinite alternate;
}

@keyframes floating {
  0% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(10px, 10px) rotate(5deg);
  }
  50% {
    transform: translate(5px, -5px) rotate(0deg);
  }
  75% {
    transform: translate(-5px, 8px) rotate(-5deg);
  }
  100% {
    transform: translate(0, 0) rotate(0deg);
  }
}

@keyframes fadePattern {
  0% {
    opacity: 0.3;
  }
  50% {
    opacity: 0.7;
  }
  100% {
    opacity: 0.3;
  }
}

.auth-box {
  width: 420px;
  max-width: 100%;
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.8), rgba(248, 251, 255, 0.58));
  backdrop-filter: blur(24px) saturate(145%);
  -webkit-backdrop-filter: blur(24px) saturate(145%);
  border-radius: 24px;
  box-shadow: 0 20px 48px rgba(66, 92, 129, 0.14),
              0 0 0 1px rgba(255, 255, 255, 0.72) inset;
  padding: 40px;
  position: relative;
  z-index: 2;
  border: 1px solid rgba(255, 255, 255, 0.72);
}

.auth-header {
  text-align: center;
  margin-bottom: 30px;
  
  .logo {
    display: flex;
    justify-content: center;
    margin-bottom: 16px;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      width: 64px;
      height: 64px;
      background: rgba(93, 141, 247, 0.1);
      border-radius: 50%;
      z-index: -1;
    }
  }
  
  .title {
    font-size: 28px;
    color: #172033;
    margin: 0 0 8px;
    font-weight: 700;
    background: linear-gradient(135deg, #172033, #5d8df7);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      width: 34px;
      height: 2px;
      background: linear-gradient(135deg, rgba(118, 167, 255, 0.94), #5d8df7);
      bottom: -4px;
      left: 50%;
      transform: translateX(-50%);
      border-radius: 2px;
    }
  }
  
  .subtitle {
    font-size: 16px;
    color: #5f7087;
    letter-spacing: 0;
  }
}

.auth-form {
  margin-bottom: 20px;
  
  :deep(.el-input__wrapper) {
    border-radius: 12px;
    box-shadow: 0 0 0 1px rgba(181, 204, 232, 0.44) inset, inset 0 1px 0 rgba(255, 255, 255, 0.72);
    padding: 0 15px;
    background-color: rgba(255, 255, 255, 0.58);
    transition: all 0.3s ease;
    
    &:hover, &:focus {
      box-shadow: 0 0 0 1px rgba(93, 141, 247, 0.68) inset, 0 0 0 4px rgba(93, 141, 247, 0.12);
      background-color: rgba(255, 255, 255, 0.9);
    }
  }
  
  :deep(.el-input__inner) {
    height: 45px;
  }
  
  :deep(.el-form-item__label) {
    color: #5f7087;
    font-weight: 500;
  }
  
  :deep(.el-form-item__error) {
    color: #e57373;
  }
  
  :deep(.el-textarea__inner) {
    border-radius: 12px;
    padding: 10px 15px;
    background-color: rgba(255, 255, 255, 0.7);
    
    &:hover, &:focus {
      background-color: rgba(255, 255, 255, 0.9);
    }
  }
  
  :deep(.el-select) {
    width: 100%;
  }
  
  :deep(.el-date-editor) {
    width: 100%;
  }
}

.auth-button {
  width: 100%;
  border-radius: 999px;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #76a7ff, #5d8df7);
  border-color: rgba(93, 141, 247, 0.62);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(
      90deg,
      rgba(255, 255, 255, 0) 0%,
      rgba(255, 255, 255, 0.2) 50%,
      rgba(255, 255, 255, 0) 100%
    );
    transition: all 0.5s ease;
  }
  
  &:hover, &:focus {
    background: linear-gradient(135deg, #8bb7ff, #3f74e0);
    border-color: rgba(93, 141, 247, 0.74);
    transform: translateY(-1px);
    box-shadow: 0 10px 20px rgba(93, 141, 247, 0.18);
    
    &::before {
      left: 100%;
    }
  }
}

.auth-links {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #5f7087;
  
  a {
    color: #3f74e0;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.3s ease;
    margin: 0 5px;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      width: 0;
      height: 1px;
      bottom: -2px;
      left: 0;
      background-color: #5d8df7;
      transition: width 0.3s ease;
    }
    
    &:hover {
      color: #2f63c9;
      
      &::after {
        width: 100%;
      }
    }
  }
}

.caring-message {
  display: flex;
  align-items: center;
  background-color: rgba(93, 141, 247, 0.08);
  padding: 12px 15px;
  border-radius: 12px;
  margin-top: 25px;
  border-left: 3px solid #5d8df7;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: radial-gradient(circle at top right, rgba(93, 141, 247, 0.1), transparent 70%);
    z-index: -1;
  }
  
  .el-icon {
    color: #5d8df7;
    margin-right: 10px;
    font-size: 18px;
  }
  
  span {
    color: #5f7087;
    font-size: 14px;
    line-height: 1.5;
  }
}

@media (min-width: 992px) {
  .auth-container {
    padding: 40px;
  }
  
  .auth-box {
    width: 480px;
  }
}

@media (max-width: 767px) {
  .auth-box {
    padding: 28px 18px;
  }
  
  .bg-circle, .bg-shape {
    opacity: 0.5;
  }
}
</style> 
