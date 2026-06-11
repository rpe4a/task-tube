import { TaskStatus } from '../models/TaskStatus';

export const getStatusColor = (
  status: TaskStatus,
): 'default' | 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success' => {
  const statusColorMap: Record<
    TaskStatus,
    'default' | 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success'
  > = {
    CREATED: 'primary',
    SCHEDULED: 'default',
    PROCESSING: 'warning',
    FINISHED: 'primary',
    COMPLETED: 'success',
    ABORTED: 'error',
    TERMINATED: 'error',
  };
  return statusColorMap[status];
};
