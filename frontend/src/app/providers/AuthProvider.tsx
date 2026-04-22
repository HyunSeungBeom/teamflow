import { useEffect, useRef, type ReactNode } from 'react'
import { useAuthStore, authApi } from '@/features/auth'

/**
 * AuthProvider
 *
 * 앱 시작 시 refresh token(httpOnly cookie)으로 인증 상태 복원.
 * refresh 응답에 accessToken + user가 포함되어 새로고침 후에도 완전한 인증 복원 가능.
 *
 * refreshingRef: Strict Mode에서 useEffect 이중 호출 시 refresh token이
 * 두 번 소비되는 것을 방지. RTR(Refresh Token Rotation) 환경에서 동일 토큰
 * 재사용 시 백엔드가 모든 세션을 무효화하므로 반드시 단일 호출을 보장해야 한다.
 */
export function AuthProvider({ children }: { children: ReactNode }) {
  const setAuth = useAuthStore((s) => s.setAuth)
  const clearAuth = useAuthStore((s) => s.clearAuth)
  const refreshingRef = useRef(false)

  useEffect(() => {
    if (useAuthStore.getState().isAuthenticated || refreshingRef.current) {
      return
    }

    refreshingRef.current = true

    const restoreAuth = async () => {
      try {
        const { data } = await authApi.refresh()
        setAuth(data.accessToken, data.user)
      } catch (error) {
        console.error('Auth restore failed:', error)
        clearAuth()
      }
    }

    restoreAuth()
  }, [setAuth, clearAuth])

  return <>{children}</>
}
