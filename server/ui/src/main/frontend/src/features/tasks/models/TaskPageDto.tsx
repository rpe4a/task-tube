interface TaskPageDto {
  id: string;
  name: string;
  tube: string;
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'ABORTED';
  createdAt: string;
  updatedAt: string;
  abortedAt: string | null;
  completedAt: string | null;
  handledBy: string;
}

export default TaskPageDto;
