import { useState, useCallback } from 'react'
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
  const [titleValue, setTitleValue] = useState(ticket.title)
  const [editingDescription, setEditingDescription] = useState(false)
  const [descriptionValue, setDescriptionValue] = useState(ticket.description ?? '')

  const handleUpdate = useCallback(
    (data: Record<string, unknown>) => {
      if (!ticket) return
      updateTicket.mutate({
        ticketNo: ticket.no,
        projectNo,
        data,
      })
    },
    [ticket, projectNo, updateTicket],
  )

  const handleTitleSave = useCallback(() => {
    if (!ticket || titleValue.trim() === ticket.title) {
      setEditingTitle(false)
      return
    }
    if (titleValue.trim().length < 2) return
    handleUpdate({ title: titleValue.trim() })
    setEditingTitle(false)
  }, [ticket, titleValue, handleUpdate])

  const handleDescriptionSave = useCallback(() => {
    if (!ticket) return
    const newDesc = descriptionValue.trim() || null
    if (newDesc === ticket.description) {
      setEditingDescription(false)
      return
    }
    handleUpdate({ description: newDesc })
    setEditingDescription(false)
  }, [ticket, descriptionValue, handleUpdate])

  const handleStatusChange = useCallback(
    (e: React.ChangeEvent<HTMLSelectElement>) => {
      handleUpdate({ status: e.target.value as TicketStatus })
    },
    [handleUpdate],
  )

  const handlePriorityChange = useCallback(
    (e: React.ChangeEvent<HTMLSelectElement>) => {
      handleUpdate({ priority: e.target.value as TicketPriority })
    },
    [handleUpdate],
  )

  const handleDueDateChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const value = e.target.value || null
      handleUpdate({ dueDate: value })
    },
    [handleUpdate],
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
                value={titleValue}
                onChange={(e) => setTitleValue(e.target.value)}
                onBlur={handleTitleSave}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') handleTitleSave()
                  if (e.key === 'Escape') {
                    setTitleValue(ticket.title)
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
                {ticket.title}
              </h3>
            )}
          </div>

          {/* Status & Priority */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-grey-600 mb-1">상태</label>
              <Select value={ticket.status} options={statusOptions} onChange={handleStatusChange} />
            </div>
            <div>
              <label className="block text-sm font-medium text-grey-600 mb-1">우선순위</label>
              <Select
                value={ticket.priority}
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
              value={ticket.dueDate ?? ''}
              onChange={handleDueDateChange}
            />
          </div>

          {/* Description */}
          <div>
            <label className="block text-sm font-medium text-grey-600 mb-1">설명</label>
            {editingDescription ? (
              <textarea
                className="w-full border border-primary-400 rounded-input px-3 py-2 text-sm text-grey-900 focus:outline-none focus:ring-2 focus:ring-primary-400 min-h-[120px] resize-y"
                value={descriptionValue}
                onChange={(e) => setDescriptionValue(e.target.value)}
                onBlur={handleDescriptionSave}
                onKeyDown={(e) => {
                  if (e.key === 'Escape') {
                    setDescriptionValue(ticket.description ?? '')
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
                {ticket.description || '설명을 추가하세요...'}
              </div>
            )}
          </div>

          {/* Meta */}
          <div className="text-xs text-grey-400 space-y-1 pt-2 border-t border-grey-200">
            <p>생성일: {ticket.createDate}</p>
            <p>수정일: {ticket.updateDate}</p>
          </div>

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
