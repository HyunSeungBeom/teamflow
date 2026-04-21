export type IssueStatus = 'BACKLOG' | 'TODO' | 'IN_PROGRESS' | 'DONE'
export type IssuePriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

export interface Issue {
  no: number
  issueKey: string
  title: string
  description: string | null
  status: IssueStatus
  priority: IssuePriority
  assigneeNo: number | null
  position: number
  dueDate: string | null
  createDate: string
  updateDate: string
}
