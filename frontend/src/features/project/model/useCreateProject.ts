import { useMutation, useQueryClient } from '@tanstack/react-query'
import { projectApi } from '../api/projectApi'

interface CreateProjectParams {
  workspaceNo: number
  data: { name: string; key?: string; description?: string }
}

export function useCreateProject() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ workspaceNo, data }: CreateProjectParams) =>
      projectApi.create(workspaceNo, data),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['projects', variables.workspaceNo] })
    },
  })
}
