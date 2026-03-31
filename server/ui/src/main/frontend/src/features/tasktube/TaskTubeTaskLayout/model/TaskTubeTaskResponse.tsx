import { TaskStatus } from '../../../../shared/models/TaskStatus';

export interface TaskSettings {
  maxFailures: number;
  failureRetryTimeoutSeconds: number;
  timeoutSeconds: number;
  heartbeatTimeoutSeconds: number;
}

export interface TaskTubeTaskResponse {
  id: string;
  name: string;
  tube: string;
  status: TaskStatus;
  correlationId: string;

  input: {}[];

  output: {} | null;

  updatedAt: string;
  createdAt: string;
  scheduledAt: string | null;
  startedAt: string | null;
  heartbeatAt: string | null;
  finishedAt: string | null;
  failedAt: string | null;
  abortedAt: string | null;
  canceledAt: string | null;
  completedAt: string | null;

  failures: number;
  failureReason: string | null;
  settings: TaskSettings;
  handledBy: string;
  countChildren: number;
}
