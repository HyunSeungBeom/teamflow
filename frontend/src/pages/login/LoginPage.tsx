import { GoogleLoginButton } from '@/features/auth'

export function LoginPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="bg-white rounded-2xl shadow-lg p-10 w-full max-w-md text-center">
        <h1 className="text-2xl font-bold text-gray-900 mb-2">
          Team<span className="text-indigo-500">Flow</span>
        </h1>
        <p className="text-gray-500 mb-8">팀 협업을 하나로</p>
        <div className="flex justify-center">
          <GoogleLoginButton />
        </div>
      </div>
    </div>
  )
}
