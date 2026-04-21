import { useQuery } from '@tanstack/react-query'
import { workspaceApi } from '../api/workspaceApi'

export function useWorkspaces() {
  return useQuery({
    queryKey: ['workspaces'],
    queryFn: () => workspaceApi.list(),
    select: (res) => res.data,
  })
}
