<template>
  <div class="notice-page">
    <div class="welcome-banner">
      <div class="banner-content">
        <h2>
          <el-icon><Bell /></el-icon>
          消息通知
        </h2>
        <p>查看预约成功、取消、改约和就诊提醒等站内通知。</p>
      </div>
      <div class="banner-actions">
        <el-badge
          :value="unreadCount"
          :hidden="!unreadCount"
          class="unread-badge"
        >
          <el-button
            type="primary"
            round
            :disabled="!unreadCount"
            @click="handleMarkAllRead"
          >
            全部已读
          </el-button>
        </el-badge>
        <el-button
          round
          @click="router.push('/profile')"
        >
          返回个人中心
        </el-button>
      </div>
    </div>

    <el-card
      class="notice-card"
      shadow="hover"
    >
      <div class="filter-bar">
        <el-select
          v-model="searchForm.noticeType"
          placeholder="通知类型"
          clearable
          class="filter-item"
        >
          <el-option
            v-for="item in noticeTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-select
          v-model="searchForm.readStatus"
          placeholder="读取状态"
          clearable
          class="filter-item"
        >
          <el-option
            label="未读"
            :value="0"
          />
          <el-option
            label="已读"
            :value="1"
          />
        </el-select>
        <el-button
          type="primary"
          @click="handleSearch"
        >
          查询
        </el-button>
        <el-button @click="resetSearch">
          重置
        </el-button>
      </div>

      <div v-loading="loading">
        <el-empty
          v-if="notices.length === 0"
          description="暂无通知记录"
          :image-size="120"
        />

        <div
          v-else
          class="notice-list"
        >
          <div
            v-for="item in notices"
            :key="item.id"
            class="notice-item"
            :class="{ unread: item.readStatus === 0 }"
            @click="handleOpenDetail(item)"
          >
            <div class="notice-item-header">
              <div class="notice-title-wrap">
                <span
                  class="notice-dot"
                  :class="{ active: item.readStatus === 0 }"
                />
                <h3>{{ item.title }}</h3>
                <el-tag
                  size="small"
                  :type="getTypeTag(item.noticeType)"
                >
                  {{ getTypeLabel(item.noticeType) }}
                </el-tag>
              </div>
              <span class="notice-time">{{ formatDateTime(item.createTime) }}</span>
            </div>

            <div class="notice-meta">
              <span v-if="item.departmentName">{{ item.departmentName }}</span>
              <span v-if="item.doctorName">医生：{{ item.doctorName }}</span>
              <span v-if="item.appointmentDate">就诊日：{{ item.appointmentDate }}</span>
              <span v-if="item.timeSlot">{{ item.timeSlot }}</span>
            </div>

            <p class="notice-content">
              {{ item.content }}
            </p>

            <div class="notice-actions">
              <el-tag
                v-if="item.readStatus === 0"
                type="danger"
                effect="plain"
              >
                未读
              </el-tag>
              <el-tag
                v-else
                type="info"
                effect="plain"
              >
                已读
              </el-tag>
              <el-button
                v-if="item.readStatus === 0"
                type="primary"
                link
                @click.stop="handleMarkRead(item)"
              >
                标记已读
              </el-button>
            </div>
          </div>
        </div>

        <div class="pagination-container">
          <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            background
            :total="total"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </div>
      </div>
    </el-card>

    <el-dialog
      v-model="detailVisible"
      title="通知详情"
      width="640px"
    >
      <div
        v-if="currentNotice"
        class="detail-wrapper"
      >
        <div class="detail-head">
          <h3>{{ currentNotice.title }}</h3>
          <div class="detail-tags">
            <el-tag :type="getTypeTag(currentNotice.noticeType)">
              {{ getTypeLabel(currentNotice.noticeType) }}
            </el-tag>
            <el-tag :type="currentNotice.readStatus === 0 ? 'danger' : 'info'">
              {{ currentNotice.readStatus === 0 ? '未读' : '已读' }}
            </el-tag>
          </div>
        </div>

        <el-descriptions
          :column="1"
          border
          class="detail-desc"
        >
          <el-descriptions-item label="通知时间">
            {{ formatDateTime(currentNotice.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="预约编号">
            {{ currentNotice.appointmentNo || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="科室">
            {{ currentNotice.departmentName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="医生">
            {{ currentNotice.doctorName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="就诊日期">
            {{ currentNotice.appointmentDate || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="时间段">
            {{ currentNotice.timeSlot || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="通知内容">
            <div class="detail-content">
              {{ currentNotice.content }}
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Bell } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  getMyNoticeDetail,
  getMyNoticePage,
  getMyUnreadCount,
  markAllNoticesRead,
  markNoticeRead
} from '@/api/notice'

const router = useRouter()

const loading = ref(false)
const notices = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const unreadCount = ref(0)
const detailVisible = ref(false)
const currentNotice = ref(null)

const searchForm = reactive({
  noticeType: '',
  readStatus: null
})

const noticeTypeOptions = [
  { value: 'APPOINTMENT_SUCCESS', label: '预约成功' },
  { value: 'APPOINTMENT_CANCEL', label: '预约取消' },
  { value: 'RESCHEDULE_SUCCESS', label: '改约成功' },
  { value: 'VISIT_REMINDER', label: '就诊提醒' },
  { value: 'SCHEDULE_CANCEL', label: '停诊通知' }
]

const fetchUnreadCount = async () => {
  unreadCount.value = await getMyUnreadCount()
}

const fetchNoticePage = async () => {
  loading.value = true
  try {
    const res = await getMyNoticePage({
      currentPage: currentPage.value,
      size: pageSize.value,
      noticeType: searchForm.noticeType || undefined,
      readStatus: searchForm.readStatus
    })
    notices.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchNoticePage()
}

const resetSearch = () => {
  searchForm.noticeType = ''
  searchForm.readStatus = null
  currentPage.value = 1
  fetchNoticePage()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchNoticePage()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchNoticePage()
}

const handleMarkRead = async (item) => {
  await markNoticeRead(item.id, { successMsg: '通知已标记为已读' })
  await Promise.all([fetchNoticePage(), fetchUnreadCount()])
}

const handleMarkAllRead = async () => {
  await markAllNoticesRead({ successMsg: '已全部标记为已读' })
  await Promise.all([fetchNoticePage(), fetchUnreadCount()])
}

const handleOpenDetail = async (item) => {
  currentNotice.value = await getMyNoticeDetail(item.id)
  detailVisible.value = true
  if (currentNotice.value.readStatus === 0) {
    await markNoticeRead(item.id, { showDefaultMsg: false })
    currentNotice.value.readStatus = 1
    await Promise.all([fetchNoticePage(), fetchUnreadCount()])
  }
}

const getTypeLabel = (type) => {
  return noticeTypeOptions.find(item => item.value === type)?.label || type
}

const getTypeTag = (type) => {
  switch (type) {
    case 'APPOINTMENT_SUCCESS':
      return 'success'
    case 'APPOINTMENT_CANCEL':
      return 'info'
    case 'RESCHEDULE_SUCCESS':
      return 'warning'
    case 'VISIT_REMINDER':
      return 'danger'
    case 'SCHEDULE_CANCEL':
      return 'danger'
    default:
      return 'info'
  }
}

const formatDateTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ')
}

onMounted(async () => {
  try {
    await Promise.all([fetchNoticePage(), fetchUnreadCount()])
  } catch (error) {
    ElMessage.error('加载通知失败')
  }
})
</script>

<style scoped>
.notice-page {
  padding: 20px;
  min-height: calc(100vh - 160px);
}

.welcome-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.banner-content h2 {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 0 8px;
  color: var(--glass-text-primary);
}

.banner-content p {
  margin: 0;
  color: var(--glass-text-secondary);
}

.banner-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.notice-card {
  max-width: 1120px;
  margin: 0 auto;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
}

.filter-item {
  width: 180px;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notice-item {
  padding: 18px 20px;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.notice-item.unread {
  border-color: rgba(93, 141, 247, 0.34) !important;
  background: rgba(255, 255, 255, 0.72) !important;
}

.notice-item:hover {
  transform: translateY(-1px);
  box-shadow: var(--glass-shadow) !important;
}

.notice-item-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.notice-title-wrap {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.notice-title-wrap h3 {
  margin: 0;
  font-size: 16px;
  color: var(--glass-text-primary);
}

.notice-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(138, 152, 171, 0.34);
}

.notice-dot.active {
  background: var(--glass-danger);
}

.notice-time {
  color: var(--glass-text-muted);
  font-size: 13px;
  white-space: nowrap;
}

.notice-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-bottom: 10px;
  color: var(--glass-text-secondary);
  font-size: 13px;
}

.notice-content {
  margin: 0;
  color: var(--glass-text-secondary);
  line-height: 1.7;
}

.notice-actions {
  margin-top: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.detail-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.detail-head h3 {
  margin: 0;
  color: var(--glass-text-primary);
}

.detail-tags {
  display: flex;
  gap: 8px;
}

.detail-content {
  white-space: pre-line;
  line-height: 1.8;
  color: var(--glass-text-secondary);
}

@media (max-width: 768px) {
  .welcome-banner,
  .notice-item-header,
  .detail-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .banner-actions {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
