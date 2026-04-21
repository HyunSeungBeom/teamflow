import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Modal, Button, Input, Spinner } from '@/shared/ui'
import { createProjectSchema, type CreateProjectFormData } from '../model/projectSchema'
import { useCreateProject } from '../model/useCreateProject'

interface CreateProjectModalProps {
  isOpen: boolean
  onClose: () => void
  workspaceNo: number
}

export function CreateProjectModal({ isOpen, onClose, workspaceNo }: CreateProjectModalProps) {
  const createProject = useCreateProject()

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    watch,
    setValue,
  } = useForm<CreateProjectFormData>({
    resolver: zodResolver(createProjectSchema),
    defaultValues: { name: '', key: '' },
  })

  const isLoading = createProject.isPending
  const nameValue = watch('name')

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const name = e.target.value
    const currentKey = watch('key')
    const autoKey = generateKey(nameValue)
    if (currentKey === '' || currentKey === autoKey) {
      setValue('key', generateKey(name), { shouldValidate: false })
    }
  }

  const onSubmit = async (data: CreateProjectFormData) => {
    try {
      await createProject.mutateAsync({ workspaceNo, data: { name: data.name, key: data.key } })
      reset()
      onClose()
    } catch {
      // error는 mutation state로 처리
    }
  }

  const handleClose = () => {
    reset()
    createProject.reset()
    onClose()
  }

  return (
    <Modal isOpen={isOpen} onClose={handleClose} className="max-w-[520px] p-9">
      <h2 className="text-xl font-bold text-gray-900 mb-2">새 프로젝트 만들기</h2>
      <p className="text-sm text-gray-500 mb-6">프로젝트 이름과 키를 입력하세요.</p>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <Input
          label="프로젝트 이름"
          placeholder="예: 팀플로우 개발"
          error={errors.name?.message}
          {...register('name', { onChange: handleNameChange })}
        />

        <Input
          label="프로젝트 키"
          placeholder="예: TF"
          error={errors.key?.message}
          {...register('key')}
        />
        <p className="text-xs text-gray-400 -mt-3">이슈 번호에 사용됩니다 (예: TF-1, TF-2)</p>

        {createProject.isError && (
          <p className="text-sm text-error">프로젝트 생성에 실패했습니다. 다시 시도해주세요.</p>
        )}

        <div className="flex gap-3 pt-2">
          <Button type="button" variant="ghost" size="lg" className="flex-1" onClick={handleClose}>
            취소
          </Button>
          <Button type="submit" variant="primary" size="lg" className="flex-1" disabled={isLoading}>
            {isLoading ? (
              <Spinner size="sm" className="border-white border-t-transparent" />
            ) : (
              '프로젝트 생성'
            )}
          </Button>
        </div>
      </form>
    </Modal>
  )
}

function generateKey(name: string): string {
  const words = name.trim().split(/\s+/)
  if (words.length >= 2) {
    return words
      .map((w) => w[0])
      .join('')
      .toUpperCase()
      .slice(0, 10)
  }
  return name
    .trim()
    .toUpperCase()
    .replace(/[^A-Z0-9]/g, '')
    .slice(0, 4)
}
