import { useQuery } from '@tanstack/react-query'
import { issueApi } from '../api/issueApi'

export function useIssues(projectNo: number | undefined) {
  return useQuery({
    queryKey: ['issues', projectNo],
    queryFn: () => issueApi.list(projectNo!),
    enabled: !!projectNo,
    select: (res) => res.data,
  })
}
