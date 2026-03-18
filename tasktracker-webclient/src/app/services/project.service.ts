import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project, ProjectRequest } from '../models/project.model';

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private adminUrl  = '/admin/Project';
  private publicUrl = '/public/Project';

  constructor(private http: HttpClient) {}

  create(project: ProjectRequest): Observable<Project> {
    return this.http.post<Project>(`${this.adminUrl}/create`, project);
  }

  readAll(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.adminUrl}/read`);
  }

  readByName(name: string): Observable<Project[]> {
    const params = new HttpParams().set('name', name);
    return this.http.get<Project[]>(`${this.adminUrl}/readByName`, { params });
  }

  update(id: number, project: ProjectRequest): Observable<Project> {
    return this.http.put<Project>(`${this.adminUrl}/edit/${id}`, project);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminUrl}/delete/${id}`);
  }

  publicReadAll(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.publicUrl}/read`);
  }

  publicReadByName(name: string): Observable<Project[]> {
    const params = new HttpParams().set('name', name);
    return this.http.get<Project[]>(`${this.publicUrl}/readByName`, { params });
  }
}
