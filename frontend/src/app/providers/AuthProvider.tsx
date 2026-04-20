import { useEffect, type ReactNode } from 'react'
import { useAuthStore, authApi } from '@/features/auth'

/**
 * AuthProvider
 *
 * 앱 시작 시 refresh token(httpOnly cookie)으로 인증 상태 복원.
 * refresh 응답에 accessToken + user가 포함되어 새로고침 후에도 완전한 인증 복원 가능.
 */
export function AuthProvider({ children }: { children: ReactNode }) {
  const setAuth = useAuthStore((s) => s.setAuth)
  const clearAuth = useAuthStore((s) => s.clearAuth)

  useEffect(() => {
    if (useAuthStore.getState().isAuthenticated) {
      return
    }

    const restoreAuth = async () => {
      try {
        const { data } = await authApi.refresh()
        setAuth(data.accessToken, data.user)
      } catch {
        clearAuth()
      }
    }

    restoreAuth()
  }, [setAuth, clearAuth])

  return <>{children}</>
}
