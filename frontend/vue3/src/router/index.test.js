/**
 * @vitest-environment jsdom
 */
import { describe, expect, it } from 'vitest'
import router, { backendRoutes } from './index'

describe('router', () => {
  it('registers the main public, patient, and backend routes', () => {
    const routeNames = router.getRoutes().map(route => route.name)

    expect(routeNames).toContain('Home')
    expect(routeNames).toContain('Login')
    expect(routeNames).toContain('Appointment')
    expect(routeNames).toContain('MyAppointments')
    expect(routeNames).toContain('Dashboard')
    expect(routeNames).toContain('UserManagement')
  })

  it('protects patient-only workflows with authentication metadata', () => {
    const appointmentRoute = router.getRoutes().find(route => route.name === 'Appointment')
    const profileRoute = router.getRoutes().find(route => route.name === 'Profile')

    expect(appointmentRoute.meta.requiresAuth).toBe(true)
    expect(profileRoute.meta.requiresAuth).toBe(true)
  })

  it('keeps admin-only backend routes explicit', () => {
    const noticeRoute = backendRoutes[0].children.find(route => route.name === 'NoticeManagement')

    expect(noticeRoute.meta.requiresAdmin).toBe(true)
  })
})
