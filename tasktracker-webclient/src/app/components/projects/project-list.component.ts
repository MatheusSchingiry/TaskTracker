import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject, switchMap } from 'rxjs';
import { ProjectService } from '../../services/project.service';
import { TaskService } from '../../services/task.service';
import { TranslationService } from '../../services/translation.service';
import { Project, ProjectRequest } from '../../models/project.model';
import { Task, TaskRequest } from '../../models/task.model';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.scss']
})
export class ProjectListComponent implements OnInit {
  projects: Project[] = [];
  filteredProjects: Project[] = [];
  projectTasks: Record<number, Task[]> = {};
  expandedProjects = new Set<number>();

  activeStatusFilter: string = 'ALL';
  searchTerm = '';
  private searchSubject = new Subject<string>();

  showProjectModal = false;
  showTaskModal = false;
  editingProject: Project | null = null;
  editingTask: Task | null = null;
  selectedProjectId: number | null = null;
  selectedProjectName = '';

  projectForm: FormGroup;
  taskForm: FormGroup;

  projectStatusOptions: { value: string; label: string }[] = [];
  taskStatusOptions:    { value: string; label: string }[] = [];

  constructor(
    private projectService: ProjectService,
    private taskService: TaskService,
    private fb: FormBuilder,
    public t: TranslationService
  ) {
    this.projectStatusOptions = this.t.projectStatusOptions();
    this.taskStatusOptions    = this.t.taskStatusOptions();

    this.projectForm = this.fb.group({
      name:        ['', [Validators.required, Validators.maxLength(150)]],
      description: [''],
      technology:  [''],
      status:      ['ACTIVE', Validators.required]
    });

    this.taskForm = this.fb.group({
      title:       ['', [Validators.required, Validators.maxLength(200)]],
      description: [''],
      status:      ['PENDING', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadProjects();

    this.searchSubject.pipe(
      debounceTime(350),
      distinctUntilChanged(),
      switchMap(term => term.trim()
        ? this.projectService.readByName(term)
        : this.projectService.readAll()
      )
    ).subscribe(projects => {
      this.projects = projects;
      this.applyStatusFilter();
    });
  }

  loadProjects(): void {
    this.projectService.readAll().subscribe(projects => {
      this.projects = projects;
      this.applyStatusFilter();
    });
  }

  onSearch(term: string): void {
    this.searchTerm = term;
    this.searchSubject.next(term);
  }

  setStatusFilter(status: string): void {
    this.activeStatusFilter = status;
    this.applyStatusFilter();
  }

  applyStatusFilter(): void {
    this.filteredProjects = this.activeStatusFilter === 'ALL'
      ? [...this.projects]
      : this.projects.filter(p => p.status === this.activeStatusFilter);
  }

  toggleExpand(projectId: number): void {
    if (this.expandedProjects.has(projectId)) {
      this.expandedProjects.delete(projectId);
    } else {
      this.expandedProjects.add(projectId);
      if (!this.projectTasks[projectId]) {
        this.loadTasks(projectId);
      }
    }
  }

  loadTasks(projectId: number): void {
    this.taskService.readAll(projectId).subscribe(tasks => {
      this.projectTasks[projectId] = tasks;
    });
  }

  isExpanded(projectId: number): boolean {
    return this.expandedProjects.has(projectId);
  }

  openCreateModal(): void {
    this.editingProject = null;
    this.projectForm.reset({ status: 'ACTIVE' });
    this.showProjectModal = true;
  }

  openEditModal(project: Project): void {
    this.editingProject = project;
    this.projectForm.patchValue({
      name:        project.name,
      description: project.description,
      technology:  project.technology,
      status:      project.status
    });
    this.showProjectModal = true;
  }

  saveProject(): void {
    if (this.projectForm.invalid) return;
    const payload: ProjectRequest = this.projectForm.value;

    const request = this.editingProject?.id
      ? this.projectService.update(this.editingProject.id, payload)
      : this.projectService.create(payload);

    request.subscribe(() => {
      this.showProjectModal = false;
      this.loadProjects();
    });
  }

  deleteProject(id: number): void {
    if (!confirm('Remover este projeto? Todas as tasks serão excluídas.')) return;
    this.projectService.delete(id).subscribe(() => this.loadProjects());
  }

  openAddTaskModal(project: Project): void {
    this.editingTask          = null;
    this.selectedProjectId    = project.id!;
    this.selectedProjectName  = project.name;
    this.taskForm.reset({ status: 'PENDING' });
    this.showTaskModal = true;
  }

  openEditTaskModal(projectId: number, projectName: string, task: Task): void {
    this.editingTask          = task;
    this.selectedProjectId    = projectId;
    this.selectedProjectName  = projectName;
    this.taskForm.patchValue({
      title:       task.title,
      description: task.description,
      status:      task.status
    });
    this.showTaskModal = true;
  }

  saveTask(): void {
    if (this.taskForm.invalid || !this.selectedProjectId) return;
    const payload: TaskRequest = this.taskForm.value;

    const request = this.editingTask?.id
      ? this.taskService.update(this.selectedProjectId, this.editingTask.id!, payload)
      : this.taskService.create(this.selectedProjectId, payload);

    request.subscribe(() => {
      this.showTaskModal = false;
      this.loadTasks(this.selectedProjectId!);
    });
  }

  deleteTask(projectId: number, taskId: number): void {
    if (!confirm('Remover esta task?')) return;
    this.taskService.delete(projectId, taskId).subscribe(() => {
      this.loadTasks(projectId);
    });
  }

  badgeClass(status: string): string {
    return this.t.projectStatusBadge(status) || this.t.taskStatusBadge(status);
  }

  statusLabel(status: string): string {
    return this.t.projectStatusLabel(status) || this.t.taskStatusLabel(status);
  }

  techList(tech?: string): string[] {
    return tech ? tech.split(',').map(t => t.trim()).filter(Boolean) : [];
  }
}
