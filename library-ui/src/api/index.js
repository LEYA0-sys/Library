import request from '@/utils/request'

// 获取系统统计数据
export function getSystemStats() {
  return request({
    url: '/stats',
    method: 'get'
  })
} 