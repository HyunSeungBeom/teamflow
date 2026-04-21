import { apiClient } from '@/shared/api/apiClient'
import type { Project, ProjectSummary } from '@/entities/project'

interface CreateProjectRequest {
  name: string
  key?: string
  description?: string
  visibility?: 'PRIVATE' | 'PUBLIC'
}

export const projectApi = {
  create: (workspaceNo: number, data: CreateProjectRequest) =>
    apiClient.post<Project>(`/api/workspaces/${workspaceNo}/projects`, data),

  list: (workspaceNo: number) =>
    apiClient.get<ProjectSummary[]>(`/api/workspaces/${workspaceNo}/projects`),

  getDetail: (projectNo: number) => apiClient.get<Project>(`/api/projects/${projectNo}`),
}
