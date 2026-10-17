<template>
  <div class="statistics-container">
    <el-card
      shadow="hover"
      class="mb-4"
    >
      <template #header>
        <div class="card-header">
          <span><i class="el-icon-data-analysis mr-2" />统计分析</span>
          <el-button
            type="primary"
            class="ml-auto"
            @click="refreshData"
          >
            <i class="el-icon-refresh mr-1" />刷新数据
          </el-button>
        </div>
      </template>
      
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <el-card class="stat-card primary">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-user" />
            </div>
            <div class="stat-info">
              <div class="stat-title">
                患者总数
              </div>
              <div class="stat-value">
                {{ statistics.totalPatients }}
              </div>
            </div>
          </div>
        </el-card>
        
        <el-card class="stat-card success">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-user-filled" />
            </div>
            <div class="stat-info">
              <div class="stat-title">
                医生总数
              </div>
              <div class="stat-value">
                {{ statistics.totalDoctors }}
              </div>
            </div>
          </div>
        </el-card>
        
        <el-card class="stat-card warning">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-document" />
            </div>
            <div class="stat-info">
              <div class="stat-title">
                就诊记录
              </div>
              <div class="stat-value">
                {{ statistics.totalRecords }}
              </div>
            </div>
          </div>
        </el-card>
        
        <el-card class="stat-card danger">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-document-checked" />
            </div>
            <div class="stat-info">
              <div class="stat-title">
                报告总数
              </div>
              <div class="stat-value">
                {{ statistics.totalReports }}
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
    
    <!-- 统计表格 -->
    <div class="tables-container">
      <el-card
        shadow="hover"
        class="mb-4"
      >
        <template #header>
          <div class="card-header">
            <span><i class="el-icon-data-line mr-2" />患者就诊趋势</span>
          </div>
        </template>
        <el-table
          :data="patientTrendData"
          style="width: 100%"
        >
          <el-table-column
            prop="month"
            label="月份"
            width="120"
          />
          <el-table-column
            prop="count"
            label="就诊人数"
          />
        </el-table>
      </el-card>
      
      <div class="table-row">
        <el-card
          shadow="hover"
          class="mb-4 table-col"
        >
          <template #header>
            <div class="card-header">
              <span><i class="el-icon-data-analysis mr-2" />科室就诊分布</span>
            </div>
          </template>
          <el-table
            :data="departmentDistributionData"
            style="width: 100%"
          >
            <el-table-column
              prop="department"
              label="科室"
              width="120"
            />
            <el-table-column
              prop="count"
              label="就诊人数"
            />
            <el-table-column
              prop="percentage"
              label="占比"
            />
          </el-table>
        </el-card>
        
        <el-card
          shadow="hover"
          class="mb-4 table-col"
        >
          <template #header>
            <div class="card-header">
              <span><i class="el-icon-data-line mr-2" />报告类型分布</span>
            </div>
          </template>
          <el-table
            :data="reportTypeData"
            style="width: 100%"
          >
            <el-table-column
              prop="type"
              label="报告类型"
              width="120"
            />
            <el-table-column
              prop="count"
              label="数量"
            />
          </el-table>
        </el-card>
      </div>
      
      <el-card
        shadow="hover"
        class="mb-4"
      >
        <template #header>
          <div class="card-header">
            <span><i class="el-icon-data-analysis mr-2" />随访状态分布</span>
          </div>
        </template>
        <el-table
          :data="followUpStatusData"
          style="width: 100%"
        >
          <el-table-column
            prop="status"
            label="状态"
            width="120"
          />
          <el-table-column
            prop="count"
            label="数量"
          />
          <el-table-column
            prop="percentage"
            label="占比"
          />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

// 统计数据
const statistics = ref({
  totalPatients: 0,
  totalDoctors: 0,
  totalRecords: 0,
  totalReports: 0
})

// 患者就诊趋势数据
const patientTrendData = ref([])

// 科室就诊分布数据
const departmentDistributionData = ref([])

// 报告类型分布数据
const reportTypeData = ref([])

