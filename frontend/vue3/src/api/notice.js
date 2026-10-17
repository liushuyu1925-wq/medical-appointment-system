import request from '@/utils/request'

export function getMyNoticePage(params) {
  return request.get('/notice/my/page', params, { showDefaultMsg: false })
}

export function getMyUnreadCount() {
  return request.get('/notice/my/unread-count', {}, { showDefaultMsg: false })
}

export function getMyNoticeDetail(id) {
  return request.get(`/notice/${id}`, {}, { showDefaultMsg: false })
}

export function markNoticeRead(id, config = {}) {
  return request.put(`/notice/read/${id}`, {}, config)
}

export function markAllNoticesRead(config = {}) {
  return request.put('/notice/read-all', {}, config)
}

export function getAdminNoticePage(params) {
  return request.get('/notice/admin/page', params, { showDefaultMsg: false })
}

export function getAdminNoticeDetail(id) {
  return request.get(`/notice/admin/${id}`, {}, { showDefaultMsg: false })
}

export function getNoticeLogPage(params) {
  return request.get('/notice/log/page', params, { showDefaultMsg: false })
}

export function getNoticeLogDetail(id) {
  return request.get(`/notice/log/${id}`, {}, { showDefaultMsg: false })
}

export function deleteNotice(id, config = {}) {
  return request.delete(`/notice/admin/${id}`, config)
}
