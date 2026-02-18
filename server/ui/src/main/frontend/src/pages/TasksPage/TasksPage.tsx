import { Container } from '@mui/material';
import React, { useState } from 'react';
import TasksFormLayout from '../../features/tasks/components/TaskFormLayout/TasksFormLayout';
import TaskPageDto from '../../features/tasks/models/TaskPageDto';
import TaskTableLayout from '../../features/tasks/components/TasksTableLayout/TasksTableLayout';
import dayjs, { Dayjs } from 'dayjs';

// Mock API functions
const mockFetchTasksByTube = async (tube: string): Promise<TaskPageDto[]> => {
  await new Promise((resolve) => setTimeout(resolve, 500));

  const mockTasks: Record<string, TaskPageDto[]> = {
    reporting: [
      {
        id: 'c7a1b6f2-6f4e-4a8c-9a2f-0a3a5b1d9c11',
        name: 'Generate Report',
        tube: 'reporting',
        status: 'PROCESSING',
        createdAt: '2026-02-17T12:00:00Z',
        updatedAt: '2026-02-17T12:05:10Z',
        abortedAt: null,
        completedAt: null,
        handledBy: 'worker-3',
      },
      {
        id: 'a2b3c4d5-e6f7-4a8c-9a2f-0a3a5b1d9c22',
        name: 'Export Data',
        tube: 'reporting',
        status: 'COMPLETED',
        createdAt: '2026-02-16T10:00:00Z',
        updatedAt: '2026-02-16T10:15:30Z',
        abortedAt: null,
        completedAt: '2026-02-16T10:15:30Z',
        handledBy: 'worker-1',
      },
    ],
    processing: [
      {
        id: 'f1e2d3c4-b5a6-4a8c-9a2f-0a3a5b1d9c33',
        name: 'Process Payment',
        tube: 'processing',
        status: 'PENDING',
        createdAt: '2026-02-17T13:00:00Z',
        updatedAt: '2026-02-17T13:00:00Z',
        abortedAt: null,
        completedAt: null,
        handledBy: 'unassigned',
      },
    ],
    notifications: [
      {
        id: 'n8o7p6q5-r4s3-4a8c-9a2f-0a3a5b1d9c44',
        name: 'Send Email',
        tube: 'notifications',
        status: 'COMPLETED',
        createdAt: '2026-02-17T11:30:00Z',
        updatedAt: '2026-02-17T11:35:00Z',
        abortedAt: null,
        completedAt: '2026-02-17T11:35:00Z',
        handledBy: 'worker-2',
      },
    ],
  };

  return mockTasks[tube] || [];
};

const mockFetchAllTasks = async (): Promise<TaskPageDto[]> => {
  await new Promise((resolve) => setTimeout(resolve, 500));

  return [
    {
      id: 'c7a1b6f2-6f4e-4a8c-9a2f-0a3a5b1d9c11',
      name: 'Generate Report',
      tube: 'reporting',
      status: 'ABORTED',
      createdAt: '2026-02-17T12:00:00Z',
      updatedAt: '2026-02-17T12:05:10Z',
      abortedAt: '2026-02-17T12:15:10Z',
      completedAt: null,
      handledBy: 'worker-3',
    },
    {
      id: 'a2b3c4d5-e6f7-4a8c-9a2f-0a3a5b1d9c22',
      name: 'Export Data',
      tube: 'reporting',
      status: 'COMPLETED',
      createdAt: '2026-02-16T10:00:00Z',
      updatedAt: '2026-02-16T10:15:30Z',
      abortedAt: null,
      completedAt: '2026-02-16T10:15:30Z',
      handledBy: 'worker-1',
    },
    {
      id: 'f1e2d3c4-b5a6-4a8c-9a2f-0a3a5b1d9c33',
      name: 'Process Payment',
      tube: 'processing',
      status: 'PENDING',
      createdAt: '2026-02-17T13:00:00Z',
      updatedAt: '2026-02-17T13:00:00Z',
      abortedAt: null,
      completedAt: null,
      handledBy: 'unassigned',
    },
    {
      id: 'n8o7p6q5-r4s3-4a8c-9a2f-0a3a5b1d9c44',
      name: 'Send Email',
      tube: 'notifications',
      status: 'COMPLETED',
      createdAt: '2026-02-17T11:30:00Z',
      updatedAt: '2026-02-17T11:35:00Z',
      abortedAt: null,
      completedAt: '2026-02-17T11:35:00Z',
      handledBy: 'worker-2',
    },
  ];
};

function Tasks(): React.JSX.Element {
  const [tasks, setTasks] = useState<TaskPageDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [customTube, setCustomTube] = useState<string>('');
  const [createdAt, setCreatedAt] = useState<Dayjs | null>(null);
  const [completedAt, setCompletedAt] = useState<Dayjs | null>(null);

  const handleCustomTubeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setCustomTube(event.target.value);
  };

  const handleFetchTasks = async (
    createdAtValue?: Dayjs | null,
    completedAtValue?: Dayjs | null,
  ) => {
    setLoading(true);
    try {
      const tube = customTube;

      let fetchedTasks: TaskPageDto[] = [];
      if (tube) {
        fetchedTasks = await mockFetchTasksByTube(tube);
      } else {
        fetchedTasks = await mockFetchAllTasks();
      }

      if (createdAtValue) {
        fetchedTasks = fetchedTasks.filter(
          (t) => dayjs(t.createdAt).valueOf() >= createdAtValue.valueOf(),
        );
      }

      if (completedAtValue) {
        fetchedTasks = fetchedTasks.filter(
          (t) => t.completedAt && dayjs(t.completedAt).valueOf() >= completedAtValue.valueOf(),
        );
      }

      setTasks(fetchedTasks);
    } catch (error) {
      console.error('Error fetching tasks:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFetchAll = async () => {
    setLoading(true);
    setCustomTube('');
    setCreatedAt(null);
    setCompletedAt(null);
    try {
      const fetchedTasks = await mockFetchAllTasks();
      setTasks(fetchedTasks);
    } catch (error) {
      console.error('Error fetching tasks:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 3 }}>
      <TasksFormLayout
        customTube={customTube}
        createdAt={createdAt}
        completedAt={completedAt}
        loading={loading}
        handleCustomTubeChange={handleCustomTubeChange}
        handleCreatedAtChange={setCreatedAt}
        handleCompletedAtChange={setCompletedAt}
        handleFetchTasks={handleFetchTasks}
        handleFetchAll={handleFetchAll}
      />
      <TaskTableLayout loading={loading} tasks={tasks} />
    </Container>
  );
}

export default Tasks;
