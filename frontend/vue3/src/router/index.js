import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import BackendLayout from '@/layouts/BackendLayout.vue'

export const backendRoutes = [
  {
    path: '/back',
    component: BackendLayout,
    redirect: '/back/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/backend/DashboardPage.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'user',
        name: 'UserManagement',
        component: () => import('@/views/backend/user/UserManagementPage.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'department',
        name: 'DepartmentManagement',
        component: () => import('@/views/backend/department/DepartmentManagementPage.vue'),
        meta: { title: '科室管理', icon: 'Office' }
      },
      {
        path: 'doctor',
        name: 'DoctorManagement',
        component: () => import('@/views/backend/doctor/DoctorManagementPage.vue'),
        meta: { title: '医生管理', icon: 'User' }
      },
      {
        path: 'patient',
        name: 'PatientManagement',
        component: () => import('@/views/backend/patient/PatientManagementPage.vue'),
        meta: { title: '患者管理', icon: 'User' }
      },
      {
        path: 'medicine',
        name: 'MedicineManagement',
        component: () => import('@/views/backend/medicine/MedicineManagementPage.vue'),
        meta: { title: '药品管理', icon: 'FirstAidKit' }
      },
      {
        path: 'medicine-category',
        name: 'MedicineCategoryManagement',
        component: () => import('@/views/backend/medicine-category/MedicineCategoryManagementPage.vue'),
        meta: { title: '药品分类管理', icon: 'Menu' }
      },
      {
        path: 'schedule',
        name: 'ScheduleManagement',
        component: () => import('@/views/backend/schedule/ScheduleManagementPage.vue'),
        meta: { title: '排班管理', icon: 'Calendar' }
      },
      {
        path: 'appointment',
        name: 'AppointmentManagement',
        component: () => import('@/views/backend/appointment/AppointmentManagementPage.vue'),
        meta: { title: '预约管理', icon: 'Calendar' }
      },
      {
        path: 'medical-record',
        name: 'MedicalRecordManagement',
        component: () => import('@/views/backend/medical-record/MedicalRecordManagementPage.vue'),
        meta: { title: '就诊记录管理', icon: 'Document' }
      },
      {
        path: 'report',
        name: 'ReportManagement',
        component: () => import('@/views/backend/report/ReportManagementPage.vue'),
        meta: { title: '报告管理', icon: 'DocumentCopy' }
      },
      {
        path: 'prescription',
        name: 'PrescriptionManagement',
        component: () => import('@/views/backend/prescription/PrescriptionManagementPage.vue'),
        meta: { title: '处方管理', icon: 'List' }
      },
      {
        path: 'statistics',
        name: 'StatisticsManagement',
        component: () => import('@/views/backend/statistics/StatisticsPage.vue'),
        meta: { title: '统计分析', icon: 'DataAnalysis' }
      },
      {
        path: 'notice',
        name: 'NoticeManagement',
        component: () => import('@/views/backend/notice/NoticeManagementPage.vue'),
        meta: { title: '通知管理', icon: 'Bell', requiresAdmin: true }
      },
      {
        path: 'follow-up',
        name: 'FollowUpManagement',
        component: () => import('@/views/backend/follow-up/FollowUpManagementPage.vue'),
        meta: { title: '随访记录管理', icon: 'Message' }
      },
      {
        path: 'profile',
        name: 'BackendProfile',
        component: () => import('@/views/backend/user/UserProfilePage.vue'),
        meta: { title: '个人信息', icon: 'UserFilled' }
      }
    ]
  }
]

const frontendRoutes = [
  {
    path: '/',
    component: () => import('@/layouts/FrontendLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/frontend/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'department',
        name: 'Department',
        component: () => import('@/views/frontend/department/DepartmentPage.vue'),
        meta: { title: '科室介绍' }
      },
      {
        path: 'doctor',
        name: 'Doctor',
        component: () => import('@/views/frontend/doctor/DoctorPage.vue'),
        meta: { title: '医生介绍' }
      },
      {
        path: 'appointment',
        name: 'Appointment',
        component: () => import('@/views/frontend/appointment/AppointmentPage.vue'),
        meta: { title: '预约挂号', requiresAuth: true }
      },
      {
        path: 'my-appointments',
        name: 'MyAppointments',
        component: () => import('@/views/frontend/appointment/MyAppointmentsPage.vue'),
        meta: { title: '我的预约', requiresAuth: true }
      },
      {
        path: 'medical-record',
        name: 'MedicalRecord',
        component: () => import('@/views/frontend/medical-record/MedicalRecordPage.vue'),
        meta: { title: '我的就诊记录', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/PatientProfilePage.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'profile/notice',
        name: 'ProfileNotice',
        component: () => import('@/views/profile/PatientNoticePage.vue'),
        meta: { title: '消息通知', requiresAuth: true }
      },
      {
        path: 'payment',
        name: 'Payment',
        component: () => import('@/views/PaymentPage.vue'),
        meta: { title: '支付中心', requiresAuth: true }
      },
      {
        path: 'patient/reviews',
        name: 'ReviewList',
        component: () => import('@/views/patient/ReviewList.vue'),
        meta: { title: '我的评价', requiresAuth: true }
      },
      {
        path: 'patient/review',
        name: 'ReviewForm',
        component: () => import('@/views/patient/ReviewForm.vue'),
        meta: { title: '评价医生', requiresAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/forget',
    name: 'ForgetPassword',
    component: () => import('@/views/auth/ForgetPassword.vue'),
    meta: { title: '忘记密码' }
  }
]

const errorRoutes = [
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/NotFoundPage.vue'),
    meta: { title: '404' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...frontendRoutes, ...backendRoutes, ...errorRoutes]
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    const appName = to.path.startsWith('/back') ? '瑾瑜慧诊系统' : '瑾瑜慧诊'
    document.title = `${to.meta.title} - ${appName}`
  }

  const userStore = useUserStore()

  if (to.matched.some(record => record.meta.requiresAuth) && !userStore.isLoggedIn) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
    return
  }

  if (to.matched.some(record => record.meta.requiresAdmin) && !userStore.isAdmin) {
    next(userStore.isLoggedIn ? '/back/dashboard' : '/login')
    return
  }

  if (userStore.isLoggedIn) {
    if (to.path === '/login') {
      next(userStore.isUser ? '/' : '/back/dashboard')
      return
    }

    if (!userStore.isUser) {
      if (to.path.startsWith('/back')) {
        next()
      } else {
        next('/back/dashboard')
      }
      return
    }

    if (to.path.startsWith('/back')) {
      next('/')
      return
    }
    next()
    return
  }

  if (to.path.startsWith('/back')) {
    next('/login')
    return
  }

  next()
})

export default router
