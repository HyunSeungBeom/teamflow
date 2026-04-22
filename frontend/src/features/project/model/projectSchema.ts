import { z } from 'zod'

export const createProjectSchema = z.object({
  name: z
    .string()
    .min(2, '프로젝트 이름은 2자 이상이어야 합니다')
    .max(100, '프로젝트 이름은 100자 이하여야 합니다'),
  key: z
    .string()
    .min(2, '프로젝트 키는 2자 이상이어야 합니다')
    .max(10, '프로젝트 키는 10자 이하여야 합니다')
    .regex(/^[A-Z][A-Z0-9]{1,9}$/, '대문자로 시작하고, 대문자/숫자만 허용됩니다'),
})

export type CreateProjectFormData = z.infer<typeof createProjectSchema>
