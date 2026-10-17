<template>
  <div class="report-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <h3>报告管理</h3>
          <el-button
            type="primary"
            @click="handleAdd"
          >
            新增报告
          </el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form
        :model="searchForm"
        :inline="true"
        class="search-form"
      >
        <el-form-item label="报告编号">
          <el-input
            v-model="searchForm.reportNo"
            placeholder="请输入报告编号"
            clearable
          />
        </el-form-item>
        <el-form-item label="报告类型">
          <el-select
            v-model="searchForm.reportType"
            placeholder="请选择报告类型"
            clearable
            style="width: 150px"
          >
            <el-option
              label="检验报告"
              value="检验报告"
            />
            <el-option
              label="检查报告"
              value="检查报告"
            />
            <el-option
              label="影像报告"
              value="影像报告"
            />
            <el-option
              label="病理报告"
              value="病理报告"
            />
            <el-option
              label="其他报告"
              value="其他报告"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="患者姓名">
          <el-input
            v-model="searchForm.patientName"
            placeholder="请输入患者姓名"
            clearable
          />
        </el-form-item>
        <el-form-item label="医生姓名">
          <el-input
            v-model="searchForm.doctorName"
            placeholder="请输入医生姓名"
            clearable
          />
        </el-form-item>
        <el-form-item label="报告日期">
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
        <el-form-item>
          <el-button
            type="primary"
            @click="handleSearch"
          >
            搜索
          </el-button>
          <el-button @click="resetSearch">
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 报告列表 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column
          prop="reportNo"
          label="报告编号"
          width="180"
        />
        <el-table-column
          prop="reportName"
          label="报告名称"
          width="150"
        />
        <el-table-column
          prop="reportType"
          label="报告类型"
          width="120"
        >
          <template #default="scope">
            <el-tag
              size="small"
              :type="getReportTypeTagType(scope.row.reportType)"
            >
              {{ scope.row.reportType || '其他' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="患者信息"
          width="150"
        >
          <template #default="scope">
            {{ scope.row.patient?.name || '未知' }}
            <el-tag
              size="small"
              type="info"
            >
              {{ scope.row.patient?.sex || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="医生信息"
          width="150"
        >
          <template #default="scope">
            {{ scope.row.doctor?.name || '未知' }}
            <el-tag size="small">
              {{ scope.row.doctor?.title || '' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="reportDate"
          label="报告日期"
          width="120"
          sortable
        />
        <el-table-column
          prop="status"
          label="状态"
          width="100"
        >
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
              {{ scope.row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
          sortable
        />
        <el-table-column
          label="操作"
          width="200"
          fixed="right"
        >
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              type="warning"
              size="small"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-popconfirm
              title="确定删除此报告吗？"
              @confirm="handleDelete(scope.row.id)"
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

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑报告对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增报告' : dialogType === 'edit' ? '编辑报告' : '查看报告'"
      width="650px"
      @closed="resetForm"
    >
      <el-form
        ref="reportFormRef"
        :model="reportForm"
        :rules="reportFormRules"
        label-width="100px"
        :disabled="dialogType === 'view'"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item
              label="患者"
              prop="patientId"
            >
              <el-select
                v-model="reportForm.patientId"
                placeholder="请选择患者"
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="patient in patientOptions"
                  :key="patient.id"
                  :label="patient.name"
                  :value="patient.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              label="医生"
              prop="doctorId"
            >
              <el-select
                v-model="reportForm.doctorId"
                placeholder="请选择医生"
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="doctor in doctorOptions"
                  :key="doctor.id"
                  :label="`${doctor.name} (${doctor.department?.deptName || ''})`"
                  :value="doctor.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item
              label="就诊记录"
              prop="recordId"
            >
              <el-select
                v-model="reportForm.recordId"
                placeholder="请选择就诊记录"
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="record in recordOptions"
                  :key="record.id"
                  :label="`就诊编号: ${record.recordNo} (${record.consultationDate || '无日期'})`"
                  :value="record.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item
              label="报告日期"
              prop="reportDate"
            >
              <el-date-picker
                v-model="reportForm.reportDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item
              label="报告类型"
              prop="reportType"
            >
              <el-select
                v-model="reportForm.reportType"
                placeholder="请选择报告类型"
                style="width: 100%"
              >
                <el-option
                  label="检验报告"
                  value="检验报告"
                />
                <el-option
                  label="检查报告"
                  value="检查报告"
                />
                <el-option
                  label="影像报告"
                  value="影像报告"
                />
                <el-option
                  label="病理报告"
                  value="病理报告"
                />
                <el-option
                  label="其他报告"
                  value="其他报告"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              label="报告名称"
              prop="reportName"
            >
              <el-input
                v-model="reportForm.reportName"
                placeholder="请输入报告名称"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item
          label="报告内容"
          prop="reportContent"
        >
          <el-input
            v-model="reportForm.reportContent"
            type="textarea"
            :rows="5"
            placeholder="请输入报告内容"
          />
        </el-form-item>

        <el-form-item
          v-if="dialogType !== 'add'"
          label="状态"
        >
          <el-radio-group v-model="reportForm.status">
            <el-radio :label="0">
              草稿
            </el-radio>
            <el-radio :label="1">
              已发布
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            v-if="dialogType !== 'view'"
            type="primary"
            @click="submitForm"
          >确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import request from '@/utils/request'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  reportNo: '',
  reportType: '',
  patientName: '',
  doctorName: ''
})

const dateRange = ref([])

const patientOptions = ref([])
const doctorOptions = ref([])
const recordOptions = ref([])

const dialogVisible = ref(false)
const dialogType = ref('add')
const reportFormRef = ref(null)
const reportForm = reactive({
  id: null,
  patientId: null,
  doctorId: null,
  recordId: null,
  reportType: '',
  reportName: '',
  reportContent: '',
  reportDate: '',
  status: 1
})

const reportFormRules = {
  patientId: [{ required: true, message: '请选择患者', trigger: 'change' }],
  doctorId: [{ required: true, message: '请选择医生', trigger: 'change' }],
  recordId: [{ required: true, message: '请选择就诊记录', trigger: 'change' }],
  reportType: [{ required: true, message: '请选择报告类型', trigger: 'change' }],
  reportName: [{ required: true, message: '请输入报告名称', trigger: 'blur' }],
  reportDate: [{ required: true, message: '请选择报告日期', trigger: 'change' }],
  reportContent: [{ required: true, message: '请输入报告内容', trigger: 'blur' }]
}

onMounted(() => {
  fetchPatients()
  fetchDoctors()
  fetchReports()
})

const fetchPatients = async () => {
  try {
    await request.get('/patient/list', {}, {
      onSuccess: (res) => {
        patientOptions.value = res || []
      }
    })
  } catch (error) {
    console.error('获取患者列表失败:', error)
  }
}

const fetchDoctors = async () => {
  try {
    await request.get('/doctor/list', {}, {
      onSuccess: (res) => {
        doctorOptions.value = res || []
      }
    })
  } catch (error) {
    console.error('获取医生列表失败:', error)
  }
}

const fetchRecords = async () => {
  if (!reportForm.patientId || !reportForm.doctorId) {
    recordOptions.value = []
    return
  }

  try {
    const params = {
      patientId: reportForm.patientId,
      doctorId: reportForm.doctorId,
      currentPage: 1,
      size: 100
    }
    await request.get('/medical-record/page', params, {
      onSuccess: (res) => {
        recordOptions.value = res.records || []
      }
    })
  } catch (error) {
    console.error('获取就诊记录列表失败:', error)
  }
}

watch(() => reportForm.patientId, () => {
  if (dialogType.value !== 'view') {
    reportForm.recordId = null
    fetchRecords()
  }
})

watch(() => reportForm.doctorId, () => {
  if (dialogType.value !== 'view') {
    reportForm.recordId = null
    fetchRecords()
  }
})

const fetchReports = async () => {
  loading.value = true
  try {
    const params = {
      currentPage: currentPage.value,
      size: pageSize.value,
      reportNo: searchForm.reportNo,
      reportType: searchForm.reportType
    }

    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }

    await request.get('/report/page', params, {
      onSuccess: (res) => {
        tableData.value = res.records
        total.value = res.total
      }
    })
  } catch (error) {
    console.error('获取报告列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchReports()
}

const resetSearch = () => {
  searchForm.reportNo = ''
  searchForm.reportType = ''
  searchForm.patientName = ''
  searchForm.doctorName = ''
  dateRange.value = []
  currentPage.value = 1
  fetchReports()
}

const handleAdd = () => {
  dialogType.value = 'add'
  reportForm.reportDate = formatDate(new Date())
  reportForm.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.keys(reportForm).forEach(key => {
    reportForm[key] = row[key]
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  dialogType.value = 'view'
  Object.keys(reportForm).forEach(key => {
    reportForm[key] = row[key]
  })
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await request.delete(`/report/${id}`, {
      successMsg: '删除成功',
      onSuccess: () => {
        fetchReports()
      }
    })
  } catch (error) {
    console.error('删除报告失败:', error)
  }
}

const submitForm = () => {
  reportFormRef.value.validate(async (valid) => {
    if (valid) {
      if (dialogType.value === 'add') {
        await request.post('/report', reportForm, {
          successMsg: '新增报告成功',
          onSuccess: () => {
            dialogVisible.value = false
            fetchReports()
          }
        })
      } else {
        await request.put(`/report/${reportForm.id}`, reportForm, {
          successMsg: '编辑报告成功',
          onSuccess: () => {
            dialogVisible.value = false
            fetchReports()
          }
        })
      }
    }
  })
}

const resetForm = () => {
  if (reportFormRef.value) {
    reportFormRef.value.resetFields()
  }
  Object.keys(reportForm).forEach(key => {
    reportForm[key] = key === 'id' ? null : key === 'status' ? 1 : null
  })
  recordOptions.value = []
}

const formatDate = (date) => {
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchReports()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchReports()
}

const getReportTypeTagType = (type) => {
  const typeMap = {
    '检验报告': 'primary',
    '检查报告': 'success',
    '影像报告': 'warning',
    '病理报告': 'danger',
    '其他报告': 'info'
  }
  return typeMap[type] || 'info'
}
</script>

<style scoped>
.report-management {
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
  text-align: right;
}
</style>
