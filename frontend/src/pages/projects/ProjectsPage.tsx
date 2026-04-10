import { useAuthStore, LogoutButton } from '@/features/auth'

export function ProjectsPage() {
  const user = useAuthStore((s) => s.user)

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 px-8 py-4 flex items-center justify-between">
        <span className="text-lg font-bold text-gray-900">
          Team<span className="text-indigo-500">Flow</span>
        </span>
        <div className="flex items-center gap-4">
          {user && (
            <span className="text-sm text-gray-600">
              {user.name}
            </span>
          )}
          <LogoutButton />
        </div>
      </header>

      {/* Content */}
      <main className="max-w-5xl mx-auto px-8 py-10">
        <h1 className="text-2xl font-bold text-gray-900 mb-8">내 프로젝트</h1>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {/* 새 프로젝트 만들기 카드 */}
          <button className="border-2 border-dashed border-gray-300 rounded-2xl p-8 flex flex-col items-center justify-center gap-3 text-gray-400 hover:border-indigo-400 hover:text-indigo-500 transition-colors">
            <span className="text-4xl">+</span>
            <span className="font-semibold">새 프로젝트 만들기</span>
          </button>
        </div>
      </main>
    </div>
  )
}
