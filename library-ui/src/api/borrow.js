import request from '@/utils/request'

// 获取借阅列表
export function getBorrowList() {
  return request({
    url: '/borrow/list',
    method: 'get'
  })
}

// 获取借阅详情
export function getBorrowDetail(id) {
  return request({
    url: `/borrow/detail/${id}`,
    method: 'get'
  })
}

// 借书
export function borrowBook(userId, bookId, startDate, borrowDays) {
  return request({
    url: '/borrow/borrow',
    method: 'post',
    params: { userId, bookId, startDate, borrowDays }
  })
}

// 还书
export function returnBook(id) {
  return request({
    url: `/borrow/return/${id}`,
    method: 'post'
  })
}

// 延长借阅时间
export function extendBorrowTime(id, days) {
  return request({
    url: '/borrow/extend',
    method: 'post',
    params: { id, days }
  })
}

// 获取用户借阅记录
export function getUserBorrows(userId, status) {
  return request({
    url: `/borrow/user/${userId}`,
    method: 'get',
    params: { status }
  })
} 