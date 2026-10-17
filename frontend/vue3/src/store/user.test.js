/**
 * @vitest-environment jsdom
 */
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useUserStore } from './user'

vi.mock('@/utils/request', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn()
  }
}))

describe('user store', () => {
  beforeEach(() => {
    localStorage.clear()
    setActivePinia(createPinia())
  })

  it('persists login state when user info is set', () => {
    const store = useUserStore()

    store.setUserInfo({
      id: 1,
      username: 'admin',
      roleCode: 'ADMIN',
      token: 'token-value'
    })

    expect(store.isLoggedIn).toBe(true)
    expect(store.isAdmin).toBe(true)
    expect(localStorage.getItem('token')).toBe('token-value')
    expect(JSON.parse(localStorage.getItem('userInfo')).username).toBe('admin')
  })

  it('clears local and in-memory state on logout', async () => {
    const store = useUserStore()
    store.setUserInfo({
      id: 2,
      username: 'patient',
      roleCode: 'PATIENT',
      token: 'patient-token'
    })

    await store.logout()

    expect(store.isLoggedIn).toBe(false)
    expect(store.userInfo).toBeNull()
    expect(localStorage.getItem('token')).toBeNull()
  })

  it('detects patient and medical staff roles', () => {
    const store = useUserStore()

    store.setUserInfo({ roleCode: 'PATIENT', token: 'patient-token' })
    expect(store.isPatient).toBe(true)
    expect(store.isUser).toBe(true)

    store.setUserInfo({ roleCode: 'DOCTOR', token: 'doctor-token' })
    expect(store.isDoctor).toBe(true)
    expect(store.isMedicalStaff).toBe(true)
  })
})
