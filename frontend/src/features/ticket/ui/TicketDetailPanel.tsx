import { useState, useCallback, useRef } from 'react'
import { useForm } from 'react-hook-form'
import { SlidePanel, Button, Select } from '@/shared/ui'
import { useUpdateTicket } from '../model/useUpdateTicket'
import { DeleteTicketDialog } from './DeleteTicketDialog'
import { toast } from '@/shared/model/useToastStore'
import type { Ticket, TicketStatus, TicketPriority } from '@/entities/ticket'

interface TicketDetailPanelProps {
  ticket: Ticket | null
  open: boolean
  onClose: () => void
  projectNo: number
}

const statusOptions = [
  { value: 'BACKLOG', label: 'Backlog' },
  { value: 'TODO', label: 'To Do' },
  { value: 'IN_PROGRESS', label: 'In Progress' },
  { value: 'DONE', label: 'Done' },
]

const priorityOptions = [
  { value: 'LOW', label: 'Low' },
  { value: 'MEDIUM', label: 'Medium' },
  { value: 'HIGH', label: 'High' },
  { value: 'CRITICAL', label: 'Critical' },
]

export function TicketDetailPanel({ ticket, open, onClose, projectNo }: TicketDetailPanelProps) {
  if (!ticket) return null

  return (
    <TicketDetailPanelInner
      key={ticket.no}
      ticket={ticket}
      open={open}
      onClose={onClose}
      projectNo={projectNo}
    />
  )
}

interface TicketFormValues {
  title: string
  description: string
  status: TicketStatus
  priority: TicketPriority
  dueDate: string
}

function TicketDetailPanelInner({
  ticket,
  open,
  onClose,
  projectNo,
}: {
  ticket: Ticket
  open: boolean
  onClose: () => void
  projectNo: number
}) {
  const updateTicket = useUpdateTicket()
  const [showDeleteDialog, setShowDeleteDialog] = useState(false)
  const dateInputRef = useRef<HTMLInputElement | null>(null)

  const {
    register,
    reset,
    handleSubmit,
    formState: { isDirty },
  } = useForm<TicketFormValues>({
    defaultValues: {
      title: ticket.title,
      description: ticket.description ?? '',
      status: ticket.status,
      priority: ticket.priority,
      dueDate: ticket.dueDate ?? '',
    },
  })

  const { ref: dueDateRef, ...dueDateRest } = register('dueDate')

  const onSubmit = useCallback(
    (data: TicketFormValues) => {
      const payload: Record<string, unknown> = {}

      if (data.title.trim() !== ticket.title) payload.title = data.title.trim()
      if ((data.description.trim() || null) !== ticket.description)
        payload.description = data.description.trim() || null
      if (data.status !== ticket.status) payload.status = data.status
      if (data.priority !== ticket.priority) payload.priority = data.priority
      if ((data.dueDate || null) !== ticket.dueDate) payload.dueDate = data.dueDate || null

      if (Object.keys(payload).length === 0) return

      updateTicket.mutate(
        { ticketNo: ticket.no, projectNo, data: payload },
        {
          onSuccess: () => {
            reset(data)
            toast.success('티켓이 수정되었습니다.')
          },
          onError: () => {
            toast.error('티켓 수정에 실패했습니다.')
          },
        },
      )
    },
    [ticket, projectNo, updateTicket, reset],
  )

  const handleCancel = useCallback(() => {
    reset()
  }, [reset])

  const handleDeleteSuccess = useCallback(() => {
    setShowDeleteDialog(false)
    toast.success('티켓이 삭제되었습니다.')
    onClose()
  }, [onClose])

  return (
    <>
      <SlidePanel open={open} onClose={onClose} title={ticket.ticketKey}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          {/* Title */}
          <div>
            <label className="block text-sm font-medium text-grey-600 mb-1">제목</label>
            <input
              type="text"
              className="w-full text-base font-semibold text-grey-900 border border-grey-200 rounded-input px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:border-primary-400"
              {...register('title', { required: true, minLength: 2 })}
            />
          </div>

          {/* Status & Priority */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-grey-600 mb-1">상태</label>
              <Select options={statusOptions} {...register('status')} />
            </div>
            <div>
              <label className="block text-sm font-medium text-grey-600 mb-1">우선순위</label>
              <Select options={priorityOptions} {...register('priority')} />
            </div>
          </div>

          {/* Due Date */}
          <div>
            <label className="block text-sm font-medium text-grey-600 mb-1">기한</label>
            <input
              type="date"
              className="w-full border border-grey-200 rounded-input px-3 py-2 text-sm text-grey-900 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:border-primary-400 cursor-pointer"
              {...dueDateRest}
              ref={(e) => {
                dueDateRef(e)
                dateInputRef.current = e
              }}
              onClick={() => dateInputRef.current?.showPicker()}
            />
          </div>

          {/* Description */}
          <div>
            <label className="block text-sm font-medium text-grey-600 mb-1">설명</label>
            <textarea
              className="w-full border border-grey-200 rounded-input px-3 py-2 text-sm text-grey-900 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:border-primary-400 min-h-[120px] resize-y"
              placeholder="설명을 추가하세요..."
              {...register('description')}
              rows={5}
            />
          </div>

          {/* Meta */}
          {(ticket.createDate || ticket.updateDate) && (
            <div className="text-xs text-grey-400 space-y-1 pt-2 border-t border-grey-200">
              {ticket.createDate && <p>생성일: {ticket.createDate}</p>}
              {ticket.updateDate && <p>수정일: {ticket.updateDate}</p>}
            </div>
          )}

          {/* Action Buttons */}
          <div className="flex gap-3 pt-2">
            <Button
              type="submit"
              variant="primary"
              size="sm"
              className="flex-1 bg-primary-100 text-primary-700 hover:bg-primary-200 active:bg-primary-300 shadow-none"
              disabled={!isDirty || updateTicket.isPending}
            >
              {updateTicket.isPending ? '저장 중...' : '수정하기'}
            </Button>
            <Button
              type="button"
              variant="secondary"
              size="sm"
              className="flex-1"
              onClick={handleCancel}
              disabled={!isDirty}
            >
              취소
            </Button>
          </div>

          {/* Delete Button */}
          <div className="pt-2 border-t border-grey-200">
            <Button
              type="button"
              variant="danger"
              size="sm"
              onClick={() => setShowDeleteDialog(true)}
              className="w-full bg-red-50 text-red-600 hover:bg-red-100 active:bg-red-200 shadow-none"
            >
              티켓 삭제
            </Button>
          </div>
        </form>
      </SlidePanel>

      <DeleteTicketDialog
        isOpen={showDeleteDialog}
        onClose={() => setShowDeleteDialog(false)}
        onSuccess={handleDeleteSuccess}
        ticket={ticket}
        projectNo={projectNo}
      />
    </>
  )
}
