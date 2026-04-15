import { Navigate, Outlet } from 'react-router-dom'
import { useAuthStore } from '@/features/auth'

export function PublicRoute() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)
  const isLoading = useAuthStore((s) => s.isLoading)

  // 로딩 중에도 공개 페이지는 바로 보여준다 (스피너 안 돌림)
  // 인증 완료 후 isAuthenticated가 true가 되면 그때 리다이렉트
  if (!isLoading && isAuthenticated) {
    return <Navigate to="/projects" replace />
  }

  return <Outlet />
}
