<template>
  <div class="review-list">
    <div class="review-list-header">
      <h2>我的评价</h2>
      <div class="review-muted">
        查看历史就诊评价与医生回复
      </div>
    </div>
    <div
      v-if="reviews.length === 0"
      class="empty review-panel"
    >
      暂无评价
    </div>
    <div
      v-for="review in reviews"
      :key="review.id"
      class="review-card review-entry"
    >
      <div class="doctor">
        医生：{{ review.doctorName }}
      </div>
      <div class="rating">
        评分：{{ review.rating }}星
      </div>
      <div class="content">
        评价：{{ review.content }}
      </div>
      <div
        v-if="review.reply"
        class="reply"
      >
        医生回复：{{ review.reply }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const reviews = ref([])

onMounted(async () => {
  try {
    const res = await axios.get('/api/review/my-reviews')
    reviews.value = res.data
  } catch (error) {
    console.error('加载评价失败', error)
  }
})
</script>

<style scoped>
.review-list {
  max-width: 900px;
  margin: 0 auto;
  padding: 28px 20px;
}

.review-list h2 {
  margin: 0;
  font-size: 26px;
  color: var(--glass-text-primary);
}

.review-list-header {
  margin-bottom: 20px;
}

.review-card {
  padding: 20px 22px;
  margin-bottom: 16px;
}

.empty {
  text-align: center;
  color: var(--glass-text-muted);
  padding: 56px 20px;
}

.doctor {
  font-weight: 600;
  color: var(--glass-text-primary);
  margin-bottom: 8px;
}

.rating {
  color: var(--glass-warning);
  font-weight: 600;
  margin-bottom: 10px;
}

.content {
  color: var(--glass-text-secondary);
  line-height: 1.7;
}

.reply {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid rgba(181, 204, 232, 0.3);
  color: var(--glass-text-secondary);
  line-height: 1.7;
}

@media (max-width: 768px) {
  .review-list {
    padding: 20px 12px;
  }

  .review-list h2 {
    font-size: 22px;
  }
}
</style>
