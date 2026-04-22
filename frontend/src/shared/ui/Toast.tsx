import { useEffect } from 'react'
import { twMerge } from 'tailwind-merge'

const types = {
  success: 'bg-success text-white',
  error: 'bg-error text-white',
  warning: 'bg-warning text-grey-900',
  info: 'bg-info text-white',
} as const

interface ToastProps {
  message: string
  type?: keyof typeof types
  open: boolean
  onClose: () => void
  duration?: number
  position?: 'top' | 'bottom'
}

export function Toast({
  message,
  type = 'success',
  open,
  onClose,
  duration = 3000,
  position = 'bottom',
}: ToastProps) {
  useEffect(() => {
    if (open && duration > 0) {
      const timer = setTimeout(onClose, duration)
      return () => clearTimeout(timer)
    }
  }, [open, duration, onClose])

  return (
    <div
      className={twMerge(
        'fixed left-1/2 -translate-x-1/2 z-[60] px-5 py-3 rounded-lg shadow-toast',
        'transition-all duration-300',
        types[type],
        position === 'top' ? 'top-6' : 'bottom-6',
        open ? 'opacity-100 translate-y-0' : 'opacity-0 pointer-events-none',
        open && position === 'top' && 'translate-y-0',
        open && position === 'bottom' && 'translate-y-0',
        !open && position === 'top' && '-translate-y-4',
        !open && position === 'bottom' && 'translate-y-4',
      )}
      role="alert"
      aria-live="polite"
    >
      <p className="text-sm font-medium">{message}</p>
    </div>
  )
}
