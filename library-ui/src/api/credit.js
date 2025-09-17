import request from '@/utils/request'

// 获取信用记录列表
export function getCreditRecordList() {
  return request({
    url: '/credit/list',
    method: 'get'
  })
}

// 获取用户信用记录
export function getUserCreditRecords(userId) {
  return request({
    url: `/credit/user/${userId}`,
    method: 'get'
  })
}

// 创建信用记录
export function createCreditRecord(data) {
  return request({
    url: '/credit/create',
    method: 'post',
    data
  })
}

// 删除信用记录
export function deleteCreditRecord(id) {
  return request({
    url: `/credit/delete/${id}`,
    method: 'post'
  })
}

// 更新用户信用积分
export function updateUserCredit(userId, change, reason) {
  return request({
    url: '/credit/update-credit',
    method: 'post',
    params: {
      userId,
      change,
      reason
    }
  })
}

// 手动检查逾期图书并更新信用积分
export function checkOverdueBooks() {
  return request({
    url: '/credit/check-overdue',
    method: 'post'
  })
} 