import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskRequest } from '../models/task.model';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private adminBase  = '/admin';
  private publicBase = '/public';

  constructor(private http: HttpClient) {}

  create(projectId: number, task: TaskRequest): Observable<Task> {
    return this.http.post<Task>(`${this.adminBase}/${projectId}/task/create`, task);
  }

  readAll(projectId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.adminBase}/${projectId}/task/read`);
  }

  readByName(projectId: number, title: string): Observable<Task[]> {
    const params = new HttpParams().set('title', title);
    return this.http.get<Task[]>(`${this.adminBase}/${projectId}/task/readByName`, { params });
  }

  update(projectId: number, taskId: number, task: TaskRequest): Observable<Task> {
    return this.http.put<Task>(`${this.adminBase}/${projectId}/task/edit/${taskId}`, task);
  }

  delete(projectId: number, taskId: number): Observable<void> {
    return this.http.delete<void>(`${this.adminBase}/${projectId}/task/delete/${taskId}`);
  }

  publicReadAll(projectId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.publicBase}/${projectId}/task/read`);
  }

  publicReadByName(projectId: number, title: string): Observable<Task[]> {
    const params = new HttpParams().set('title', title);
    return this.http.get<Task[]>(`${this.publicBase}/${projectId}/task/readByName`, { params });
  }
}
