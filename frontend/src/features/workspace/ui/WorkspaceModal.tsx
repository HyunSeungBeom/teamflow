import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Modal, Button, Input, Spinner } from '@/shared/ui'
import { createWorkspaceSchema, type CreateWorkspaceFormData } from '../model/workspaceSchema'
import { useCreateWorkspace } from '../model/useCreateWorkspace'
import { toast } from '@/shared/model/useToastStore'

interface WorkspaceModalProps {
  isOpen: boolean
  onClose: () => void
  onSuccess?: (workspaceNo: number) => void
}

export function WorkspaceModal({ isOpen, onClose, onSuccess }: WorkspaceModalProps) {
  const { createMutation } = useCreateWorkspace()

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<CreateWorkspaceFormData>({
    resolver: zodResolver(createWorkspaceSchema),
    defaultValues: { name: '' },
  })

  const isLoading = createMutation.isPending

  const onSubmit = async (data: CreateWorkspaceFormData) => {
    try {
      const res = await createMutation.mutateAsync({ name: data.name })
      const workspaceNo = res.data.no

      reset()
      onSuccess?.(workspaceNo)
      onClose()
      toast.success('워크스페이스가 생성되었습니다.')
    } catch (error) {
      console.error('워크스페이스 생성 실패:', error)
      toast.error('워크스페이스 생성에 실패했습니다.')
    }
  }

  const handleClose = () => {
    reset()
    onClose()
  }

  return (
    <Modal isOpen={isOpen} onClose={handleClose} className="max-w-[520px] p-9">
      <h2 className="text-xl font-bold text-gray-900 mb-2">워크스페이스 만들기</h2>
      <p className="text-sm text-gray-500 mb-6">팀을 위한 워크스페이스를 생성하세요.</p>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <Input
          label="워크스페이스 이름"
          placeholder="예: 우리 팀"
          error={errors.name?.message}
          {...register('name')}
        />

        <Button
          type="submit"
          variant="primary"
          size="lg"
          className="w-full mt-2"
          disabled={isLoading}
        >
          {isLoading ? (
            <Spinner size="sm" className="border-white border-t-transparent" />
          ) : (
            '워크스페이스 생성'
          )}
        </Button>
      </form>

      <button
        type="button"
        onClick={handleClose}
        className="w-full text-center text-sm text-gray-400 hover:text-gray-500 mt-4 transition-colors"
      >
        나중에 하기
      </button>
    </Modal>
  )
}
