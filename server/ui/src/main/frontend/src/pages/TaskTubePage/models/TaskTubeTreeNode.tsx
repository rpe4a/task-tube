import { TaskStatus } from '../../../shared/models/TaskStatus';

interface TaskTubeTreeNode {
  id: string;
  name: string;
  parentId: string;
  status: TaskStatus;
  createdAt: string;
  scheduledAt: string | null;
  startedAt: string | null;
  finishedAt: string | null;
  abortedAt: string | null;
  canceledAt: string | null;
  completedAt: string | null;
  childrenCount: number;
}

export default TaskTubeTreeNode;
