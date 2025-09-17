import request from '@/utils/request'

// 获取类型列表
export function getTypeList() {
  return request({
    url: '/bookType/list',
    method: 'get'
  })
}

// 获取类型详情
export function getTypeDetail(id) {
  return request({
    url: `/bookType/detail/${id}`,
    method: 'get'
  })
}

// 创建类型
export function createType(data) {
  return request({
    url: '/bookType/create',
    method: 'post',
    data
  })
}

// 更新类型
export function updateType(data) {
  return request({
    url: '/bookType/update',
    method: 'post',
    data
  })
}

// 删除类型
export function deleteType(id) {
  return request({
    url: `/bookType/delete/${id}`,
    method: 'post'
  })
} 