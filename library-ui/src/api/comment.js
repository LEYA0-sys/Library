import request from '@/utils/request'

// 获取留言列表
export function getCommentList() {
  return request({
    url: '/comment/list',
    method: 'get'
  })
}

// 创建留言
export function createComment(data) {
  return request({
    url: '/comment/create',
    method: 'post',
    data
  })
}

// 删除留言
export function deleteComment(id) {
  return request({
    url: `/comment/delete/${id}`,
    method: 'post'
  })
} 