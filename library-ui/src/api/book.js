import request from '@/utils/request'

// 获取图书列表
export function getBookList(keyword) {
  return request({
    url: '/book/list',
    method: 'get',
    params: { keyword }
  })
}

// 获取图书详情
export function getBookDetail(id) {
  return request({
    url: `/book/detail/${id}`,
    method: 'get'
  })
}

// 创建图书
export function createBook(data) {
  return request({
    url: '/book/create',
    method: 'post',
    data
  })
}

// 更新图书
export function updateBook(data) {
  return request({
    url: '/book/update',
    method: 'post',
    data
  })
}

// 删除图书
export function deleteBook(id) {
  return request({
    url: `/book/delete/${id}`,
    method: 'post'
  })
}

// 根据类型获取图书列表
export function getBooksByType(typeId) {
  return request({
    url: `/book/type/${typeId}`,
    method: 'get'
  })
} 