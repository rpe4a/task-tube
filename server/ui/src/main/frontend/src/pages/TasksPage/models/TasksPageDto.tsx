import { TaskStatus } from '../../../shared/models/TaskStatus';

interface TasksPageDto {
  id: string;
  name: string;
  tube: string;
  correlationId: string;
  status: TaskStatus;
  createdAt: string;
  updatedAt: string;
  abortedAt: string | null;
  canceledAt: string | null;
  completedAt: string | null;
  handledBy: string;
}

export default TasksPageDto;
