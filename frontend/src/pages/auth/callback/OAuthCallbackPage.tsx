import { useEffect, useRef } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useAuthStore, authApi } from '@/features/auth'
import { env } from '@/shared/config/env'

export function OAuthCallbackPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const setAuth = useAuthStore((s) => s.setAuth)
  const called = useRef(false)

  useEffect(() => {
    if (called.current) return
    called.current = true

    const code = searchParams.get('code')
    const state = searchParams.get('state')
    const savedState = sessionStorage.getItem('oauth_state')

    if (!code || !state || state !== savedState) {
      navigate('/login', { replace: true })
      return
    }

    sessionStorage.removeItem('oauth_state')

    authApi
      .loginWithGoogle(code, env.googleRedirectUri)
      .then(({ data }) => {
        setAuth(data.accessToken, data.user)
        navigate('/projects', { replace: true })
      })
      .catch(() => {
        navigate('/login', { replace: true })
      })
  }, [searchParams, navigate, setAuth])

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-center">
        <div className="w-8 h-8 border-4 border-indigo-500 border-t-transparent rounded-full animate-spin mx-auto mb-4" />
        <p className="text-gray-500">로그인 처리 중...</p>
      </div>
    </div>
  )
}
