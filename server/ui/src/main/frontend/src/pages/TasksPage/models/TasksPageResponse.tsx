import TasksPageDto from './TasksPageDto';

interface TasksPageResponse {
  tasks: TasksPageDto[];
  totalCount: number;
}

export default TasksPageResponse;
