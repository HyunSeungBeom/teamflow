import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from '@/app/providers/AuthProvider'
import { ProtectedRoute } from '@/app/routes/ProtectedRoute'
import { PublicRoute } from '@/app/routes/PublicRoute'
import { LandingPage } from '@/pages/landing/LandingPage'
import { LoginPage } from '@/pages/login/LoginPage'
import { OAuthCallbackPage } from '@/pages/auth/callback/OAuthCallbackPage'
import { ProjectsPage } from '@/pages/projects/ProjectsPage'

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* 공개 */}
          <Route path="/auth/callback" element={<OAuthCallbackPage />} />

          {/* 미인증 전용 */}
          <Route element={<PublicRoute />}>
            <Route path="/" element={<LandingPage />} />
            <Route path="/login" element={<LoginPage />} />
          </Route>

          {/* 인증 필요 */}
          <Route element={<ProtectedRoute />}>
            <Route path="/projects" element={<ProjectsPage />} />
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App
