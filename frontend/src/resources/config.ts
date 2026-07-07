import type { Row } from '../api/resources'
import { PRIORITIES, PROJECT_STATUSES, TASK_STATUSES } from '../types'

export interface ColumnDef {
  key: string
  label: string
  /** 'badge' renders the value as a colored pill (statuses, priorities) */
  kind?: 'badge'
}

export interface FieldDef {
  name: string
  label: string
  type: 'text' | 'textarea' | 'date' | 'select'
  required?: boolean
  /** Static choices (enum values) */
  options?: readonly string[]
  /** Load choices from this API resource; the submitted value is the numeric id */
  optionsResource?: string
  /** Label for a resource option row (defaults to row.name) */
  optionLabel?: (item: Row) => string
}

export interface ResourceConfig {
  /** API path segment, e.g. "business-roles" */
  resource: string
  /** SPA route segment, e.g. "roles" */
  route: string
  title: string
  singular: string
  columns: ColumnDef[]
  fields: FieldDef[]
}

export const BADGE_STYLES: Record<string, string> = {
  PLANNED: 'bg-sky-100 text-sky-700',
  TODO: 'bg-slate-100 text-slate-600',
  IN_PROGRESS: 'bg-blue-100 text-blue-700',
  IN_REVIEW: 'bg-purple-100 text-purple-700',
  COMPLETED: 'bg-green-100 text-green-700',
  DONE: 'bg-green-100 text-green-700',
  ON_HOLD: 'bg-amber-100 text-amber-700',
  CANCELLED: 'bg-slate-200 text-slate-600',
  LOW: 'bg-slate-100 text-slate-600',
  MEDIUM: 'bg-sky-100 text-sky-700',
  HIGH: 'bg-amber-100 text-amber-700',
  URGENT: 'bg-orange-100 text-orange-700',
  CRITICAL: 'bg-red-100 text-red-700',
}

const employeeLabel = (item: Row) => `${item.firstName} ${item.lastName}`

export const RESOURCES: ResourceConfig[] = [
  {
    resource: 'departments',
    route: 'departments',
    title: 'Departments',
    singular: 'Department',
    columns: [
      { key: 'name', label: 'Name' },
      { key: 'managerFullName', label: 'Manager' },
      { key: 'description', label: 'Description' },
    ],
    fields: [
      { name: 'name', label: 'Name', type: 'text', required: true },
      { name: 'description', label: 'Description', type: 'textarea' },
      {
        name: 'managerId',
        label: 'Manager',
        type: 'select',
        required: true,
        optionsResource: 'employees',
        optionLabel: employeeLabel,
      },
    ],
  },
  {
    resource: 'projects',
    route: 'projects',
    title: 'Projects',
    singular: 'Project',
    columns: [
      { key: 'name', label: 'Name' },
      { key: 'projectStatus', label: 'Status', kind: 'badge' },
      { key: 'priority', label: 'Priority', kind: 'badge' },
      { key: 'startDate', label: 'Start date' },
      { key: 'departmentName', label: 'Department' },
      { key: 'managerFullName', label: 'Manager' },
    ],
    fields: [
      { name: 'name', label: 'Name', type: 'text', required: true },
      { name: 'description', label: 'Description', type: 'textarea' },
      {
        name: 'projectStatus',
        label: 'Status',
        type: 'select',
        required: true,
        options: PROJECT_STATUSES,
      },
      { name: 'priority', label: 'Priority', type: 'select', required: true, options: PRIORITIES },
      { name: 'startDate', label: 'Start date', type: 'date' },
      {
        name: 'departmentId',
        label: 'Department',
        type: 'select',
        required: true,
        optionsResource: 'departments',
      },
      {
        name: 'managerId',
        label: 'Manager',
        type: 'select',
        required: true,
        optionsResource: 'employees',
        optionLabel: employeeLabel,
      },
    ],
  },
  {
    resource: 'teams',
    route: 'teams',
    title: 'Teams',
    singular: 'Team',
    columns: [
      { key: 'name', label: 'Name' },
      { key: 'departmentName', label: 'Department' },
      { key: 'projectName', label: 'Project' },
      { key: 'leadFullName', label: 'Lead' },
    ],
    fields: [
      { name: 'name', label: 'Name', type: 'text', required: true },
      { name: 'description', label: 'Description', type: 'textarea' },
      {
        name: 'departmentId',
        label: 'Department',
        type: 'select',
        required: true,
        optionsResource: 'departments',
      },
      {
        name: 'projectId',
        label: 'Project',
        type: 'select',
        required: true,
        optionsResource: 'projects',
      },
      {
        name: 'leadId',
        label: 'Lead (optional)',
        type: 'select',
        optionsResource: 'employees',
        optionLabel: employeeLabel,
      },
    ],
  },
  {
    resource: 'tasks',
    route: 'tasks',
    title: 'Tasks',
    singular: 'Task',
    columns: [
      { key: 'name', label: 'Name' },
      { key: 'taskStatus', label: 'Status', kind: 'badge' },
      { key: 'priority', label: 'Priority', kind: 'badge' },
      { key: 'dueDate', label: 'Due date' },
      { key: 'projectName', label: 'Project' },
      { key: 'teamName', label: 'Team' },
      { key: 'assigneeFullName', label: 'Assignee' },
    ],
    fields: [
      { name: 'name', label: 'Name', type: 'text', required: true },
      { name: 'description', label: 'Description', type: 'textarea' },
      { name: 'taskStatus', label: 'Status', type: 'select', required: true, options: TASK_STATUSES },
      { name: 'priority', label: 'Priority', type: 'select', required: true, options: PRIORITIES },
      { name: 'dueDate', label: 'Due date', type: 'date' },
      {
        name: 'projectId',
        label: 'Project',
        type: 'select',
        required: true,
        optionsResource: 'projects',
      },
      { name: 'teamId', label: 'Team', type: 'select', required: true, optionsResource: 'teams' },
      {
        name: 'assigneeId',
        label: 'Assignee (optional)',
        type: 'select',
        optionsResource: 'employees',
        optionLabel: employeeLabel,
      },
    ],
  },
  {
    resource: 'business-roles',
    route: 'roles',
    title: 'Business Roles',
    singular: 'Business Role',
    columns: [
      { key: 'name', label: 'Name' },
      { key: 'description', label: 'Description' },
    ],
    fields: [
      { name: 'name', label: 'Name', type: 'text', required: true },
      { name: 'description', label: 'Description', type: 'textarea' },
    ],
  },
]
