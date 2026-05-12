import TasksPageTaskDto from './TasksPageTaskDto';

interface TasksPageResponse {
  tasks: TasksPageTaskDto[];
  totalCount: number;
}

export default TasksPageResponse;
