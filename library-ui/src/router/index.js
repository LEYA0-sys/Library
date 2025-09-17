import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/admin',
      name: 'AdminLayout',
      component: () => import('../views/admin/Layout.vue'),
      redirect: '/admin/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'AdminDashboard',
          component: () => import('../views/admin/Dashboard.vue')
        },
        {
          path: 'books',
          name: 'AdminBooks',
          component: () => import('../views/admin/Books.vue')
        },
        {
          path: 'book-types',
          name: 'AdminBookTypes',
          component: () => import('../views/admin/BookTypes.vue')
        },
        {
          path: 'users',
          name: 'AdminUsers',
          component: () => import('../views/admin/Users.vue')
        },
        {
          path: 'borrows',
          name: 'AdminBorrows',
          component: () => import('../views/admin/Borrows.vue')
        },
        {
          path: 'credits',
          name: 'AdminCredits',
          component: () => import('../views/admin/Credits.vue')
        },
        {
          path: 'profile',
          name: 'AdminProfile',
          component: () => import('../views/admin/Profile.vue')
        }
      ]
    },
    {
      path: '/user',
      name: 'UserLayout',
      component: () => import('../views/user/Layout.vue'),
      redirect: '/user/home',
      children: [
        {
          path: 'home',
          name: 'UserHome',
          component: () => import('../views/user/Home.vue')
        },
        {
          path: 'books',
          name: 'UserBooks',
          component: () => import('../views/user/Books.vue')
        },
        {
          path: 'my-borrows',
          name: 'UserBorrows',
          component: () => import('../views/user/MyBorrows.vue')
        },
        {
          path: 'profile',
          name: 'UserProfile',
          component: () => import('../views/user/Profile.vue')
        },
        {
          path: 'comments',
          name: 'UserComments',
          component: () => import('../views/user/Comments.vue')
        },
        {
          path: 'credit',
          name: 'UserCredit',
          component: () => import('../views/user/Credit.vue')
        }
      ]
    }
  ]
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.path === '/login' || to.path === '/register') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router 