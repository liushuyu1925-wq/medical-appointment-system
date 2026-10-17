import request from '@/utils/request'

export function createFollowUpRecord(data, config = {}) {
  return request.post('/follow-up', data, config)
}

export function updateFollowUpRecord(id, data, config = {}) {
  return request.put(`/follow-up/${id}`, data, config)
}

export function getFollowUpRecordById(id) {
  return request.get(`/follow-up/${id}`, {}, { showDefaultMsg: false })
}

export function getFollowUpRecordsByRecordId(recordId, params = {}) {
  return request.get(`/follow-up/record/${recordId}`, params, { showDefaultMsg: false })
}

export function getFollowUpRecordsByPatientId(patientId, params = {}) {
  return request.get(`/follow-up/patient/${patientId}`, params, { showDefaultMsg: false })
}

export function getFollowUpRecordsByDoctorId(doctorId, params = {}) {
  return request.get(`/follow-up/doctor/${doctorId}`, params, { showDefaultMsg: false })
}

export function deleteFollowUpRecord(id, config = {}) {
  return request.delete(`/follow-up/${id}`, config)
}

export function getAllFollowUpRecords(params = {}) {
  return request.get('/follow-up/page', params, { showDefaultMsg: false })
}

// 获取患者列表
export function getPatientList() {
  return request.get('/patient/list', {}, { showDefaultMsg: false })
}

// 获取医生列表
export function getDoctorList() {
  return request.get('/doctor', {}, { showDefaultMsg: false })
}

// 更新随访记录状态
export function updateFollowUpStatus(id, status) {
  return request.put(`/follow-up/${id}/status`, { status }, {
    showDefaultMsg: false
  })
}