import { TaskTubeTaskLog } from './TaskTubeTaskLog';

interface TaskTubeTaskLogsResponse {
  logs: TaskTubeTaskLog[];
  totalCount: number;
}

export default TaskTubeTaskLogsResponse;
