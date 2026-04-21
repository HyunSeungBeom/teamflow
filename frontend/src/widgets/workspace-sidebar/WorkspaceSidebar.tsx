import { useNavigate, useLocation } from 'react-router-dom'
import { Badge } from '@/shared/ui'

interface WorkspaceSidebarProps {
  projectName: string
  workspaceNo: number
  projectNo: number
}

const tabs = [
  { id: 'board', label: 'Board', icon: '▦', enabled: true },
  { id: 'docs', label: 'Docs', icon: '📄', enabled: false },
  { id: 'chat', label: 'Chat', icon: '💬', enabled: false },
] as const

export function WorkspaceSidebar({ projectName, workspaceNo, projectNo }: WorkspaceSidebarProps) {
  const navigate = useNavigate()
  const location = useLocation()

  const activeTab = location.pathname.includes('/docs')
    ? 'docs'
    : location.pathname.includes('/chat')
      ? 'chat'
      : 'board'

  return (
    <aside className="w-60 border-r border-gray-200 bg-white flex flex-col h-full">
      {/* Header */}
      <div className="p-4 border-b border-gray-100">
        <button
          className="flex items-center gap-2 text-sm text-gray-500 hover:text-gray-700 transition-colors mb-3"
          onClick={() => navigate('/projects')}
        >
          <span>←</span>
          <span>프로젝트 허브</span>
        </button>
        <h2 className="text-base font-bold text-gray-900 truncate">{projectName}</h2>
      </div>

      {/* Tabs */}
      <nav className="flex-1 p-3">
        <ul className="space-y-1">
          {tabs.map((tab) => (
            <li key={tab.id}>
              <button
                className={`w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition-colors ${
                  activeTab === tab.id
                    ? 'bg-primary-50 text-primary-700 font-semibold'
                    : tab.enabled
                      ? 'text-gray-600 hover:bg-gray-50'
                      : 'text-gray-400 cursor-not-allowed'
                }`}
                disabled={!tab.enabled}
                onClick={() => {
                  if (tab.enabled) {
                    navigate(`/workspace/${workspaceNo}/project/${projectNo}`)
                  }
                }}
              >
                <span>{tab.icon}</span>
                <span>{tab.label}</span>
                {!tab.enabled && <Badge className="ml-auto text-[10px]">Soon</Badge>}
              </button>
            </li>
          ))}
        </ul>
      </nav>
    </aside>
  )
}
