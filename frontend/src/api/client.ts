import axios from 'axios'
import type { ApiError } from '../types'

// Session cookie + CSRF header, same-origin via the Vite dev proxy.
// Axios reads the XSRF-TOKEN cookie set by Spring Security and echoes it
// back as the X-XSRF-TOKEN header on mutating requests.
export const api = axios.create({
  baseURL: '/api',
  withCredentials: true,
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
  withXSRFToken: true,
})

export function extractApiError(error: unknown): ApiError {
  if (axios.isAxiosError(error) && error.response?.data?.message) {
    return error.response.data as ApiError
  }
  return { code: 'Unknown', message: 'Something went wrong. Please try again.', fieldErrors: null }
}

export function isUnauthorized(error: unknown): boolean {
  return axios.isAxiosError(error) && error.response?.status === 401
}
