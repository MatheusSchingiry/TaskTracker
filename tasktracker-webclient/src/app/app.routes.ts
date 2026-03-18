import { Routes } from '@angular/router';
import { ProjectListComponent } from './components/projects/project-list.component';
import { TaskListComponent } from './components/tasks/task-list.component';
import { PublicViewComponent } from './components/projects/public-view.component';

export const routes: Routes = [
  { path: '', redirectTo: 'projetos', pathMatch: 'full' },
  { path: 'projetos', component: ProjectListComponent },
  { path: 'tasks', component: TaskListComponent },
  { path: 'public', component: PublicViewComponent },
  { path: '**', redirectTo: 'projetos' }
];
