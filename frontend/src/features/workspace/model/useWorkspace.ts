import { useQuery } from '@tanstack/react-query'
import { workspaceApi } from '../api/workspaceApi'

export function useWorkspace(workspaceNo: number | undefined) {
  return useQuery({
    queryKey: ['workspace', workspaceNo],
    queryFn: () => workspaceApi.getDetail(workspaceNo!),
    enabled: !!workspaceNo,
    select: (res) => res.data,
  })
}
