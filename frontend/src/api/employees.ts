import { api } from './client'
import type { DashboardStats, Employee, EmployeeCreatePayload, EmployeeUpdatePayload } from '../types'

export async function listEmployees(): Promise<Employee[]> {
  const { data } = await api.get<Employee[]>('/employees')
  return data
}

export async function getEmployee(id: number): Promise<Employee> {
  const { data } = await api.get<Employee>(`/employees/${id}`)
  return data
}

export async function createEmployee(payload: EmployeeCreatePayload): Promise<Employee> {
  const { data } = await api.post<Employee>('/employees', payload)
  return data
}

export async function updateEmployee(id: number, payload: EmployeeUpdatePayload): Promise<Employee> {
  const { data } = await api.put<Employee>(`/employees/${id}`, payload)
  return data
}

export async function deleteEmployee(id: number): Promise<void> {
  await api.delete(`/employees/${id}`)
}

export async function fetchDashboardStats(): Promise<DashboardStats> {
  const { data } = await api.get<DashboardStats>('/dashboard')
  return data
}
