<template>
  <div class="my-medical-records">
    <div class="welcome-banner">
      <div class="banner-content">
        <h2><el-icon><DocumentChecked /></el-icon> 我的就诊记录</h2>
        <p>在这里查看您的历史就诊记录，帮助您更好地了解自己的健康状况</p>
      </div>
    </div>

    <el-card
      class="records-card"
      shadow="hover"
    >
      <!-- 搜索表单 -->
      <div class="search-container">
        <div class="search-header">
          <div class="header-icon">
            <el-icon><Search /></el-icon>
          </div>
          <h4>查询记录</h4>
        </div>
        <el-form
          :model="searchForm"
          :inline="true"
          class="search-form"
        >
          <el-form-item label="就诊日期">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item label="医生姓名">
            <el-input
              v-model="searchForm.doctorName"
              placeholder="请输入医生姓名"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              round
              @click="handleSearch"
            >
              <el-icon><Search /></el-icon>搜索
            </el-button>
            <el-button
              round
              @click="resetSearch"
            >
              <el-icon><Refresh /></el-icon>重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 就诊记录列表 -->
      <div
        v-if="loading"
        class="loading-container"
      >
        <el-skeleton
          :rows="5"
          animated
        />
      </div>
      
      <div
        v-else-if="medicalRecords.length === 0"
        class="empty-container"
      >
        <el-empty
          description="暂无就诊记录"
          :image-size="120"
        >
          <template #image>
            <el-icon class="empty-icon">
              <FirstAidKit />
            </el-icon>
          </template>
        </el-empty>
      </div>
      
      <div
        v-else
        class="records-container"
      >
        <el-timeline>
          <el-timeline-item
            v-for="record in medicalRecords"
            :key="record.id"
            :timestamp="record.recordDate"
            placement="top"
            :type="getTimelineItemType(record)"
          >
            <el-card class="record-card">
              <template #header>
                <div class="record-header">
                  <div class="record-title">
                    <span class="record-no">{{ record.recordNo }}</span>
                    <el-tag
                      size="small"
                      effect="plain"
                    >
                      {{ record.doctor?.department?.deptName || '未知科室' }}
                    </el-tag>
                  </div>
                  <div class="record-actions">
                    <el-button
                      type="primary"
                      size="small"
                      @click="handleViewRecord(record)"
                    >
                      查看详情
                    </el-button>
                  </div>
                </div>
              </template>
              
              <el-descriptions
                :column="2"
                border
                size="small"
              >
                <el-descriptions-item label="就诊医生">
                  {{ record.doctor?.name || '未知' }}
                  <el-tag size="small">
                    {{ record.doctor?.title || '' }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="就诊日期">
                  {{ record.recordDate }}
                </el-descriptions-item>
                <el-descriptions-item
                  label="诊断结果"
                  :span="2"
                >
                  {{ record.diagnosis || '暂无诊断' }}
                </el-descriptions-item>
                <el-descriptions-item
                  label="治疗方案"
                  :span="2"
                >
                  {{ record.treatment || '暂无治疗方案' }}
                </el-descriptions-item>
                <el-descriptions-item
                  v-if="record.followUp"
                  label="随访日期"
                >
                  {{ record.followUp }}
                </el-descriptions-item>
              </el-descriptions>
              
              <div
                v-if="record.prescriptions && record.prescriptions.length > 0"
                class="prescription-section"
              >
                <div class="section-divider">
                  <span>处方信息</span>
                </div>
                
                <el-collapse>
                  <el-collapse-item 
                    v-for="prescription in record.prescriptions" 
                    :key="prescription.id"
                    :title="`处方 ${prescription.prescriptionNo} (${prescription.prescriptionDate})`"
                  >
                    <div class="prescription-info">
                      <div class="prescription-header">
                        <div>
                          <el-tag :type="prescription.status === 1 ? 'success' : 'warning'">
                            {{ prescription.status === 1 ? '已取药' : '未取药' }}
                          </el-tag>
                        </div>
                        <div>
                          <el-button
                            type="primary"
                            size="small"
                            @click="handleViewPrescription(prescription)"
                          >
                            查看处方详情
                          </el-button>
                        </div>
                      </div>
                      
                      <div
                        v-if="prescription.details && prescription.details.length > 0"
                        class="medicine-list"
                      >
                        <el-table
                          :data="prescription.details"
                          border
                          style="width: 100%"
                        >
                          <el-table-column
                            prop="medicine.medicineName"
                            label="药品名称"
                          />
                          <el-table-column
                            prop="dosage"
                            label="用量"
                          />
                          <el-table-column
                            prop="frequency"
                            label="频次"
                          />
                          <el-table-column
                            prop="days"
                            label="用药天数"
                          />
                          <el-table-column
                            prop="usage"
                            label="用法"
                          />
                        </el-table>
                      </div>
                      <div
                        v-else
                        class="no-medicine"
                      >
                        <el-empty
                          description="暂无药品信息"
                          :image-size="60"
                        />
                      </div>
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>

      <!-- 分页 -->
      <div
        v-if="medicalRecords.length > 0"
        class="pagination-container"
      >
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 健康提示卡片 -->
    <el-card
      class="health-tips-card"
      shadow="hover"
    >
      <div class="tips-header">
        <el-icon><InfoFilled /></el-icon>
        <h4>健康小贴士</h4>
      </div>
      <ul class="tips-list">
        <li>定期查看您的就诊记录，有助于了解自己的健康状况变化</li>
        <li>按时服药、遵医嘱治疗是康复的关键</li>
        <li>建议保存您的历史就诊记录，方便日后就医参考</li>
        <li>如有疑问，请随时咨询您的主治医生</li>
      </ul>
    </el-card>

    <!-- 就诊记录详情对话框 -->
    <el-dialog
      v-model="recordDialogVisible"
      title="就诊记录详情"
      width="650px"
      class="record-dialog"
    >
      <div
        v-if="currentRecord.id"
        class="record-detail"
      >
        <div class="dialog-header-info">
          <div class="header-icon">
            <el-icon><DocumentChecked /></el-icon>
          </div>
          <div class="header-content">
            <h3>{{ currentRecord.recordNo }}</h3>
            <p>{{ currentRecord.recordDate }}</p>
          </div>
        </div>

        <el-descriptions
          title="基本信息"
          :column="2"
          border
        >
          <el-descriptions-item label="就诊医生">
            <div class="info-with-icon">
              <el-icon><User /></el-icon>
              {{ currentRecord.doctor?.name || '未知' }}
              <el-tag
                size="small"
                type="info"
              >
                {{ currentRecord.doctor?.title || '' }}
              </el-tag>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="科室">
            <div class="info-with-icon">
              <el-icon><OfficeBuilding /></el-icon>
              {{ currentRecord.doctor?.department?.deptName || '未知科室' }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item
            label="诊断结果"
            :span="2"
          >
            <div class="diagnosis-box">
              {{ currentRecord.diagnosis || '暂无诊断' }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item
            label="治疗方案"
            :span="2"
          >
            <div class="treatment-box">
              {{ currentRecord.treatment || '暂无治疗方案' }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item
            v-if="currentRecord.followUp"
            label="随访日期"
          >
            <div class="info-with-icon">
              <el-icon><Calendar /></el-icon>
              {{ currentRecord.followUp }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item
            v-if="currentRecord.notes"
            label="医生备注"
            :span="2"
          >
            <div class="notes-box">
              {{ currentRecord.notes }}
            </div>
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="section-divider">
          <span>处方信息</span>
        </div>
        
        <div
          v-if="currentRecord.prescriptions && currentRecord.prescriptions.length > 0"
          class="prescription-list"
        >
          <el-table
            :data="currentRecord.prescriptions"
            border
            style="width: 100%"
            :row-class-name="tableRowClassName"
          >
            <el-table-column
              prop="prescriptionNo"
              label="处方编号"
              width="180"
            />
            <el-table-column
              prop="prescriptionDate"
              label="处方日期"
              width="120"
            />
            <el-table-column
              prop="status"
              label="状态"
              width="100"
            >
              <template #default="scope">
                <el-tag
                  :type="scope.row.status === 1 ? 'success' : 'warning'"
                  effect="light"
                  round
                >
                  {{ scope.row.status === 1 ? '已取药' : '未取药' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              width="120"
            >
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  round
                  @click="handleViewPrescription(scope.row)"
                >
                  <el-icon><View /></el-icon>查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div
          v-else
          class="no-prescription"
        >
          <el-empty
            description="暂无处方信息"
            :image-size="80"
          />
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button
            round
            @click="recordDialogVisible = false"
          >关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 处方详情对话框 -->
    <el-dialog
      v-model="prescriptionDialogVisible"
      title="处方详情"
      width="700px"
      class="prescription-dialog"
    >
      <div
        v-if="currentPrescription.id"
        class="prescription-detail"
      >
        <div class="dialog-header-info">
          <div class="header-icon">
            <el-icon><Stamp /></el-icon>
          </div>
          <div class="header-content">
            <h3>{{ currentPrescription.prescriptionNo }}</h3>
            <p>{{ currentPrescription.prescriptionDate }}</p>
          </div>
        </div>

        <el-descriptions
          title="处方信息"
          :column="2"
          border
        >
          <el-descriptions-item label="开方医生">
            <div class="info-with-icon">
              <el-icon><User /></el-icon>
              {{ currentPrescription.doctor?.name || '未知' }}
              <el-tag
                size="small"
                type="info"
              >
                {{ currentPrescription.doctor?.title || '' }}
              </el-tag>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag
              :type="currentPrescription.status === 1 ? 'success' : 'warning'"
              effect="light"
              round
            >
              <el-icon v-if="currentPrescription.status === 1">
                <CircleCheckFilled />
              </el-icon>
              <el-icon v-else>
                <Clock />
              </el-icon>
              {{ currentPrescription.status === 1 ? '已取药' : '未取药' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item
            label="诊断结果"
            :span="2"
          >
            <div class="diagnosis-box">
              {{ currentPrescription.diagnosis || '同就诊记录' }}
            </div>
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="section-divider">
          <span>药品明细</span>
        </div>
        
        <div
          v-if="currentPrescription.details && currentPrescription.details.length > 0"
          class="medicine-detail"
        >
          <el-table
            :data="currentPrescription.details"
            border
            style="width: 100%"
            :row-class-name="tableRowClassName"
          >
            <el-table-column
              prop="medicine.medicineName"
              label="药品名称"
            />
            <el-table-column
              prop="medicine.specification"
              label="规格"
            />
            <el-table-column
              prop="dosage"
              label="用量"
            />
            <el-table-column
              prop="frequency"
              label="频次"
            />
            <el-table-column
              prop="days"
              label="用药天数"
            />
            <el-table-column
              prop="usage"
              label="用法"
            />
            <el-table-column
              prop="quantity"
              label="数量"
            />
          </el-table>
        </div>
        <div
          v-else
          class="no-medicine"
        >
          <el-empty
            description="暂无药品信息"
            :image-size="80"
          />
        </div>
        
        <div
          v-if="currentPrescription.notes"
          class="prescription-notes"
        >
          <div class="section-divider">
            <span>处方备注</span>
          </div>
          <div class="notes-box">
            {{ currentPrescription.notes }}
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button
            round
            @click="prescriptionDialogVisible = false"
          >关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { 
  Search, 
  Refresh, 
  FirstAidKit, 
  InfoFilled, 
  DocumentChecked, 
  Calendar, 
  User,
  Clock,
  OfficeBuilding,
  Stamp,
  CircleCheckFilled,
  View
} from '@element-plus/icons-vue'

// 用户信息
const userStore = useUserStore()
const patientId = computed(() => userStore.patientInfo?.id)
// 列表数据
const allMedicalRecords = ref([])
const medicalRecords = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 搜索表单
const searchForm = reactive({
  doctorName: '',
  startDate: '',
  endDate: ''
})

// 日期范围
const dateRange = ref([])

// 对话框
const recordDialogVisible = ref(false)
const prescriptionDialogVisible = ref(false)
const currentRecord = ref({})
const currentPrescription = ref({})

// 监听日期范围变化
watch(dateRange, (val) => {
  if (val && val.length === 2) {
    searchForm.startDate = val[0]
    searchForm.endDate = val[1]
  } else {
    searchForm.startDate = ''
    searchForm.endDate = ''
  }
})

// 初始化
onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchMyMedicalRecords()
  }
})

// 获取我的就诊记录
const fetchMyMedicalRecords = async () => {
  loading.value = true
  try {
    const records = await request.get('/medical-record/my', {}, {
      showDefaultMsg: false
    })
    allMedicalRecords.value = Array.isArray(records) ? records : []
    await loadPatientPrescriptions(allMedicalRecords.value)
    applyRecordFilters()
  } catch (error) {
    console.error('获取就诊记录失败:', error)
    ElMessage.error('获取就诊记录失败')
  } finally {
    loading.value = false
  }
}

// 加载当前患者的处方信息，并按就诊记录关联到列表中
const loadPatientPrescriptions = async (records) => {
  if (!records.length) return

  const activePatientId = patientId.value || records.find(record => record.patientId)?.patientId
  if (!activePatientId) return

  try {
    const prescriptions = await request.get(`/prescription/patient/${activePatientId}`, {}, {
      showDefaultMsg: false
    })
    const prescriptionGroups = new Map()

    ;(Array.isArray(prescriptions) ? prescriptions : []).forEach(prescription => {
      if (!prescription.recordId) return
      const group = prescriptionGroups.get(prescription.recordId) || []
      group.push(prescription)
      prescriptionGroups.set(prescription.recordId, group)
    })

    records.forEach(record => {
      record.prescriptions = prescriptionGroups.get(record.id) || []
    })
  } catch (error) {
    console.error('获取处方信息失败:', error)
    records.forEach(record => {
      if (!record.prescriptions) {
        record.prescriptions = []
      }
    })
  }
}

// 本地筛选和分页
const applyRecordFilters = () => {
  const doctorName = searchForm.doctorName.trim().toLowerCase()
  const filteredRecords = allMedicalRecords.value.filter(record => {
    const recordDoctorName = (record.doctor?.name || '').toLowerCase()
    const matchesDoctor = !doctorName || recordDoctorName.includes(doctorName)
    const matchesStartDate = !searchForm.startDate || record.recordDate >= searchForm.startDate
    const matchesEndDate = !searchForm.endDate || record.recordDate <= searchForm.endDate
    return matchesDoctor && matchesStartDate && matchesEndDate
  })

  total.value = filteredRecords.length
  const start = (currentPage.value - 1) * pageSize.value
  medicalRecords.value = filteredRecords.slice(start, start + pageSize.value)

  if (medicalRecords.value.length === 0 && currentPage.value > 1) {
    currentPage.value = 1
    medicalRecords.value = filteredRecords.slice(0, pageSize.value)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  applyRecordFilters()
}

// 重置搜索
const resetSearch = () => {
  searchForm.doctorName = ''
  dateRange.value = []
  currentPage.value = 1
  applyRecordFilters()
}

// 查看就诊记录详情
const handleViewRecord = (record) => {
  currentRecord.value = record
  recordDialogVisible.value = true
}

// 查看处方详情
const handleViewPrescription = (prescription) => {
  currentPrescription.value = prescription
  prescriptionDialogVisible.value = true
}

// 根据记录类型获取时间线类型
const getTimelineItemType = (record) => {
  if (record.followUp) {
    const today = new Date()
    const followUpDate = new Date(record.followUp)
    
    if (followUpDate > today) {
      return 'warning' // 即将随访
    } else if (followUpDate.toDateString() === today.toDateString()) {
      return 'success' // 今天随访
    }
  }
  
  return 'primary' // 默认类型
}

// 分页操作
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  applyRecordFilters()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  applyRecordFilters()
}

// 表格行样式
const tableRowClassName = ({ rowIndex }) => {
  if (rowIndex % 2 === 0) {
    return 'even-row'
  }
  return 'odd-row'
}
</script>

<style>
.my-medical-records {
  padding: 20px;
}

.welcome-banner {
  padding: 26px 30px;
  margin-bottom: 25px;
}

.banner-content {
  max-width: 800px;
  text-align: center;
  margin: 0 auto;
}

.banner-content h2 {
  color: var(--glass-text-primary);
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
}

.banner-content p {
  color: var(--glass-text-secondary);
  font-size: 16px;
  margin: 0;
}

.records-card {
  margin-bottom: 25px;
}

.search-container {
  padding: 15px;
  margin-bottom: 20px;
  border-left: 3px solid rgba(93, 141, 247, 0.42);
}

.search-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  color: var(--glass-text-primary);
}

.header-icon {
  margin-right: 10px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(93, 141, 247, 0.12);
  color: var(--glass-primary-strong);
}

.search-form .el-form-item {
  margin-bottom: 10px;
}

.search-form .el-button {
  padding: 10px 20px;
}

.records-container {
  padding: 10px;
}

.record-card {
  margin-bottom: 15px;
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.record-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--glass-shadow) !important;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.record-title {
  display: flex;
  align-items: center;
}

.record-no {
  font-weight: bold;
  margin-right: 10px;
  color: var(--glass-text-primary);
}

.record-actions .el-button {
  border-radius: 20px;
}

.section-divider {
  display: flex;
  align-items: center;
  margin: 15px 0;
  color: var(--glass-text-secondary);
}

.section-divider:before,
.section-divider:after {
  content: "";
  flex: 1;
  height: 1px;
  background-color: rgba(181, 204, 232, 0.32);
}

.section-divider:before {
  margin-right: 10px;
}

.section-divider:after {
  margin-left: 10px;
}

.prescription-section {
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px dashed rgba(181, 204, 232, 0.32);
}

.prescription-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.medicine-list {
  margin-top: 10px;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

.empty-container {
  padding: 40px 0;
  text-align: center;
}

.empty-icon {
  font-size: 60px;
}

.loading-container {
  padding: 20px;
}

.health-tips-card {
  margin-top: 25px;
}

.tips-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  color: var(--glass-text-primary);
  border-bottom: 1px dashed rgba(181, 204, 232, 0.32);
  padding-bottom: 10px;
}

.tips-header .el-icon {
  margin-right: 10px;
  color: var(--glass-primary-strong);
  font-size: 24px;
}

.tips-header h4 {
  margin: 0;
  font-size: 18px;
}

.tips-list {
  list-style: none;
  padding-left: 0;
}

.tips-list li {
  margin-bottom: 12px;
  padding-left: 25px;
  position: relative;
  color: var(--glass-text-secondary);
}

.tips-list li:before {
  content: "•";
  position: absolute;
  left: 8px;
  color: var(--glass-primary);
  font-size: 18px;
}

.notes-content {
  white-space: pre-line;
  background-color: rgba(255, 255, 255, 0.5);
  padding: 10px;
  border-radius: 10px;
  border-left: 3px solid rgba(93, 141, 247, 0.42);
}

/* 对话框样式 */
:deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  padding: 15px 20px;
}

:deep(.el-dialog__title) {
  color: var(--glass-text-primary);
  font-weight: 600;
}

:deep(.el-descriptions__title) {
  color: var(--glass-text-primary);
}

:deep(.el-descriptions__label) {
  color: var(--glass-text-secondary);
}

:deep(.el-descriptions__content) {
  color: var(--glass-text-secondary);
}

/* 对话框样式 */
.dialog-header-info {
  display: flex;
  margin-bottom: 20px;
  align-items: center;
}

.dialog-header-info .header-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  background: rgba(93, 141, 247, 0.12);
  color: var(--glass-primary-strong);
  font-size: 24px;
}

.dialog-header-info .header-content h3 {
  margin: 0 0 5px 0;
  color: var(--glass-text-primary);
  font-size: 18px;
}

.dialog-header-info .header-content p {
  margin: 0;
  color: var(--glass-text-secondary);
  font-size: 14px;
}

.info-with-icon {
  display: flex;
  align-items: center;
}

.info-with-icon .el-icon {
  margin-right: 5px;
  color: var(--glass-primary-strong);
}

.diagnosis-box, .treatment-box, .notes-box {
  background-color: rgba(255, 255, 255, 0.5);
  padding: 10px 15px;
  border-radius: 10px;
  border-left: 3px solid rgba(93, 141, 247, 0.42);
  color: var(--glass-text-secondary);
  line-height: 1.6;
}

.diagnosis-box {
  border-left-color: rgba(240, 172, 82, 0.52);
}

.treatment-box {
  border-left-color: rgba(79, 185, 159, 0.52);
}

.notes-box {
  border-left-color: rgba(93, 141, 247, 0.42);
}

.prescription-list, .medicine-detail {
  margin: 15px 0;
}

/* 表格样式 */
:deep(.el-table) {
  border-radius: 14px;
  overflow: hidden;
}

:deep(.el-table th) {
  color: var(--glass-text-primary);
  font-weight: 600;
}

:deep(.el-table .even-row) {
  background-color: rgba(248, 251, 255, 0.68);
}

:deep(.el-table .odd-row) {
  background-color: #ffffff;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background-color: rgba(93, 141, 247, 0.08);
}

/* 对话框底部按钮 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer .el-button {
  min-width: 100px;
}

/* 空状态 */
.no-prescription, .no-medicine {
  display: flex;
  justify-content: center;
  padding: 30px 0;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .welcome-banner {
    padding: 20px;
  }
  
  .banner-content h2 {
    font-size: 22px;
  }
  
  .banner-content p {
    font-size: 14px;
  }
  
  .search-container {
    padding: 10px;
  }
  
  .el-form-item {
    margin-right: 0 !important;
  }
  
  .dialog-header-info .header-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }
  
  .dialog-header-info .header-content h3 {
    font-size: 16px;
  }
  
  .info-with-icon {
    flex-wrap: wrap;
  }
}
</style> 