// 随访状态分布数据
const followUpStatusData = ref([
  { status: '已完成', count: 0, percentage: '0%' },
  { status: '进行中', count: 0, percentage: '0%' },
  { status: '未开始', count: 0, percentage: '0%' },
  { status: '已取消', count: 0, percentage: '0%' }
])

// 获取随访状态分布数据
const getFollowUpStatusData = async () => {
  try {
    const data = await request.get('/statistics/follow-up-status-distribution')
    const totalCount = data.reduce((sum, item) => sum + item.value, 0)
    followUpStatusData.value = data.map(item => ({
      status: item.label,
      count: item.value,
      percentage: totalCount > 0 ? `${((item.value / totalCount) * 100).toFixed(2)}%` : '0%'
    }))
  } catch (error) {
    console.error('获取随访状态分布数据失败:', error)
  }
}

// 获取统计概览数据
const getStatisticsOverview = async () => {
  try {
    const data = await request.get('/statistics/overview')
    statistics.value = data
  } catch (error) {
    console.error('获取统计概览数据失败:', error)
  }
}

// 获取患者就诊趋势数据
const getPatientTrendData = async () => {
  try {
    const data = await request.get('/statistics/patient-trend', { months: 6 })
    patientTrendData.value = data.map(item => ({
      month: item.label,
      count: item.value
    }))
  } catch (error) {
    console.error('获取患者就诊趋势数据失败:', error)
  }
}

// 获取科室就诊分布数据
const getDepartmentDistributionData = async () => {
  try {
    const data = await request.get('/statistics/department-distribution')
    departmentDistributionData.value = data.map(item => ({
      department: item.departmentName,
      count: item.count,
      percentage: `${item.percentage}%`
    }))
  } catch (error) {
    console.error('获取科室就诊分布数据失败:', error)
  }
}

// 获取报告类型分布数据
const getReportTypeDistributionData = async () => {
  try {
    const data = await request.get('/statistics/report-type-distribution')
    reportTypeData.value = data.map(item => ({
      type: item.label,
      count: item.value
    }))
  } catch (error) {
    console.error('获取报告类型分布数据失败:', error)
  }
}

// 刷新数据
const refreshData = async () => {
  await getStatisticsOverview()
  await getPatientTrendData()
  await getDepartmentDistributionData()
  await getReportTypeDistributionData()
  await getFollowUpStatusData()
}

// 页面加载时获取数据
onMounted(async () => {
  await refreshData()
})
</script>

<style scoped>
.statistics-container {
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
  min-width: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  min-height: 34px;
  font-size: 18px;
  font-weight: 600;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 18px;
  margin-top: 4px;
}

.stat-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  min-width: 0;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--glass-shadow);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  min-height: 92px;
}

.stat-icon {
  font-size: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 58px;
  height: 58px;
  border-radius: 50%;
  color: var(--glass-primary-strong);
  flex: 0 0 auto;
  border: 1px solid rgba(255, 255, 255, 0.72);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.78);
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-title {
  font-size: 14px;
  color: var(--glass-text-secondary);
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.2;
  color: var(--glass-text-primary);
}

.stat-card.primary .stat-icon {
  background: rgba(93, 141, 247, 0.14);
}

.stat-card.success .stat-icon {
  background: rgba(79, 185, 159, 0.16);
  color: #21836c;
}

.stat-card.warning .stat-icon {
  background: rgba(240, 172, 82, 0.16);
  color: #a66a1f;
}

.stat-card.danger .stat-icon {
  background: rgba(240, 111, 122, 0.16);
  color: #c94e59;
}

.tables-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-top: 0;
  min-width: 0;
}

.table-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
  gap: 24px;
  min-width: 0;
}

.table-col {
  min-width: 0;
}

.tables-container > .el-card,
.table-row > .el-card {
  margin-bottom: 0 !important;
}

:deep(.el-table) {
  width: 100%;
}

:deep(.el-table__body-wrapper),
:deep(.el-table__header-wrapper) {
  min-width: 0;
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .table-row {
    grid-template-columns: 1fr;
  }

  .card-header {
    align-items: flex-start;
  }
}
</style>
