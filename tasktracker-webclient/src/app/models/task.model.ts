export type TaskStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export interface Task {
  id?: number;
  title: string;
  description?: string;
  status: TaskStatus;
  createdAt?: string;
  project?: { id: number };
}

export interface TaskRequest {
  title: string;
  description?: string;
  status: TaskStatus;
}
