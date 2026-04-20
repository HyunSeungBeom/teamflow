import { describe, it, expect, beforeEach } from 'vitest'
import { useAuthStore } from './authStore'

describe('authStore', () => {
  beforeEach(() => {
    useAuthStore.getState().clearAuth()
  })

  it('초기 상태는 비인증이다', () => {
    const state = useAuthStore.getState()
    expect(state.isAuthenticated).toBe(false)
    expect(state.user).toBeNull()
    expect(state.accessToken).toBeNull()
  })

  it('setAuth로 인증 상태를 설정한다', () => {
    const user = {
      no: 1,
      email: 'test@test.com',
      name: 'Test',
      picture: null,
      provider: 'GOOGLE',
    }
    useAuthStore.getState().setAuth('test-token', user)

    const state = useAuthStore.getState()
    expect(state.isAuthenticated).toBe(true)
    expect(state.accessToken).toBe('test-token')
    expect(state.user?.email).toBe('test@test.com')
  })

  it('setAccessToken으로 토큰만 갱신한다', () => {
    const user = {
      no: 1,
      email: 'test@test.com',
      name: 'Test',
      picture: null,
      provider: 'GOOGLE',
    }
    useAuthStore.getState().setAuth('old', user)
    useAuthStore.getState().setAccessToken('new')

    expect(useAuthStore.getState().accessToken).toBe('new')
    expect(useAuthStore.getState().user?.email).toBe('test@test.com')
  })

  it('clearAuth로 초기화한다', () => {
    const user = {
      no: 1,
      email: 'test@test.com',
      name: 'Test',
      picture: null,
      provider: 'GOOGLE',
    }
    useAuthStore.getState().setAuth('token', user)
    useAuthStore.getState().clearAuth()

    const state = useAuthStore.getState()
    expect(state.isAuthenticated).toBe(false)
    expect(state.accessToken).toBeNull()
    expect(state.user).toBeNull()
  })
})
