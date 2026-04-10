import { GoogleLoginButton } from '@/features/auth'

export function LandingPage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-indigo-900 text-white">
      {/* Nav */}
      <nav className="flex items-center justify-between px-8 py-5">
        <span className="text-xl font-extrabold">
          Team<span className="text-indigo-400">Flow</span>
        </span>
        <a
          href="/login"
          className="px-5 py-2 text-sm font-semibold border border-white/30 rounded-lg hover:bg-white/10 transition-colors"
        >
          로그인
        </a>
      </nav>

      {/* Hero */}
      <main className="flex flex-col items-center justify-center text-center px-6 pt-32 pb-20">
        <h1 className="text-5xl md:text-6xl font-black leading-tight mb-6">
          문서 + 이슈 + 채팅,
          <br />
          <span className="text-indigo-400">하나로 끝.</span>
        </h1>
        <p className="text-lg text-white/60 max-w-lg mb-10">
          Notion, Jira, Slack을 넘나드는 컨텍스트 스위칭은 이제 그만.
          <br />
          TeamFlow에서 팀의 모든 협업을 한 곳에서.
        </p>
        <GoogleLoginButton />

        {/* Features */}
        <div className="flex gap-6 mt-16 flex-wrap justify-center">
          {[
            { icon: '📄', title: '문서', desc: '블록 에디터로 함께 쓰는 문서' },
            { icon: '▦', title: '이슈', desc: '칸반 보드로 일감 관리' },
            { icon: '💬', title: '채팅', desc: '실시간 메시지로 빠른 소통' },
          ].map((feat) => (
            <div
              key={feat.title}
              className="w-48 p-5 bg-white/5 border border-white/10 rounded-2xl text-left"
            >
              <div className="text-2xl mb-3">{feat.icon}</div>
              <h3 className="font-semibold mb-1">{feat.title}</h3>
              <p className="text-sm text-white/50">{feat.desc}</p>
            </div>
          ))}
        </div>
      </main>
    </div>
  )
}
