import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProjectService } from '../../services/project.service';
import { TaskService } from '../../services/task.service';
import { TranslationService } from '../../services/translation.service';
import { Task, TaskRequest } from '../../models/task.model';
import { forkJoin, of, switchMap } from 'rxjs';

interface TaskWithProject extends Task {
  projectName: string;
  projectId: number;
}

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss']
})
export class TaskListComponent implements OnInit {
  tasks: TaskWithProject[] = [];
  loading = true;

  showTaskModal = false;
  editingTask: TaskWithProject | null = null;
  taskForm: FormGroup;
  taskStatusOptions: { value: string; label: string }[] = [];

  constructor(
    private projectService: ProjectService,
    private taskService: TaskService,
    private fb: FormBuilder,
    public t: TranslationService
  ) {
    this.taskStatusOptions = this.t.taskStatusOptions();
    this.taskForm = this.fb.group({
      title:       ['', [Validators.required, Validators.maxLength(200)]],
      description: [''],
      status:      ['PENDING', Validators.required]
    });
  }

  ngOnInit(): void { this.loadAllTasks(); }

  loadAllTasks(): void {
    this.loading = true;
    this.projectService.readAll().pipe(
      switchMap(projects => {
        if (!projects.length) return of([]);
        return forkJoin(
          projects.map(p =>
            this.taskService.readAll(p.id!).pipe(
              switchMap(tasks => of(
                tasks.map(task => ({ ...task, projectName: p.name, projectId: p.id! }))
              ))
            )
          )
        );
      })
    ).subscribe(results => {
      this.tasks = (results as TaskWithProject[][]).flat();
      this.loading = false;
    });
  }

  openEditTaskModal(task: TaskWithProject): void {
    this.editingTask = task;
    this.taskForm.patchValue({
      title:       task.title,
      description: task.description,
      status:      task.status
    });
    this.showTaskModal = true;
  }

  saveTask(): void {
    if (this.taskForm.invalid || !this.editingTask) return;
    const payload: TaskRequest = this.taskForm.value;
    this.taskService.update(this.editingTask.projectId, this.editingTask.id!, payload)
      .subscribe(() => {
        this.showTaskModal = false;
        this.loadAllTasks();
      });
  }

  deleteTask(task: TaskWithProject): void {
    if (!confirm('Remover esta task?')) return;
    this.taskService.delete(task.projectId, task.id!).subscribe(() => this.loadAllTasks());
  }

  badgeClass(status: string): string  { return this.t.taskStatusBadge(status); }
  statusLabel(status: string): string { return this.t.taskStatusLabel(status); }
}
