import { useMutation, useQueryClient } from '@tanstack/react-query'
import { issueApi } from '../api/issueApi'

interface UpdateIssueParams {
  issueNo: number
  projectNo: number
  data: {
    title?: string
    description?: string
    status?: string
    priority?: string
    assigneeNo?: number | null
    dueDate?: string | null
  }
}

export function useUpdateIssue() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ issueNo, data }: UpdateIssueParams) => issueApi.update(issueNo, data),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['issues', variables.projectNo] })
    },
  })
}
