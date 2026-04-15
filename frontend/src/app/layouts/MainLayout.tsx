import { Outlet } from 'react-router-dom'
import { Header, Footer } from '@/shared/ui'

/**
 * MainLayout
 *
 * 헤더 + 콘텐츠(Outlet) + 푸터를 포함하는 공통 레이아웃.
 * Login, OAuth Callback 등 헤더/푸터가 필요 없는 페이지는 이 Layout 밖에 배치한다.
 */
export function MainLayout() {
  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <main className="flex-1">
        <Outlet />
      </main>
      <Footer />
    </div>
  )
}
