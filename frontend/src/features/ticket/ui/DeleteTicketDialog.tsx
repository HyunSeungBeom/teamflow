import { Modal, Button } from '@/shared/ui'
import { useDeleteTicket } from '../model/useDeleteTicket'
import { toast } from '@/shared/model/useToastStore'
import type { Ticket } from '@/entities/ticket'

interface DeleteTicketDialogProps {
  isOpen: boolean
  onClose: () => void
  onSuccess: () => void
  ticket: Ticket | null
  projectNo: number
}

export function DeleteTicketDialog({
  isOpen,
  onClose,
  onSuccess,
  ticket,
  projectNo,
}: DeleteTicketDialogProps) {
  const deleteTicket = useDeleteTicket()

  const handleDelete = async () => {
    if (!ticket) return
    try {
      await deleteTicket.mutateAsync({ ticketNo: ticket.no, projectNo })
      onSuccess()
    } catch (error) {
      console.error('티켓 삭제 실패:', error)
      toast.error('티켓 삭제에 실패했습니다.')
    }
  }

  if (!ticket) return null

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="티켓 삭제">
      <div className="space-y-4">
        <p className="text-sm text-grey-700">
          <strong>{ticket.ticketKey}</strong> 티켓을 삭제하시겠습니까?
        </p>
        <p className="text-xs text-grey-500">이 작업은 되돌릴 수 없습니다.</p>
        <div className="flex justify-end gap-3 pt-2">
          <Button variant="secondary" size="sm" onClick={onClose}>
            취소
          </Button>
          <Button
            variant="danger"
            size="sm"
            onClick={handleDelete}
            disabled={deleteTicket.isPending}
          >
            {deleteTicket.isPending ? '삭제 중...' : '삭제'}
          </Button>
        </div>
      </div>
    </Modal>
  )
}
