import { env } from '@/shared/config/env'

export function redirectToGoogleOAuth() {
  const state = crypto.randomUUID()
  sessionStorage.setItem('oauth_state', state)

  const params = new URLSearchParams({
    client_id: env.googleClientId,
    redirect_uri: env.googleRedirectUri,
    response_type: 'code',
    scope: 'openid email profile',
    state,
    access_type: 'offline',
    prompt: 'consent',
  })

  window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?${params}`
}
