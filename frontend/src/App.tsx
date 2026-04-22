import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from '@/app/providers/AuthProvider'
import { QueryProvider } from '@/app/providers/QueryProvider'
import { ProtectedRoute } from '@/app/routes/ProtectedRoute'
import { PublicRoute } from '@/app/routes/PublicRoute'
import { MainLayout } from '@/app/layouts/MainLayout'
import { ToastContainer } from '@/shared/ui'
import { LandingPage } from '@/pages/landing/LandingPage'
import { LoginPage } from '@/pages/login/LoginPage'
import { OAuthCallbackPage } from '@/pages/auth/callback/OAuthCallbackPage'
import { ProjectsPage } from '@/pages/projects/ProjectsPage'
import { WorkspaceLayout } from '@/pages/workspace/WorkspaceLayout'
import { BoardPage } from '@/pages/workspace/board/BoardPage'

function App() {
  return (
    <BrowserRouter>
      <QueryProvider>
        <AuthProvider>
          <Routes>
            {/* 헤더/푸터 없는 페이지 */}
            <Route path="/auth/callback" element={<OAuthCallbackPage />} />
            <Route path="/login" element={<LoginPage />} />

            {/* 헤더/푸터 있는 페이지 (MainLayout) */}
            <Route element={<MainLayout />}>
              {/* 미인증 전용 */}
              <Route element={<PublicRoute />}>
                <Route path="/" element={<LandingPage />} />
              </Route>

              {/* 인증 필요 */}
              <Route element={<ProtectedRoute />}>
                <Route path="/projects" element={<ProjectsPage />} />
              </Route>
            </Route>

            {/* 워크스페이스 (별도 레이아웃, 인증 필요) */}
            <Route element={<ProtectedRoute />}>
              <Route path="/workspace/:wsNo/project/:projectNo" element={<WorkspaceLayout />}>
                <Route index element={<BoardPage />} />
              </Route>
            </Route>
          </Routes>
        </AuthProvider>
        <ToastContainer />
      </QueryProvider>
    </BrowserRouter>
  )
}

export default App
