import { useEffect, type ReactNode } from 'react'
import { useAuthStore, authApi } from '@/features/auth'

export function AuthProvider({ children }: { children: ReactNode }) {
  const setAuth = useAuthStore((s) => s.setAuth)
  const clearAuth = useAuthStore((s) => s.clearAuth)

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
        clearAuth()
      }
    }

    restoreAuth()
  }, [setAuth, clearAuth])

  return <>{children}</>
}
