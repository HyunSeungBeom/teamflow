import { useState, useCallback } from 'react'
import { useForm } from 'react-hook-form'
import { SlidePanel, Button, Select } from '@/shared/ui'
import { useUpdateTicket } from '../model/useUpdateTicket'
import { DeleteTicketDialog } from './DeleteTicketDialog'
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
  const [editingTitle, setEditingTitle] = useState(false)
  const [editingDescription, setEditingDescription] = useState(false)

  const { register, watch, setValue, getValues } = useForm<TicketFormValues>({
    defaultValues: {
      title: ticket.title,
      description: ticket.description ?? '',
      status: ticket.status,
      priority: ticket.priority,
      dueDate: ticket.dueDate ?? '',
    },
  })

  const titleValue = watch('title')
  const descriptionValue = watch('description')
  const statusValue = watch('status')
  const priorityValue = watch('priority')
  const dueDateValue = watch('dueDate')

  const handleUpdate = useCallback(
    (data: Record<string, unknown>) => {
      updateTicket.mutate({
        ticketNo: ticket.no,
        projectNo,
        data,
      })
    },
    [ticket.no, projectNo, updateTicket],
  )

  const handleTitleSave = useCallback(() => {
    const value = getValues('title').trim()
    if (value === ticket.title || value.length < 2) {
      setValue('title', ticket.title)
      setEditingTitle(false)
      return
    }
    handleUpdate({ title: value })
    setEditingTitle(false)
  }, [ticket.title, getValues, setValue, handleUpdate])

  const handleDescriptionSave = useCallback(() => {
    const value = getValues('description').trim()
    const newDesc = value || null
    if (newDesc === ticket.description) {
      setEditingDescription(false)
      return
    }
    handleUpdate({ description: newDesc })
    setEditingDescription(false)
  }, [ticket.description, getValues, handleUpdate])

  const handleStatusChange = useCallback(
    (e: React.ChangeEvent<HTMLSelectElement>) => {
      const newStatus = e.target.value as TicketStatus
      setValue('status', newStatus)
      handleUpdate({ status: newStatus })
    },
    [setValue, handleUpdate],
  )

  const handlePriorityChange = useCallback(
    (e: React.ChangeEvent<HTMLSelectElement>) => {
      const newPriority = e.target.value as TicketPriority
      setValue('priority', newPriority)
      handleUpdate({ priority: newPriority })
    },
    [setValue, handleUpdate],
  )

  const handleDueDateChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const value = e.target.value
      setValue('dueDate', value)
      handleUpdate({ dueDate: value || null })
    },
    [setValue, handleUpdate],
  )

  const handleDeleteSuccess = useCallback(() => {
    setShowDeleteDialog(false)
    onClose()
  }, [onClose])

  return (
    <>
      <SlidePanel open={open} onClose={onClose} title={ticket.ticketKey}>
        <div className="space-y-6">
          {/* Title */}
          <div>
            {editingTitle ? (
              <input
                type="text"
                className="w-full text-lg font-bold text-grey-900 border border-primary-400 rounded-input px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-400"
                {...register('title')}
                onBlur={handleTitleSave}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') handleTitleSave()
                  if (e.key === 'Escape') {
                    setValue('title', ticket.title)
                    setEditingTitle(false)
                  }
                }}
                autoFocus
              />
            ) : (
              <h3
                className="text-lg font-bold text-grey-900 cursor-pointer hover:bg-grey-50 rounded px-3 py-2 -mx-3 -my-2 transition-colors"
                onClick={() => setEditingTitle(true)}
              >
                {titleValue}
              </h3>
            )}
          </div>

          {/* Status & Priority */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-grey-600 mb-1">상태</label>
              <Select value={statusValue} options={statusOptions} onChange={handleStatusChange} />
            </div>
            <div>
              <label className="block text-sm font-medium text-grey-600 mb-1">우선순위</label>
              <Select
                value={priorityValue}
                options={priorityOptions}
                onChange={handlePriorityChange}
              />
            </div>
          </div>

          {/* Due Date */}
          <div>
            <label className="block text-sm font-medium text-grey-600 mb-1">기한</label>
            <input
              type="date"
              className="w-full border border-grey-200 rounded-input px-3 py-2 text-sm text-grey-900 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:border-primary-400"
              value={dueDateValue}
              onChange={handleDueDateChange}
            />
          </div>

          {/* Description */}
          <div>
            <label className="block text-sm font-medium text-grey-600 mb-1">설명</label>
            {editingDescription ? (
              <textarea
                className="w-full border border-primary-400 rounded-input px-3 py-2 text-sm text-grey-900 focus:outline-none focus:ring-2 focus:ring-primary-400 min-h-[120px] resize-y"
                {...register('description')}
                onBlur={handleDescriptionSave}
                onKeyDown={(e) => {
                  if (e.key === 'Escape') {
                    setValue('description', ticket.description ?? '')
                    setEditingDescription(false)
                  }
                }}
                rows={5}
                autoFocus
              />
            ) : (
              <div
                className="w-full min-h-[80px] border border-grey-200 rounded-input px-3 py-2 text-sm text-grey-700 cursor-pointer hover:bg-grey-50 transition-colors whitespace-pre-wrap"
                onClick={() => setEditingDescription(true)}
              >
                {descriptionValue || '설명을 추가하세요...'}
              </div>
            )}
          </div>

          {/* Meta */}
          {(ticket.createDate || ticket.updateDate) && (
            <div className="text-xs text-grey-400 space-y-1 pt-2 border-t border-grey-200">
              {ticket.createDate && <p>생성일: {ticket.createDate}</p>}
              {ticket.updateDate && <p>수정일: {ticket.updateDate}</p>}
            </div>
          )}

          {/* Delete Button */}
          <div className="pt-4">
            <Button
              variant="danger"
              size="sm"
              onClick={() => setShowDeleteDialog(true)}
              className="w-full"
            >
              티켓 삭제
            </Button>
          </div>
        </div>
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
