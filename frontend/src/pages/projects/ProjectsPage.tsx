import { useState } from 'react'
import { useLocation } from 'react-router-dom'
import { WorkspaceModal } from '@/features/workspace'
import { Button } from '@/shared/ui'

export function ProjectsPage() {
  const location = useLocation()
  const isNewUser = (location.state as { isNewUser?: boolean })?.isNewUser ?? false
  const [showWorkspaceModal, setShowWorkspaceModal] = useState(isNewUser)

  if (isNewUser) {
    window.history.replaceState({}, '')
  }

  return (
    <div className="bg-gray-50 min-h-full">
      {/* Content */}
      <div className="max-w-5xl mx-auto px-fluid-page-x py-10">
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-fluid-h1 font-bold text-gray-900">내 프로젝트</h1>
          <Button variant="primary" size="md">
            + 새 프로젝트
          </Button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {/* 새 프로젝트 만들기 카드 */}
          <button className="border-2 border-dashed border-gray-300 rounded-card p-8 flex flex-col items-center justify-center gap-3 text-gray-400 hover:border-primary-400 hover:text-primary-500 transition-colors">
            <span className="text-4xl">+</span>
            <span className="font-semibold">새 프로젝트 만들기</span>
          </button>
        </div>
      </div>

      {/* Workspace Modal */}
      <WorkspaceModal isOpen={showWorkspaceModal} onClose={() => setShowWorkspaceModal(false)} />
    </div>
  )
}
