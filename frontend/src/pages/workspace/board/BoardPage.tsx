import { useState } from 'react'
import { useParams } from 'react-router-dom'
import { useIssues, CreateIssueModal } from '@/features/issue'
import { Button, Badge, Spinner } from '@/shared/ui'
import type { Issue, IssueStatus } from '@/entities/issue'

const columns: { status: IssueStatus; label: string }[] = [
  { status: 'BACKLOG', label: 'Backlog' },
  { status: 'TODO', label: 'To Do' },
  { status: 'IN_PROGRESS', label: 'In Progress' },
  { status: 'DONE', label: 'Done' },
]

const priorityColors: Record<string, 'danger' | 'warning' | 'primary' | 'success'> = {
  CRITICAL: 'danger',
  HIGH: 'warning',
  MEDIUM: 'primary',
  LOW: 'success',
}

export function BoardPage() {
  const { projectNo } = useParams<{ projectNo: string }>()
  const projNo = Number(projectNo)
  const { data: issues, isLoading } = useIssues(projNo)
  const [showCreateModal, setShowCreateModal] = useState(false)

  const getIssuesByStatus = (status: IssueStatus): Issue[] => {
    if (!issues) return []
    return issues.filter((issue) => issue.status === status).sort((a, b) => a.position - b.position)
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <Spinner size="lg" />
      </div>
    )
  }

  return (
    <div className="h-full flex flex-col">
      {/* Board Header */}
      <div className="flex items-center justify-between px-6 py-4 border-b border-grey-200">
        <h1 className="text-lg font-bold text-grey-900">Board</h1>
        <Button variant="primary" size="sm" onClick={() => setShowCreateModal(true)}>
          + 이슈 만들기
        </Button>
      </div>

      {/* Kanban Columns */}
      <div className="flex-1 flex gap-4 p-6 overflow-x-auto">
        {columns.map((col) => {
          const columnIssues = getIssuesByStatus(col.status)
          return (
            <div key={col.status} className="flex-1 min-w-[280px] flex flex-col">
              {/* Column Header */}
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-2">
                  <h3 className="text-sm font-bold text-grey-700">{col.label}</h3>
                  <span className="text-xs text-grey-500 bg-grey-100 px-1.5 py-0.5 rounded-full">
                    {columnIssues.length}
                  </span>
                </div>
              </div>

              {/* Column Body */}
              <div className="flex-1 bg-grey-50 rounded-lg p-2 space-y-2">
                {columnIssues.length === 0 ? (
                  <p className="text-center text-sm text-grey-400 py-8">이슈 없음</p>
                ) : (
                  columnIssues.map((issue) => <IssueCard key={issue.no} issue={issue} />)
                )}
              </div>
            </div>
          )
        })}
      </div>

      {/* Create Issue Modal */}
      {projNo && (
        <CreateIssueModal
          isOpen={showCreateModal}
          onClose={() => setShowCreateModal(false)}
          projectNo={projNo}
        />
      )}
    </div>
  )
}

function IssueCard({ issue }: { issue: Issue }) {
  return (
    <div className="bg-white rounded-lg border border-grey-200 p-3 hover:shadow-sm transition-shadow cursor-pointer">
      <div className="flex items-start justify-between mb-2">
        <span className="text-xs text-grey-500 font-medium">{issue.issueKey}</span>
        <Badge variant={priorityColors[issue.priority] ?? 'default'} size="xs">
          {issue.priority}
        </Badge>
      </div>
      <p className="text-sm font-medium text-grey-900 mb-2 line-clamp-2">{issue.title}</p>
      <div className="flex items-center justify-between text-xs text-grey-500">
        {issue.dueDate && <span>{issue.dueDate}</span>}
      </div>
    </div>
  )
}
