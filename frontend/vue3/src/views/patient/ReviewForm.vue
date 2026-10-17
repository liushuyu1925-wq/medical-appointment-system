<template>
  <div class="review-form">
    <div class="review-panel">
      <div class="review-form-header">
        <h2>评价医生</h2>
        <div class="doctor-info">
          医生：{{ doctorName }}
        </div>
      </div>

      <div class="rating-section">
        <div class="review-section-label">
          评分
        </div>
        <div class="stars review-stars">
          <span
            v-for="s in 5"
            :key="s"
            :class="{ active: rating >= s }"
            @click="rating = s"
          >★</span>
        </div>
      </div>

      <div class="content-section">
        <label
          class="review-section-label"
          for="review-content"
        >评价内容</label>
        <textarea
          id="review-content"
          v-model="content"
          rows="5"
          placeholder="请输入您的评价..."
        />
      </div>

      <button
        class="submit-button"
        :disabled="submitting"
        @click="submitReview"
      >
        提交评价
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const appointmentId = route.query.appointmentId
const doctorName = route.query.doctorName || '医生'

const rating = ref(5)
const content = ref('')
const submitting = ref(false)

const submitReview = async () => {
  if (!rating.value) {
    ElMessage.warning('请选择评分')
    return
  }
  submitting.value = true
  try {
    await request.post('/api/review/add', {
      appointmentId: parseInt(appointmentId),
      rating: rating.value,
      content: content.value
    }, {
      successMsg: '评价成功！'
    })
    router.push('/patient/reviews')
  } catch (error) {
    console.error('评价失败:', error)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.review-form {
  max-width: 760px;
  margin: 0 auto;
  padding: 28px 20px;
}

.review-panel {
  padding: 28px;
}

.review-form-header {
  margin-bottom: 24px;
}

.review-form h2 {
  margin: 0;
  font-size: 26px;
  color: var(--glass-text-primary);
}

.doctor-info {
  font-size: 15px;
  font-weight: 500;
  color: var(--glass-text-secondary);
}

.rating-section,
.content-section {
  margin-bottom: 22px;
}

.stars {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.stars span {
  font-size: 34px;
  cursor: pointer;
  color: rgba(138, 152, 171, 0.42);
  margin-right: 0;
}

.stars span.active {
  color: var(--glass-warning);
}

textarea {
  width: 100%;
  padding: 14px 16px;
  margin: 0;
  border: 1px solid rgba(181, 204, 232, 0.36);
  border-radius: var(--glass-radius-md);
  background: rgba(255, 255, 255, 0.56);
  color: var(--glass-text-primary);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.72);
  resize: vertical;
  min-height: 132px;
  outline: none;
}

textarea:focus {
  border-color: rgba(93, 141, 247, 0.5);
  box-shadow: 0 0 0 4px rgba(93, 141, 247, 0.12), inset 0 1px 0 rgba(255, 255, 255, 0.82);
}

.submit-button {
  min-width: 148px;
  background: linear-gradient(135deg, #76a7ff, var(--glass-primary));
  color: white;
  padding: 12px 24px;
  border: 1px solid rgba(93, 141, 247, 0.62);
  border-radius: 999px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  box-shadow: 0 12px 24px rgba(93, 141, 247, 0.18);
}

.submit-button:disabled {
  background: rgba(138, 152, 171, 0.4);
  border-color: rgba(138, 152, 171, 0.16);
  box-shadow: none;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .review-form {
    padding: 20px 12px;
  }

  .review-panel {
    padding: 20px 18px;
  }

  .review-form-header {
    align-items: flex-start;
  }

  .review-form h2 {
    font-size: 22px;
  }
}
</style>
