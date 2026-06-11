export type TaskStatus =
  | 'CREATED'
  | 'SCHEDULED'
  | 'PROCESSING'
  | 'FINISHED'
  | 'COMPLETED'
  | 'ABORTED'
  | 'TERMINATED';

export const isTaskTerminal = (task: { status: TaskStatus }): boolean => {
  return task.status === 'COMPLETED' || task.status === 'ABORTED' || task.status === 'TERMINATED';
};
