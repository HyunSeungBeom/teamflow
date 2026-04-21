import { apiClient } from '@/shared/api/apiClient'
import type { Issue, IssueStatus } from '@/entities/issue'

interface CreateIssueRequest {
  title: string
  description?: string
  status?: IssueStatus
  priority?: string
  assigneeNo?: number
  dueDate?: string
}

interface UpdateIssueRequest {
  title?: string
  description?: string
  status?: string
  priority?: string
  assigneeNo?: number | null
  dueDate?: string | null
}

export const issueApi = {
  create: (projectNo: number, data: CreateIssueRequest) =>
    apiClient.post<Issue>(`/api/projects/${projectNo}/issues`, data),

  list: (projectNo: number) => apiClient.get<Issue[]>(`/api/projects/${projectNo}/issues`),

  getDetail: (issueNo: number) => apiClient.get<Issue>(`/api/issues/${issueNo}`),

  update: (issueNo: number, data: UpdateIssueRequest) =>
    apiClient.patch<Issue>(`/api/issues/${issueNo}`, data),

  delete: (issueNo: number) => apiClient.delete(`/api/issues/${issueNo}`),

  updateStatus: (issueNo: number, status: IssueStatus) =>
    apiClient.patch<Issue>(`/api/issues/${issueNo}/status`, { status }),

  updatePosition: (issueNo: number, position: number) =>
    apiClient.patch<Issue>(`/api/issues/${issueNo}/position`, { position }),
}
