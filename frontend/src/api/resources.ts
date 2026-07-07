import { api } from './client'

/** A generic API row: every read DTO has a numeric id plus arbitrary fields. */
export type Row = { id: number } & Record<string, unknown>

export async function listResource(resource: string): Promise<Row[]> {
  const { data } = await api.get<Row[]>(`/${resource}`)
  return data
}

export async function getResource(resource: string, id: number): Promise<Row> {
  const { data } = await api.get<Row>(`/${resource}/${id}`)
  return data
}

export async function createResource(resource: string, payload: Record<string, unknown>): Promise<Row> {
  const { data } = await api.post<Row>(`/${resource}`, payload)
  return data
}

export async function updateResource(
  resource: string,
  id: number,
  payload: Record<string, unknown>,
): Promise<Row> {
  const { data } = await api.put<Row>(`/${resource}/${id}`, payload)
  return data
}

export async function deleteResource(resource: string, id: number): Promise<void> {
  await api.delete(`/${resource}/${id}`)
}
