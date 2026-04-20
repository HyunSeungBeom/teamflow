import { useEffect, useRef, useState } from 'react'
import { useNavigate, useSearchParams, Link } from 'react-router-dom'
import { useAuthStore, authApi } from '@/features/auth'
import { env } from '@/shared/config/env'
import { Spinner, Button } from '@/shared/ui'

export function OAuthCallbackPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const setAuth = useAuthStore((s) => s.setAuth)
  const called = useRef(false)
  const [error, setError] = useState<string | null>(null)

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
        navigate('/projects', { replace: true, state: { isNewUser: data.isNewUser } })
      })
      .catch(() => {
        setError('로그인에 실패했습니다. 다시 시도해주세요.')
      })
  }, [searchParams, navigate, setAuth])

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-white">
        <div className="text-center">
          <p className="text-lg font-semibold text-gray-900 mb-2">로그인 실패</p>
          <p className="text-sm text-gray-500 mb-6">{error}</p>
          <Link to="/login" replace>
            <Button variant="primary" size="md">
              로그인 페이지로 돌아가기
            </Button>
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-white">
      <div className="text-center">
        <Spinner size="lg" className="mx-auto mb-4" />
        <p className="text-lg font-semibold text-gray-500">로그인 처리 중...</p>
        <p className="text-sm text-gray-400 mt-2">
          잠시만 기다려주세요. Google 계정을 확인하고 있습니다.
        </p>
      </div>
    </div>
  )
}
