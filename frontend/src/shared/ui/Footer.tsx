import { Link } from 'react-router-dom'

/**
 * Organism/Footer
 *
 * 반응형 컨벤션 적용:
 *   배경: 전폭 (#0F172A)
 *   콘텐츠: max-w-[1280px] mx-auto + fluid padding
 *   Desktop: 가로 배치, gap-20
 *   Tablet: 가로 유지, gap 축소
 *   Mobile: 세로 스택, 링크 2열 그리드
 */

const linkGroups = [
  {
    title: '제품',
    links: [
      { label: '기능 소개', href: '#' },
      { label: '요금제', href: '#' },
      { label: '업데이트 노트', href: '#' },
      { label: '로드맵', href: '#' },
    ],
  },
  {
    title: '회사',
    links: [
      { label: '소개', href: '#' },
      { label: '채용', href: '#' },
      { label: '블로그', href: '#' },
      { label: '문의하기', href: '#' },
    ],
  },
  {
    title: '지원',
    links: [
      { label: '도움말 센터', href: '#' },
      { label: '개인정보처리방침', href: '/privacy' },
      { label: '이용약관', href: '/terms' },
      { label: 'API 문서', href: '#' },
    ],
  },
] as const

export function Footer() {
  return (
    <footer className="bg-[#0F172A] text-white">
      <div className="px-fluid-page-x py-fluid-py">
        {/* Top Section */}
        <div className="flex flex-col md:flex-row justify-between gap-fluid-gap">
          {/* Logo + Tagline */}
          <div className="max-w-xs shrink-0">
            <Link to="/" className="text-xl font-bold inline-block">
              Team<span className="text-primary-400">Flow</span>
            </Link>
            <p className="mt-3 text-sm text-slate-400 leading-relaxed">
              팀의 협업을 하나로 연결합니다.
              <br />
              워크스페이스 기반 프로젝트 관리 플랫폼.
            </p>
          </div>

          {/* Link Groups */}
          <div className="grid grid-cols-2 sm:grid-cols-3 gap-8 lg:gap-16">
            {linkGroups.map((group) => (
              <div key={group.title}>
                <h3 className="text-sm font-semibold mb-3 lg:mb-4">{group.title}</h3>
                <ul className="space-y-2.5 lg:space-y-3">
                  {group.links.map((link) => (
                    <li key={link.label}>
                      <Link
                        to={link.href}
                        className="text-sm text-slate-400 hover:text-white transition-colors"
                      >
                        {link.label}
                      </Link>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        </div>

        {/* Divider + Bottom */}
        <div className="border-t border-slate-800 mt-fluid-gap pt-6 lg:pt-8">
          <div className="flex flex-col sm:flex-row justify-between items-center gap-4">
            <p className="text-xs text-slate-500">
              © 2026 TeamFlow. All rights reserved.
            </p>
            <div className="flex flex-wrap justify-center gap-4 sm:gap-6">
              <Link to="/privacy" className="text-xs text-slate-500 hover:text-slate-300 transition-colors">
                개인정보처리방침
              </Link>
              <Link to="/terms" className="text-xs text-slate-500 hover:text-slate-300 transition-colors">
                이용약관
              </Link>
              <Link to="#" className="text-xs text-slate-500 hover:text-slate-300 transition-colors">
                쿠키 정책
              </Link>
            </div>
          </div>
        </div>
      </div>
    </footer>
  )
}
