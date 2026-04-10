import { useNavigate } from 'react-router-dom'
import { authApi } from '../api/authApi'
import { useAuthStore } from '../model/authStore'

export function LogoutButton() {
  const navigate = useNavigate()
  const clearAuth = useAuthStore((s) => s.clearAuth)

  const handleLogout = async () => {
    try {
      await authApi.logout()
    } finally {
      clearAuth()
      navigate('/login')
    }
  }

  return (
    <button
      onClick={handleLogout}
      className="px-4 py-2 text-sm text-gray-500 hover:text-gray-700 transition-colors"
    >
      로그아웃
    </button>
  )
}
