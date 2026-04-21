import { useMutation, useQueryClient } from '@tanstack/react-query'
import { issueApi } from '../api/issueApi'

interface CreateIssueParams {
  projectNo: number
  data: {
    title: string
    description?: string
    status?: string
    priority?: string
    assigneeNo?: number
    dueDate?: string
  }
}

export function useCreateIssue() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ projectNo, data }: CreateIssueParams) => issueApi.create(projectNo, data),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['issues', variables.projectNo] })
    },
  })
}
