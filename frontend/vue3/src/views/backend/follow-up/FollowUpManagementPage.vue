<template>
  <div class="follow-up-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <h3>随访记录管理</h3>
          <el-button
            type="primary"
            @click="handleAddFollowUp"
          >
            新增随访记录
          </el-button>
        </div>
      </template>

      <el-form
        :inline="true"
        :model="searchForm"
        class="search-form"
      >
        <el-form-item label="就诊记录ID">
          <el-input
            v-model="searchForm.recordId"
            placeholder="就诊记录ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="患者ID">
          <el-input
            v-model="searchForm.patientId"
            placeholder="患者ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="医生ID">
          <el-input
            v-model="searchForm.doctorId"
            placeholder="医生ID"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="handleSearch"
          >
            查询
          </el-button>
          <el-button @click="resetSearch">
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
      >
        <el-table-column
          prop="patient"
          label="患者"
          width="100"
        >
          <template #default="{ row }">
            {{ patientList.find(p => p.id === row.patientId)?.name || row.patientId }}
          </template>
        </el-table-column>
        <el-table-column
          label="医生"
          width="120"
        >
          <template #default="{ row }">
            {{ doctorList.find(d => d.id === row.doctorId)?.name || row.doctorId }}
          </template>
        </el-table-column>
        <el-table-column
          prop="followUpDate"
          label="随访日期"
          width="180"
        />
        <el-table-column
          prop="followUpType"
          label="随访类型"
          width="120"
        />
        <el-table-column
          prop="followUpContent"
          label="随访内容"
          min-width="200"
        />
        <el-table-column
          label="完成状态"
          width="120"
        >
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'success' : 'warning'">
              {{ row.status === 2 ? '已完成' : '未完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="nextFollowUpDate"
          label="下次随访日期"
          width="180"
        />
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
            <div style="display: flex; flex-direction: column; gap: 8px;">
              <div style="display: flex; gap: 8px;">
                <el-button
                  size="small"
                  type="primary"
                  style="width: 66px;"
                  @click="handleEditFollowUp(row.id)"
                >
                  编辑
                </el-button>
                <el-button
                  size="small"
                  type="success"
                  style="width: 66px;"
                  @click="handleViewFollowUp(row.id)"
                >
                  详情
                </el-button>
              </div>
              <div style="display: flex; gap: 8px;">
                <el-button 
                  size="small" 
                  :type="row.status === 2 ? 'success' : 'warning'" 
                  style="width: 66px;"
                  @click="handleCompleteFollowUp(row.id)"
                >
                  {{ row.status === 2 ? '已完成' : '未完成' }}
                </el-button>
                <el-popconfirm
                  title="确定删除此随访记录吗？"
                  @confirm="handleDeleteFollowUp(row.id)"
                >
                  <template #reference>
                    <el-button
                      size="small"
                      type="danger"
                      style="width: 66px;"
                    >
                      删除
                    </el-button>
                  </template>
                </el-popconfirm>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        :model="form"
        label-width="120px"
      >
        <el-form-item
          label="患者"
          required
        >
          <el-select
            v-model="form.patientId"
            placeholder="请选择患者"
          >
            <el-option
              v-for="patient in patientList"
              :key="patient.id"
              :label="patient.name"
              :value="patient.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          label="医生"
          required
        >
          <el-select
            v-model="form.doctorId"
            placeholder="请选择医生"
          >
            <el-option
              v-for="doctor in doctorList"
              :key="doctor.id"
              :label="doctor.name"
              :value="doctor.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          label="随访日期"
          required
        >
          <el-date-picker
            v-model="form.followUpDate"
            type="date"
            placeholder="选择随访日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item
          label="随访类型"
          required
        >
          <el-input
            v-model="form.followUpType"
            placeholder="请输入随访类型"
          />
        </el-form-item>
        <el-form-item
          label="随访内容"
          required
        >
          <el-input
            v-model="form.followUpContent"
            type="textarea"
            placeholder="请输入随访内容"
            rows="4"
          />
        </el-form-item>
        <el-form-item label="下次随访日期">
          <el-date-picker
            v-model="form.nextFollowUp"
            type="date"
            placeholder="选择下次随访日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
          >确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailVisible"
      title="随访记录详情"
      width="600px"
    >
      <el-descriptions
        v-if="currentFollowUp"
        :column="1"
        border
      >
        <el-descriptions-item label="ID">
          {{ currentFollowUp.id }}
        </el-descriptions-item>
        <el-descriptions-item label="就诊记录ID">
          {{ currentFollowUp.recordId }}
        </el-descriptions-item>
        <el-descriptions-item label="患者">
          {{ patientList.find(p => p.id === currentFollowUp.patientId)?.name || currentFollowUp.patientId }}
        </el-descriptions-item>
        <el-descriptions-item label="医生">
          {{ doctorList.find(d => d.id === currentFollowUp.doctorId)?.name || currentFollowUp.doctorId }}
        </el-descriptions-item>
        <el-descriptions-item label="随访日期">
          {{ currentFollowUp.followUpDate }}
        </el-descriptions-item>
        <el-descriptions-item label="随访类型">
          {{ currentFollowUp.followUpType }}
        </el-descriptions-item>
        <el-descriptions-item label="随访内容">
          {{ currentFollowUp.followUpContent }}
        </el-descriptions-item>
        <el-descriptions-item label="下次随访日期">
          {{ currentFollowUp.nextFollowUp || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          {{ currentFollowUp.status || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ currentFollowUp.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ currentFollowUp.updateTime || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import {
  createFollowUpRecord,
  updateFollowUpRecord,
  getFollowUpRecordById,
  getFollowUpRecordsByRecordId,
  getFollowUpRecordsByPatientId,
  getFollowUpRecordsByDoctorId,
  deleteFollowUpRecord,
  getAllFollowUpRecords,
  getPatientList,
  getDoctorList,
  updateFollowUpStatus
} from '@/api/followUp'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  recordId: '',
  patientId: '',
  doctorId: ''
})

const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const form = reactive({})
const currentFollowUp = ref(null)

// 患者和医生列表
const patientList = ref([])
const doctorList = ref([])

// 获取患者和医生列表
const fetchPatientAndDoctorList = async () => {
  try {
    const [patients, doctors] = await Promise.all([
      getPatientList(),
      getDoctorList()
    ])
    patientList.value = patients || []
    doctorList.value = doctors || []
  } catch (error) {
    console.error('获取患者和医生列表失败:', error)
  }
}

const fetchFollowUpRecords = async () => {
  loading.value = true
  try {
    let res
    if (searchForm.recordId) {
      res = await getFollowUpRecordsByRecordId(searchForm.recordId, {
        currentPage: currentPage.value,
        size: pageSize.value
      })
    } else if (searchForm.patientId) {
      res = await getFollowUpRecordsByPatientId(searchForm.patientId, {
        currentPage: currentPage.value,
        size: pageSize.value
      })
    } else if (searchForm.doctorId) {
      res = await getFollowUpRecordsByDoctorId(searchForm.doctorId, {
        currentPage: currentPage.value,
        size: pageSize.value
      })
    } else {
      // 默认获取所有随访记录
      res = await getAllFollowUpRecords({
        currentPage: currentPage.value,
        size: pageSize.value
      })
    }
    tableData.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchFollowUpRecords()
}

const resetSearch = () => {
  searchForm.recordId = ''
  searchForm.patientId = ''
  searchForm.doctorId = ''
  currentPage.value = 1
  fetchFollowUpRecords()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchFollowUpRecords()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchFollowUpRecords()
}

const handleAddFollowUp = () => {
  dialogTitle.value = '新增随访记录'
  Object.assign(form, {
    recordId: '',
    patientId: '',
    doctorId: '',
    followUpDate: new Date().toISOString().slice(0, 10),
    followUpType: '',
    followUpContent: '',
    nextFollowUp: ''
  })
  dialogVisible.value = true
}

const handleEditFollowUp = async (id) => {
  const followUp = await getFollowUpRecordById(id)
  dialogTitle.value = '编辑随访记录'
  Object.assign(form, followUp)
  dialogVisible.value = true
}

const handleViewFollowUp = async (id) => {
  currentFollowUp.value = await getFollowUpRecordById(id)
  detailVisible.value = true
}

const handleDeleteFollowUp = async (id) => {
  try {
    await deleteFollowUpRecord(id, {
      showDefaultMsg: false,
      onSuccess: () => {
        fetchFollowUpRecords()
      }
    })
  } catch (error) {
    console.error('删除随访记录失败:', error)
  }
}

const handleCompleteFollowUp = async (id) => {
  try {
    // 先获取当前随访记录的状态
    const followUp = await getFollowUpRecordById(id)
    const newStatus = followUp.status === 2 ? 1 : 2
    
    await updateFollowUpStatus(id, newStatus)
    fetchFollowUpRecords()
  } catch (error) {
    console.error('更新随访记录状态失败:', error)
  }
}

const handleSubmit = async () => {
  try {
    if (form.id) {
      // 编辑
      await updateFollowUpRecord(form.id, form, {
        showDefaultMsg: false,
        onSuccess: () => {
          dialogVisible.value = false
          fetchFollowUpRecords()
        }
      })
    } else {
      // 新增
      await createFollowUpRecord(form, {
        showDefaultMsg: false,
        onSuccess: () => {
          dialogVisible.value = false
          fetchFollowUpRecords()
        }
      })
    }
  } catch (error) {
    console.error('提交随访记录失败:', error)
  }
}

onMounted(async () => {
  await fetchPatientAndDoctorList()
  fetchFollowUpRecords()
})
</script>

<style scoped>
.follow-up-management {
  padding: 20px;
  overflow-x: auto;
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
