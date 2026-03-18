import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject, switchMap } from 'rxjs';
import { ProjectService } from '../../services/project.service';
import { TranslationService } from '../../services/translation.service';
import { Project } from '../../models/project.model';

@Component({
  selector: 'app-public-view',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page">
      <div class="topbar">
        <h1 class="page-title">Visualização Pública</h1>
        <div class="endpoint-badge">
          GET /public/Project/read · <span class="public-label">Sem autenticação</span>
        </div>
      </div>
      <div class="search-wrap">
        <span>🔍</span>
        <input type="text" placeholder="Buscar projeto por nome..."
          [(ngModel)]="searchTerm" (ngModelChange)="onSearch($event)" />
      </div>
      <div class="table-card">
        <div class="table-header">
          <span>Projeto</span><span>Status</span><span>Tecnologias</span><span>Criado em</span>
        </div>
        <div *ngIf="projects.length === 0" class="empty-state">Nenhum projeto encontrado.</div>
        <div class="proj-row" *ngFor="let p of projects">
          <div><div class="proj-name">{{ p.name }}</div></div>
          <div><span class="badge" [ngClass]="t.projectStatusBadge(p.status)">{{ t.projectStatusLabel(p.status) }}</span></div>
          <div class="tech-tags">
            <span class="tech-tag" *ngFor="let tech of techList(p.technology)">{{ tech }}</span>
          </div>
          <div class="date-col">{{ p.createdAt | date:'dd/MM/yyyy' }}</div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page { padding: 24px; display: flex; flex-direction: column; gap: 16px; }
    .topbar { display: flex; align-items: center; gap: 16px; flex-wrap: wrap; }
    .page-title { font-size: 16px; font-weight: 500; margin: 0; flex: 1; }
    .endpoint-badge { font-size: 12px; color: #73726c; background: #f5f5f3; padding: 6px 12px; border-radius: 8px; border: 1px solid #e0dfd8; font-family: monospace; }
    .public-label { color: #1d9e75; font-weight: 500; }
    .search-wrap { display: flex; align-items: center; gap: 8px; background: #f5f5f3; border: 1px solid #e0dfd8; border-radius: 8px; padding: 0 12px; height: 34px; max-width: 300px; }
    .search-wrap input { border: none; background: transparent; outline: none; font-size: 13px; width: 100%; }
    .table-card { background: #fff; border: 1px solid #e0dfd8; border-radius: 12px; overflow: hidden; }
    .table-header { display: grid; grid-template-columns: 2fr 1fr 1.5fr 1fr; padding: 10px 16px; background: #f5f5f3; border-bottom: 1px solid #e0dfd8; font-size: 11px; font-weight: 500; color: #9c9a92; text-transform: uppercase; letter-spacing: 0.06em; }
    .proj-row { display: grid; grid-template-columns: 2fr 1fr 1.5fr 1fr; padding: 12px 16px; border-bottom: 1px solid #e0dfd8; align-items: center; }
    .proj-row:last-child { border-bottom: none; }
    .proj-name { font-size: 13px; font-weight: 500; }
    .badge { display: inline-flex; padding: 3px 9px; border-radius: 99px; font-size: 11px; font-weight: 500; }
    .badge-ativo { background: #eaf3de; color: #3b6d11; }
    .badge-pausado { background: #faeeda; color: #854f0b; }
    .badge-concluido { background: #e6f1fb; color: #185fa5; }
    .badge-cancelado { background: #fcebeb; color: #a32d2d; }
    .tech-tags { display: flex; flex-wrap: wrap; gap: 4px; }
    .tech-tag { font-size: 10px; padding: 2px 7px; background: #f5f5f3; border: 1px solid #e0dfd8; border-radius: 4px; color: #73726c; }
    .date-col { font-size: 12px; color: #9c9a92; }
    .empty-state { text-align: center; padding: 40px; color: #9c9a92; font-size: 13px; }
  `]
})
export class PublicViewComponent implements OnInit {
  projects: Project[] = [];
  searchTerm = '';
  private search$ = new Subject<string>();

  constructor(
    private projectService: ProjectService,
    public t: TranslationService
  ) {}

  ngOnInit(): void {
    this.projectService.publicReadAll().subscribe(p => this.projects = p);
    this.search$.pipe(
      debounceTime(350),
      distinctUntilChanged(),
      switchMap(term => term
        ? this.projectService.publicReadByName(term)
        : this.projectService.publicReadAll()
      )
    ).subscribe(p => this.projects = p);
  }

  onSearch(term: string): void { this.search$.next(term); }

  techList(tech?: string): string[] {
    return tech ? tech.split(',').map(t => t.trim()).filter(Boolean) : [];
  }
}
