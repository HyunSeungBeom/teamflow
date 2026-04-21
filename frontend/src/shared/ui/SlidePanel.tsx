import { useEffect, useRef, type ReactNode } from 'react'
import { twMerge } from 'tailwind-merge'

interface SlidePanelProps {
  open: boolean
  onClose: () => void
  title?: string
  children: ReactNode
  className?: string
  width?: string
}

export function SlidePanel({
  open,
  onClose,
  title,
  children,
  className,
  width = 'w-[480px]',
}: SlidePanelProps) {
  const overlayRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose()
    }

    if (open) {
      document.addEventListener('keydown', handleEsc)
      document.body.style.overflow = 'hidden'
    }

    return () => {
      document.removeEventListener('keydown', handleEsc)
      document.body.style.overflow = ''
    }
  }, [open, onClose])

  return (
    <>
      {/* Backdrop */}
      <div
        ref={overlayRef}
        className={twMerge(
          'fixed inset-0 z-40 bg-black/50 transition-opacity duration-300',
          open ? 'opacity-100' : 'opacity-0 pointer-events-none',
        )}
        onClick={(e) => {
          if (e.target === overlayRef.current) onClose()
        }}
      />

      {/* Panel */}
      <div
        className={twMerge(
          'fixed top-0 right-0 z-50 h-full bg-white shadow-slide-panel',
          'transition-transform duration-300 ease-out',
          'flex flex-col',
          width,
          open ? 'translate-x-0' : 'translate-x-full',
          className,
        )}
      >
        {/* Header */}
        {title && (
          <div className="flex items-center justify-between px-6 py-4 border-b border-grey-200">
            <h2 className="text-lg font-bold text-grey-900">{title}</h2>
            <button
              onClick={onClose}
              className="p-1 text-grey-400 hover:text-grey-600 transition-colors"
              aria-label="닫기"
            >
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </button>
          </div>
        )}

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6">{children}</div>
      </div>
    </>
  )
}
