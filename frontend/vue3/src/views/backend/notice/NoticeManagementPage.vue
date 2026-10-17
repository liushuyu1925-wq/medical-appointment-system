<template>
  <div class="notice-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <h3>通知管理</h3>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane
          label="通知记录"
          name="notice"
        >
          <el-form
            :inline="true"
            :model="noticeSearchForm"
            class="search-form"
          >
            <el-form-item label="接收人">
              <el-input
                v-model="noticeSearchForm.recipientKeyword"
                placeholder="姓名/用户名/手机号"
                clearable
              />
            </el-form-item>
            <el-form-item label="通知类型">
              <el-select
                v-model="noticeSearchForm.noticeType"
                placeholder="通知类型"
                clearable
              >
                <el-option
                  v-for="item in noticeTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="读取状态">
              <el-select
                v-model="noticeSearchForm.readStatus"
                placeholder="读取状态"
                clearable
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
            </el-form-item>
            <el-form-item label="发送状态">
              <el-select
                v-model="noticeSearchForm.sendStatus"
                placeholder="发送状态"
                clearable
              >
                <el-option
                  label="成功"
                  :value="1"
                />
                <el-option
                  label="失败"
                  :value="0"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="noticeDateRange"
                type="daterange"
                value-format="YYYY-MM-DD"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                @click="handleNoticeSearch"
              >
                查询
              </el-button>
              <el-button @click="resetNoticeSearch">
                重置
              </el-button>
            </el-form-item>
          </el-form>

          <el-table
            v-loading="noticeLoading"
            :data="noticeTableData"
            border
          >
            <el-table-column
              prop="title"
              label="标题"
              min-width="160"
            />
            <el-table-column
              prop="recipientName"
              label="接收人"
              width="120"
            />
            <el-table-column
              label="通知类型"
              width="140"
            >
              <template #default="{ row }">
                <el-tag :type="getNoticeTypeTag(row.noticeType)">
                  {{ getNoticeTypeLabel(row.noticeType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="doctorName"
              label="医生"
              width="120"
            />
            <el-table-column
              prop="departmentName"
              label="科室"
              width="120"
            />
            <el-table-column
              prop="appointmentNo"
              label="预约编号"
              width="160"
            />
            <el-table-column
              label="读取状态"
              width="100"
            >
              <template #default="{ row }">
                <el-tag :type="row.readStatus === 1 ? 'info' : 'danger'">
                  {{ row.readStatus === 1 ? '已读' : '未读' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="发送状态"
              width="100"
            >
              <template #default="{ row }">
                <el-tag :type="row.sendStatus === 1 ? 'success' : 'danger'">
                  {{ row.sendStatus === 1 ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="createTime"
              label="创建时间"
              width="180"
            />
            <el-table-column
              label="操作"
              width="160"
              fixed="right"
            >
              <template #default="{ row }">
                <el-button
                  type="primary"
                  size="small"
                  @click="handleOpenNoticeDetail(row.id)"
                >
                  详情
                </el-button>
                <el-popconfirm
                  title="确定删除此通知吗？"
                  @confirm="handleDeleteNotice(row.id)"
                >
                  <template #reference>
                    <el-button
                      type="danger"
                      size="small"
                    >
                      删除
                    </el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              :current-page="noticeCurrentPage"
              :page-size="noticePageSize"
              :page-sizes="[10, 20, 50]"
              background
              layout="total, sizes, prev, pager, next, jumper"
              :total="noticeTotal"
              @current-change="handleNoticeCurrentChange"
              @size-change="handleNoticeSizeChange"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane
          label="审计日志"
          name="log"
        >
          <el-form
            :inline="true"
            :model="logSearchForm"
            class="search-form"
          >
            <el-form-item label="接收人">
              <el-input
                v-model="logSearchForm.recipientKeyword"
                placeholder="姓名/用户名/手机号"
                clearable
              />
            </el-form-item>
            <el-form-item label="操作类型">
              <el-select
                v-model="logSearchForm.actionType"
                placeholder="操作类型"
                clearable
              >
                <el-option
                  v-for="item in logTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="执行结果">
              <el-select
                v-model="logSearchForm.actionResult"
                placeholder="执行结果"
                clearable
              >
                <el-option
                  label="成功"
                  value="SUCCESS"
                />
                <el-option
                  label="失败"
                  value="FAILED"
                />
                <el-option
                  label="跳过"
                  value="SKIPPED"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="logDateRange"
                type="daterange"
                value-format="YYYY-MM-DD"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                @click="handleLogSearch"
              >
                查询
              </el-button>
              <el-button @click="resetLogSearch">
                重置
              </el-button>
            </el-form-item>
          </el-form>

          <el-table
            v-loading="logLoading"
            :data="logTableData"
            border
          >
            <el-table-column
              prop="noticeTitle"
              label="关联通知"
              min-width="160"
            />
            <el-table-column
              prop="recipientName"
              label="接收人"
              width="120"
            />
            <el-table-column
              prop="actionType"
              label="操作类型"
              width="150"
            >
              <template #default="{ row }">
                {{ getLogTypeLabel(row.actionType) }}
              </template>
            </el-table-column>
            <el-table-column
              label="执行结果"
              width="100"
            >
              <template #default="{ row }">
                <el-tag :type="getLogResultTag(row.actionResult)">
                  {{ row.actionResult }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="operatorName"
              label="操作人"
              width="120"
            />
            <el-table-column
              prop="taskBatchNo"
              label="任务批次"
              width="180"
            />
            <el-table-column
              prop="createTime"
              label="操作时间"
              width="180"
            />
            <el-table-column
              label="操作"
              width="100"
              fixed="right"
            >
              <template #default="{ row }">
                <el-button
                  type="primary"
                  size="small"
                  @click="handleOpenLogDetail(row.id)"
                >
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              :current-page="logCurrentPage"
              :page-size="logPageSize"
              :page-sizes="[10, 20, 50]"
              background
              layout="total, sizes, prev, pager, next, jumper"
              :total="logTotal"
              @current-change="handleLogCurrentChange"
              @size-change="handleLogSizeChange"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog
      v-model="noticeDetailVisible"
      title="通知详情"
      width="700px"
    >
      <el-descriptions
        v-if="currentNotice"
        :column="1"
        border
      >
        <el-descriptions-item label="标题">
          {{ currentNotice.title }}
        </el-descriptions-item>
        <el-descriptions-item label="接收人">
          {{ currentNotice.recipientName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="通知类型">
          {{ getNoticeTypeLabel(currentNotice.noticeType) }}
        </el-descriptions-item>
        <el-descriptions-item label="医生">
          {{ currentNotice.doctorName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="科室">
          {{ currentNotice.departmentName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="预约编号">
          {{ currentNotice.appointmentNo || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="就诊日期">
          {{ currentNotice.appointmentDate || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="时间段">
          {{ currentNotice.timeSlot || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="读取状态">
          {{ currentNotice.readStatus === 1 ? '已读' : '未读' }}
        </el-descriptions-item>
        <el-descriptions-item label="发送状态">
          {{ currentNotice.sendStatus === 1 ? '成功' : '失败' }}
        </el-descriptions-item>
        <el-descriptions-item label="内容">
          {{ currentNotice.content }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog
      v-model="logDetailVisible"
      title="审计日志详情"
      width="700px"
    >
      <el-descriptions
        v-if="currentLog"
        :column="1"
        border
      >
        <el-descriptions-item label="关联通知">
          {{ currentLog.noticeTitle || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="接收人">
          {{ currentLog.recipientName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="操作类型">
          {{ getLogTypeLabel(currentLog.actionType) }}
        </el-descriptions-item>
        <el-descriptions-item label="执行结果">
          {{ currentLog.actionResult }}
        </el-descriptions-item>
        <el-descriptions-item label="操作人">
          {{ currentLog.operatorName || currentLog.operatorRole || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="任务批次">
          {{ currentLog.taskBatchNo || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="说明">
          {{ currentLog.actionMessage || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">
          {{ currentLog.createTime || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getAdminNoticeDetail,
  getAdminNoticePage,
  getNoticeLogDetail,
  getNoticeLogPage,
  deleteNotice
} from '@/api/notice'

const activeTab = ref('notice')

const noticeLoading = ref(false)
const noticeTableData = ref([])
const noticeCurrentPage = ref(1)
const noticePageSize = ref(10)
const noticeTotal = ref(0)
const noticeDateRange = ref([])
const noticeDetailVisible = ref(false)
const currentNotice = ref(null)

const logLoading = ref(false)
const logTableData = ref([])
const logCurrentPage = ref(1)
const logPageSize = ref(10)
const logTotal = ref(0)
const logDateRange = ref([])
const logDetailVisible = ref(false)
const currentLog = ref(null)

const noticeSearchForm = reactive({
  recipientKeyword: '',
  noticeType: '',
  readStatus: null,
  sendStatus: null
})

const logSearchForm = reactive({
  recipientKeyword: '',
  actionType: '',
  actionResult: ''
})

const noticeTypeOptions = [
  { value: 'APPOINTMENT_SUCCESS', label: '预约成功' },
  { value: 'APPOINTMENT_CANCEL', label: '预约取消' },
  { value: 'RESCHEDULE_SUCCESS', label: '改约成功' },
  { value: 'VISIT_REMINDER', label: '就诊提醒' },
  { value: 'SCHEDULE_CANCEL', label: '停诊通知' }
]

const logTypeOptions = [
  { value: 'NOTICE_CREATE', label: '通知生成' },
  { value: 'NOTICE_READ', label: '通知已读' },
  { value: 'NOTICE_READ_ALL', label: '全部已读' },
  { value: 'REMINDER_SCAN', label: '提醒任务扫描' },
  { value: 'REMINDER_CREATE', label: '提醒通知生成' },
  { value: 'SCHEDULE_CANCEL_SCAN', label: '停诊通知扫描' }
]

const getDateRangeParams = (dateRange) => {
  if (!dateRange || dateRange.length !== 2) {
    return {}
  }
  return {
    startTime: `${dateRange[0]} 00:00:00`,
    endTime: `${dateRange[1]} 23:59:59`
  }
}

const fetchNoticePage = async () => {
  noticeLoading.value = true
  try {
    const res = await getAdminNoticePage({
      currentPage: noticeCurrentPage.value,
      size: noticePageSize.value,
      recipientKeyword: noticeSearchForm.recipientKeyword || undefined,
      noticeType: noticeSearchForm.noticeType || undefined,
      readStatus: noticeSearchForm.readStatus,
      sendStatus: noticeSearchForm.sendStatus,
      ...getDateRangeParams(noticeDateRange.value)
    })
    noticeTableData.value = res.records || []
    noticeTotal.value = res.total || 0
  } finally {
    noticeLoading.value = false
  }
}

const fetchLogPage = async () => {
  logLoading.value = true
  try {
    const res = await getNoticeLogPage({
      currentPage: logCurrentPage.value,
      size: logPageSize.value,
      recipientKeyword: logSearchForm.recipientKeyword || undefined,
      actionType: logSearchForm.actionType || undefined,
      actionResult: logSearchForm.actionResult || undefined,
      ...getDateRangeParams(logDateRange.value)
    })
    logTableData.value = res.records || []
    logTotal.value = res.total || 0
  } finally {
    logLoading.value = false
  }
}

const handleNoticeSearch = () => {
  noticeCurrentPage.value = 1
  fetchNoticePage()
}

const resetNoticeSearch = () => {
  noticeSearchForm.recipientKeyword = ''
  noticeSearchForm.noticeType = ''
  noticeSearchForm.readStatus = null
  noticeSearchForm.sendStatus = null
  noticeDateRange.value = []
  noticeCurrentPage.value = 1
  fetchNoticePage()
}

const handleNoticeCurrentChange = (page) => {
  noticeCurrentPage.value = page
  fetchNoticePage()
}

const handleNoticeSizeChange = (size) => {
  noticePageSize.value = size
  noticeCurrentPage.value = 1
  fetchNoticePage()
}

const handleLogSearch = () => {
  logCurrentPage.value = 1
  fetchLogPage()
}

const resetLogSearch = () => {
  logSearchForm.recipientKeyword = ''
  logSearchForm.actionType = ''
  logSearchForm.actionResult = ''
  logDateRange.value = []
  logCurrentPage.value = 1
  fetchLogPage()
}

const handleLogCurrentChange = (page) => {
  logCurrentPage.value = page
  fetchLogPage()
}

const handleLogSizeChange = (size) => {
  logPageSize.value = size
  logCurrentPage.value = 1
  fetchLogPage()
}

const handleOpenNoticeDetail = async (id) => {
  currentNotice.value = await getAdminNoticeDetail(id)
  noticeDetailVisible.value = true
}

const handleOpenLogDetail = async (id) => {
  currentLog.value = await getNoticeLogDetail(id)
  logDetailVisible.value = true
}

// 删除通知
const handleDeleteNotice = async (id) => {
  try {
    await deleteNotice(id, {
      successMsg: '通知删除成功',
      onSuccess: () => {
        fetchNoticePage()
      }
    })
  } catch (error) {
    console.error('删除通知失败:', error)
  }
}

const getNoticeTypeLabel = (type) => {
  return noticeTypeOptions.find(item => item.value === type)?.label || type
}

const getNoticeTypeTag = (type) => {
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

const getLogTypeLabel = (type) => {
  return logTypeOptions.find(item => item.value === type)?.label || type
}

const getLogResultTag = (result) => {
  switch (result) {
    case 'SUCCESS':
      return 'success'
    case 'FAILED':
      return 'danger'
    case 'SKIPPED':
      return 'warning'
    default:
      return 'info'
  }
}

onMounted(async () => {
  try {
    await Promise.all([fetchNoticePage(), fetchLogPage()])
  } catch (error) {
    ElMessage.error('加载通知管理数据失败')
  }
})
</script>

<style scoped>
.notice-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
