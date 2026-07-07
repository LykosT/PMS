export const SYSTEM_ROLES = ['ADMIN', 'USER', 'MANAGER'] as const
export type SystemRole = (typeof SYSTEM_ROLES)[number]

export const SENIORITY_LEVELS = ['JUNIOR', 'MID', 'SENIOR', 'LEAD', 'MANAGER'] as const
export type SeniorityLevel = (typeof SENIORITY_LEVELS)[number]

export const EMPLOYEE_STATUSES = [
  'ACTIVE',
  'ON_LEAVE',
  'SICK_LEAVE',
  'RESIGNED',
  'TERMINATED',
  'RETIRED',
] as const
export type EmployeeStatus = (typeof EMPLOYEE_STATUSES)[number]

export interface AuthUser {
  username: string
  fullName: string
  systemRole: SystemRole
}

export interface Employee {
  id: number
  uuid: string
  firstName: string
  lastName: string
  email: string
  phone: string | null
  seniorityLevel: SeniorityLevel
  employeeStatus: EmployeeStatus
  username: string | null
  systemRole: SystemRole | null
  userActive: boolean | null
  createdAt: string
  updatedAt: string
}

export interface EmployeeCreatePayload {
  firstName: string
  lastName: string
  email: string
  phone?: string
  seniorityLevel: SeniorityLevel
  employeeStatus: EmployeeStatus
  username: string
  password: string
  systemRole: SystemRole
  active: boolean
}

export interface EmployeeUpdatePayload extends Omit<EmployeeCreatePayload, 'password'> {
  // Omitted/undefined means "keep the current password"
  password?: string
}

export const PROJECT_STATUSES = [
  'PLANNED',
  'IN_PROGRESS',
  'COMPLETED',
  'ON_HOLD',
  'CANCELLED',
] as const
export type ProjectStatus = (typeof PROJECT_STATUSES)[number]

export const TASK_STATUSES = ['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE', 'CANCELLED'] as const
export type TaskStatus = (typeof TASK_STATUSES)[number]

export const PRIORITIES = ['LOW', 'MEDIUM', 'HIGH', 'URGENT', 'CRITICAL'] as const
export type Priority = (typeof PRIORITIES)[number]

export interface DashboardStats {
  employeeCount: number
  departmentCount: number
  projectCount: number
  teamCount: number
  taskCount: number
  businessRoleCount: number
}

export interface ApiError {
  code: string
  message: string
  fieldErrors: Record<string, string> | null
}
