import { defineStore } from 'pinia'
import { login, adminLogin, register } from '@/api/user'
import router from '@/router'
import { useBorrowStore } from './borrow'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),
  getters: {
    isLogin: (state) => !!state.token,
    isAdmin: (state) => state.userInfo.role === 'admin',
    userId: (state) => state.userInfo.id
  },
  actions: {
    // 普通用户登录
    async userLogin(userInfo) {
      try {
        const res = await login(userInfo)
        if (res.code === 200) {
          this.token = res.data.token
          this.userInfo = res.data.user
          this.userInfo.role = 'user'
          
          localStorage.setItem('token', res.data.token)
          localStorage.setItem('userInfo', JSON.stringify(res.data.user))
          
          // 初始化借阅状态
          const borrowStore = useBorrowStore()
          borrowStore.initialize(res.data.user.id)
          
          router.push('/user/home')
          return Promise.resolve()
        }
        return Promise.reject(res.message)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 管理员登录
    async adminLogin(userInfo) {
      try {
        const res = await adminLogin(userInfo)
        if (res.code === 200) {
          this.token = res.data.token
          this.userInfo = res.data.user
          this.userInfo.role = 'admin'
          
          localStorage.setItem('token', res.data.token)
          localStorage.setItem('userInfo', JSON.stringify(res.data.user))
          
          router.push('/admin/dashboard')
          return Promise.resolve()
        }
        return Promise.reject(res.message)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 用户注册
    async userRegister(userInfo) {
      try {
        const res = await register(userInfo)
        if (res.code === 200) {
          router.push('/login')
          return Promise.resolve()
        }
        return Promise.reject(res.message)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 退出登录
    logout() {
      this.token = ''
      this.userInfo = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      
      // 清空借阅状态
      const borrowStore = useBorrowStore()
      borrowStore.reset()
      
      router.push('/login')
    }
  }
}) 