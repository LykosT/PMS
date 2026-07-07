import { api } from './client'
import type { AuthUser } from '../types'

export async function login(username: string, password: string): Promise<AuthUser> {
  const { data } = await api.post<AuthUser>('/auth/login', { username, password })
  return data
}

export async function logout(): Promise<void> {
  await api.post('/auth/logout')
}

export async function fetchCurrentUser(): Promise<AuthUser> {
  const { data } = await api.get<AuthUser>('/auth/me')
  return data
}
