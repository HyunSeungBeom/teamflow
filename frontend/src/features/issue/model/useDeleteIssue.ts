import { useMutation, useQueryClient } from '@tanstack/react-query'
import { issueApi } from '../api/issueApi'

interface DeleteIssueParams {
  issueNo: number
  projectNo: number
}

export function useDeleteIssue() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ issueNo }: DeleteIssueParams) => issueApi.delete(issueNo),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['issues', variables.projectNo] })
    },
  })
}
