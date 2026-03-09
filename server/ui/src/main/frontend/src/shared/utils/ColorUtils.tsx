import { TaskStatus } from '../models/TaskStatus';

export const getStatusColor = (
  status: TaskStatus,
): 'default' | 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success' => {
  const statusColorMap: Record<
    TaskStatus,
    'default' | 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success'
  > = {
    CREATED: 'default',
    SCHEDULED: 'primary',
    CANCELED: 'warning',
    PROCESSING: 'info',
    COMPLETED: 'success',
    ABORTED: 'error',
    FINISHED: 'secondary',
  };
  return statusColorMap[status];
};
