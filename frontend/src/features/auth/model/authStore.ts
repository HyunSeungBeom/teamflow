import { create } from 'zustand'
import type { User } from '@/entities/user'

interface AuthState {
  accessToken: string | null
  user: User | null
  isAuthenticated: boolean
  isLoading: boolean
  setAuth: (token: string, user: User) => void
  setAccessToken: (token: string) => void
  clearAuth: () => void
  setLoading: (loading: boolean) => void
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  user: null,
  isAuthenticated: false,
  isLoading: true,

  setAuth: (token, user) =>
    set({ accessToken: token, user, isAuthenticated: true, isLoading: false }),

  setAccessToken: (token) => set({ accessToken: token }),

  clearAuth: () => set({ accessToken: null, user: null, isAuthenticated: false, isLoading: false }),

  setLoading: (loading) => set({ isLoading: loading }),
}))
