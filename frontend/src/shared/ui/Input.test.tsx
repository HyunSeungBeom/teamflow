import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, it, expect } from 'vitest'
import { Input } from './Input'

describe('Input', () => {
  it('input이 렌더링된다', () => {
    render(<Input placeholder="이메일" />)
    expect(screen.getByPlaceholderText('이메일')).toBeInTheDocument()
  })

  it('label이 표시된다', () => {
    render(<Input label="이메일" />)
    expect(screen.getByText('이메일')).toBeInTheDocument()
  })

  it('error 메시지가 표시된다', () => {
    render(<Input error="필수 입력입니다" />)
    expect(screen.getByText('필수 입력입니다')).toBeInTheDocument()
  })

  it('error 시 빨간 border가 적용된다', () => {
    render(<Input error="에러" />)
    expect(screen.getByRole('textbox').className).toContain('border-error')
  })

  it('텍스트를 입력할 수 있다', async () => {
    render(<Input placeholder="이름" />)
    const input = screen.getByPlaceholderText('이름')
    await userEvent.type(input, 'TeamFlow')
    expect(input).toHaveValue('TeamFlow')
  })

  it('disabled 상태에서 입력 불가', () => {
    render(<Input disabled placeholder="비활성" />)
    expect(screen.getByPlaceholderText('비활성')).toBeDisabled()
  })

  it('높이가 h-11 스펙에 맞다', () => {
    render(<Input />)
    expect(screen.getByRole('textbox').className).toContain('h-11')
  })
})
