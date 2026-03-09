import { TaskStatus } from '../../../shared/models/TaskStatus';

interface TasksTubePageDto {
  id: string;
  name: string;
  parentId: string;
  status: TaskStatus;
  createdAt: string;
  abortedAt: string | null;
  completedAt: string | null;
}

export default TasksTubePageDto;
