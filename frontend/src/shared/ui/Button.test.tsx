import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, it, expect, vi } from 'vitest'
import { Button } from './Button'

describe('Button', () => {
  it('텍스트가 올바르게 렌더링된다', () => {
    render(<Button>클릭</Button>)
    expect(screen.getByRole('button', { name: '클릭' })).toBeInTheDocument()
  })

  it('primary variant에 올바른 스타일이 적용된다', () => {
    render(<Button variant="primary">Primary</Button>)
    expect(screen.getByRole('button').className).toContain('bg-primary-600')
  })

  it('secondary variant에 border가 적용된다', () => {
    render(<Button variant="secondary">Secondary</Button>)
    expect(screen.getByRole('button').className).toContain('border')
  })

  it('sm size에 h-8이 적용된다', () => {
    render(<Button size="sm">Small</Button>)
    expect(screen.getByRole('button').className).toContain('h-8')
  })

  it('lg size에 h-12가 적용된다', () => {
    render(<Button size="lg">Large</Button>)
    expect(screen.getByRole('button').className).toContain('h-12')
  })

  it('클릭하면 onClick이 호출된다', async () => {
    const handleClick = vi.fn()
    render(<Button onClick={handleClick}>클릭</Button>)
    await userEvent.click(screen.getByRole('button'))
    expect(handleClick).toHaveBeenCalledTimes(1)
  })

  it('disabled 상태에서는 클릭이 동작하지 않는다', async () => {
    const handleClick = vi.fn()
    render(
      <Button disabled onClick={handleClick}>
        비활성
      </Button>,
    )
    await userEvent.click(screen.getByRole('button'))
    expect(handleClick).not.toHaveBeenCalled()
  })

  it('추가 className이 병합된다', () => {
    render(<Button className="w-full">Full</Button>)
    expect(screen.getByRole('button').className).toContain('w-full')
  })
})
