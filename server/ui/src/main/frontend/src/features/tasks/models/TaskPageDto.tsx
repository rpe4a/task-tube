interface TaskPageDto {
  id: string;
  name: string;
  tube: string;
  status:
    | 'CREATED'
    | 'SCHEDULED'
    | 'PROCESSING'
    | 'FINISHED'
    | 'COMPLETED'
    | 'ABORTED'
    | 'CANCELED';
  createdAt: string;
  updatedAt: string;
  abortedAt: string | null;
  completedAt: string | null;
  handledBy: string;
}

export default TaskPageDto;
