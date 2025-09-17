import request from '@/utils/request'

// 管理员登录
export function adminLogin(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

// 用户登录
export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/register',
    method: 'post',
    data
  })
}

// 获取用户列表
export function getUserList(keyword) {
  return request({
    url: '/user/list',
    method: 'get',
    params: { keyword }
  })
}

// 获取用户详情
export function getUserDetail(id) {
  return request({
    url: `/user/detail/${id}`,
    method: 'get'
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/user/create',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/user/delete/${id}`,
    method: 'post'
  })
}

// 获取用户信息
export function getUserInfo(id) {
  return request({
    url: `/user/detail/${id}`,
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  })
}

// 修改密码
export function changePassword(userId, oldPassword, newPassword) {
  return request({
    url: '/user/change-password',
    method: 'post',
    data: {
      userId,
      oldPassword,
      newPassword
    }
  })
} 