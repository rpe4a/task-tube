import './style.css';
import { Container } from '@mui/material';
import TaskTubePushForm from '../../../features/tasktube/TaskTubePushForm/TaskTubePushForm';
import TaskTubePushedHistory from '../../../features/tasktube/TaskTubePushedTasks/TaskTubePushedHistory';
import { memo, useCallback, useEffect, useState } from 'react';
import { PushedTask } from '../../../features/tasktube/TaskTubePushedTasks/storage/PushedTasksHistory';
import PushedTasksHistory from '../../../features/tasktube/TaskTubePushedTasks/storage/PushedTasksHistory';
import { useSearchParams } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { TaskTubeTaskResponse } from '../../../features/tasktube/TaskTubeTaskLayout/model/TaskTubeTaskResponse';
import api from '../../../shared/api';

const fetchTaskTubeAsync = async (
  correlationId: string | null,
  taskId: string | null,
): Promise<TaskTubeTaskResponse> => {
  if (!correlationId || !taskId) {
    throw new Error('Missing required parameters correlationId or taskId.');
  }

  const response = await api.get<TaskTubeTaskResponse>(
    `/api/v1/tasktube/${correlationId}/task/${taskId}`,
  );

  return response.data;
};

function TaskTubePushPage() {
  const [searchParams] = useSearchParams();
  const correlationId = searchParams.get('correlationId');
  const taskId = searchParams.get('taskId');

  const [task, setTask] = useState<TaskTubeTaskResponse | null>(null);
  const [tasks, setTasks] = useState<PushedTask[]>(PushedTasksHistory.getTasks());

  const { isFetching, isError, data, error } = useQuery({
    queryKey: ['push_task', correlationId, taskId],
    queryFn: () => fetchTaskTubeAsync(correlationId, taskId),
    enabled: !!correlationId && !!taskId,
  });

  useEffect(() => {
    if (isError) {
      console.error('Error fetching task:', error);
    } else if (data) {
      setTask(data);
    }
  }, [isError, data, error]);

  const addTask = useCallback((task: PushedTask) => {
    PushedTasksHistory.addTask(task);
    setTasks([...PushedTasksHistory.getTasks()]);
  }, []);

  const removeTask = useCallback((task: PushedTask) => {
    PushedTasksHistory.removeTask(task);
    setTasks([...PushedTasksHistory.getTasks()]);
  }, []);

  return (
    <Container maxWidth="xl" sx={{ py: 1 }}>
      <div className="push-task-container">
        <div>
          <TaskTubePushForm isFetching={isFetching} task={task} addTask={addTask} />
        </div>
        <div className="pushed-tasks-wrapper">
          <div className="pushed-tasks-content">
            <TaskTubePushedHistory tasks={tasks} removeTask={removeTask} />
          </div>
        </div>
      </div>
    </Container>
  );
}

export default memo(TaskTubePushPage);
