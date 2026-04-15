import { useEffect, type ReactNode } from 'react'
import { useAuthStore, authApi } from '@/features/auth'

export function AuthProvider({ children }: { children: ReactNode }) {
  const setAuth = useAuthStore((s) => s.setAuth)
  const clearAuth = useAuthStore((s) => s.clearAuth)
  const setLoading = useAuthStore((s) => s.setLoading)

  useEffect(() => {
    const restoreAuth = async () => {
      try {
        const { data } = await authApi.refresh()
        const currentUser = useAuthStore.getState().user
        if (currentUser) {
          setAuth(data.accessToken, currentUser)
        } else {
          clearAuth()
        }
      } catch {
        // refresh 실패 = 로그인 안 된 상태 → 바로 로딩 해제
        clearAuth()
      }
    }

    restoreAuth()
  }, [setAuth, setLoading, clearAuth])

  return <>{children}</>
}
