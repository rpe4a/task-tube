import { Container } from '@mui/material';
import TaskTubePushFormLayout from '../../features/tasktube/TaskTubePushFormLayout/TaskTubePushFormLayout';
import TaskTubePushedTasksLayout from '../../features/tasktube/TaskTubePushedTasksLayout/TaskTubePushedTasksLayout';
import './style.css';
import { useEffect, useState } from 'react';
import { addPushedTask, getPushedTasks, PushedTask, removePushedTask } from './storage/PushedTasks';
import { useSearchParams } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { TaskTubeTaskResponse } from '../../features/tasktube/TaskTubeTaskLayout/model/TaskTubeTaskResponse';
import api from '../../shared/api';

const fetchTaskTubeTask = async (
  correlationId: string | null,
  taskId: string | null,
): Promise<TaskTubeTaskResponse> => {
  const response = await api.get<TaskTubeTaskResponse>(
    `/api/v1/tasktube/${correlationId}/task/${taskId}`,
  );
  return response.data;
};

function TaskTubePushPage() {
  const [searchParams] = useSearchParams();
  const correlationId = searchParams.get('correlationId');
  const taskId = searchParams.get('taskId');

  const [tasks, setTasks] = useState<PushedTask[]>(getPushedTasks());
  const [task, setTask] = useState<TaskTubeTaskResponse | null>(null);
  const [loading, setLoading] = useState(false);

  const { isFetching, isError, data, error } = useQuery({
    queryKey: ['push_task', correlationId, taskId],
    queryFn: () => fetchTaskTubeTask(correlationId, taskId),
    refetchOnWindowFocus: false,
    refetchIntervalInBackground: false,
    enabled: !!correlationId && !!taskId,
  });

  useEffect(() => {
    handleResponse(isFetching, isError, data, error);
  }, [isError, isFetching, data, error]);

  const handleResponse = (
    isFetching: boolean,
    isError: boolean,
    data: TaskTubeTaskResponse | undefined,
    error: Error | null,
  ) => {
    if (isFetching) {
      setLoading(true);
    } else if (isError) {
      console.error('Error fetching task:', error);
      setLoading(false);
    } else if (data) {
      setTask(data);
      setLoading(false);
    }
  };

  const addTask = (task: PushedTask) => {
    addPushedTask(task);
    setTasks([...getPushedTasks()]);
  };

  const removeTask = (task: PushedTask) => {
    removePushedTask(task);
    setTasks([...getPushedTasks()]);
  };

  return (
    <Container maxWidth="xl" sx={{ py: 1 }}>
      <div className="push-task-container">
        <div>
          <TaskTubePushFormLayout loading={loading} task={task} addTask={addTask} />
        </div>
        <div className="pushed-tasks-wrapper">
          <div className="pushed-tasks-content">
            <TaskTubePushedTasksLayout tasks={tasks} removeTask={removeTask} />
          </div>
        </div>
      </div>
    </Container>
  );
}

export default TaskTubePushPage;
