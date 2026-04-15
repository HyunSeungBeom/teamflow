import { Link } from 'react-router-dom'
import { useAuthStore, LogoutButton } from '@/features/auth'
import { Avatar, Button } from '@/shared/ui'

/**
 * Organism/Header
 *
 * 인증 상태에 따라 자동 전환:
 *   비로그인: 로고 + 로그인 버튼
 *   로그인: 로고 + Avatar + 로그아웃
 *
 * 배경은 transparent — 부모 페이지의 배경색에 의존
 */
export function Header() {
  const user = useAuthStore((s) => s.user)
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)

  return (
    <header className="flex items-center justify-between px-fluid-page-x py-4 lg:py-6">
      <Link to={isAuthenticated ? '/projects' : '/'} className="text-xl font-bold">
        Team<span className="text-primary-400">Flow</span>
      </Link>

      <div className="flex items-center gap-4">
        {isAuthenticated && user ? (
          <>
            <Avatar src={user.picture} name={user.name} size="sm" />
            <LogoutButton />
          </>
        ) : (
          <Link to="/login">
            <Button variant="ghost" size="sm" className="text-current opacity-80 hover:opacity-100">
              로그인
            </Button>
          </Link>
        )}
      </div>
    </header>
  )
}
