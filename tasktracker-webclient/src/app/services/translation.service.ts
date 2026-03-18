import { Injectable } from '@angular/core';

export const PROJECT_STATUS_LABELS: Record<string, string> = {
  ACTIVE:    'Ativo',
  PAUSED:    'Pausado',
  COMPLETED: 'Concluído',
  CANCELLED: 'Cancelado',
};

export const TASK_STATUS_LABELS: Record<string, string> = {
  PENDING:     'Pendente',
  IN_PROGRESS: 'Em Progresso',
  COMPLETED:   'Concluída',
  CANCELLED:   'Cancelada',
};

export const PROJECT_STATUS_VALUES: Record<string, string> = {
  Ativo:     'ACTIVE',
  Pausado:   'PAUSED',
  Concluído: 'COMPLETED',
  Cancelado: 'CANCELLED',
};

export const TASK_STATUS_VALUES: Record<string, string> = {
  Pendente:      'PENDING',
  'Em Progresso': 'IN_PROGRESS',
  Concluída:     'COMPLETED',
  Cancelada:     'CANCELLED',
};

export const PROJECT_STATUS_BADGE: Record<string, string> = {
  ACTIVE:    'badge-ativo',
  PAUSED:    'badge-pausado',
  COMPLETED: 'badge-concluido',
  CANCELLED: 'badge-cancelado',
};

export const TASK_STATUS_BADGE: Record<string, string> = {
  PENDING:     'badge-pendente',
  IN_PROGRESS: 'badge-progresso',
  COMPLETED:   'badge-concluido',
  CANCELLED:   'badge-cancelado',
};

@Injectable({ providedIn: 'root' })
export class TranslationService {

  projectStatusLabel(status: string): string {
    return PROJECT_STATUS_LABELS[status] ?? status;
  }

  taskStatusLabel(status: string): string {
    return TASK_STATUS_LABELS[status] ?? status;
  }

  projectStatusBadge(status: string): string {
    return PROJECT_STATUS_BADGE[status] ?? '';
  }

  taskStatusBadge(status: string): string {
    return TASK_STATUS_BADGE[status] ?? '';
  }

  projectStatusOptions(): { value: string; label: string }[] {
    return Object.entries(PROJECT_STATUS_LABELS).map(([value, label]) => ({ value, label }));
  }

  taskStatusOptions(): { value: string; label: string }[] {
    return Object.entries(TASK_STATUS_LABELS).map(([value, label]) => ({ value, label }));
  }
}
