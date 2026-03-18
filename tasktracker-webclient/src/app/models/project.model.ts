export type ProjectStatus = 'ACTIVE' | 'PAUSED' | 'COMPLETED' | 'CANCELLED';

export interface Project {
  id?: number;
  name: string;
  description?: string;
  technology?: string;
  status: ProjectStatus;
  createdAt?: string;
}

export interface ProjectRequest {
  name: string;
  description?: string;
  technology?: string;
  status: ProjectStatus;
}
