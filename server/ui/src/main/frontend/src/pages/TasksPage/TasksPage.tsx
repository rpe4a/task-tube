import { Container, SelectChangeEvent } from '@mui/material';
import React, { useState } from 'react';
import TasksFormLayout from '../../features/tasks/components/TaskFormLayout/TasksFormLayout';
import TaskPageDto from '../../features/tasks/models/TaskPageDto';
import TaskTableLayout from '../../features/tasks/components/TasksTableLayout/TasksTableLayout';
import dayjs, { Dayjs } from 'dayjs';

// Mock API functions
const generateMockTasks = (): TaskPageDto[] => {
  const tubes = ['reporting', 'processing', 'notifications', 'analytics', 'email'];
  const statuses: Array<'PENDING' | 'PROCESSING' | 'COMPLETED' | 'ABORTED'> = [
    'PENDING',
    'PROCESSING',
    'COMPLETED',
    'ABORTED',
  ];
  const workers = ['worker-1', 'worker-2', 'worker-3', 'worker-4', 'unassigned'];
  const taskNames = [
    'Generate Report',
    'Export Data',
    'Process Payment',
    'Send Email',
    'Analyze Metrics',
    'Update Cache',
    'Sync Database',
    'Generate Invoice',
    'Send Notification',
    'Cleanup Old Files',
  ];

  const tasks: TaskPageDto[] = [];
  for (let i = 1; i <= 28; i++) {
    const tube = tubes[Math.floor(Math.random() * tubes.length)];
    const status = statuses[Math.floor(Math.random() * statuses.length)];
    const worker = workers[Math.floor(Math.random() * workers.length)];
    const taskName = taskNames[Math.floor(Math.random() * taskNames.length)];
    const createdDate = dayjs().subtract(Math.random() * 30, 'days');
    const updatedDate =
      status === 'PENDING' ? createdDate : createdDate.add(Math.random() * 120, 'minutes');
    const completedDate =
      status === 'COMPLETED' ? updatedDate.add(Math.random() * 60, 'minutes') : null;
    const abortedDate =
      status === 'ABORTED' ? updatedDate.add(Math.random() * 60, 'minutes') : null;

    tasks.push({
      id: `${tube}-${i.toString().padStart(3, '0')}-${Math.random().toString(36).substr(2, 9)}`,
      name: `${taskName} #${i}`,
      tube,
      status,
      createdAt: createdDate.format(),
      updatedAt: updatedDate.format(),
      completedAt: completedDate?.format() || null,
      abortedAt: abortedDate?.format() || null,
      handledBy: worker,
    });
  }
  return tasks;
};

const mockFetchTasksByTube = async (tube: string): Promise<TaskPageDto[]> => {
  await new Promise((resolve) => setTimeout(resolve, 500));
  const allTasks = generateMockTasks();
  return allTasks.filter((t) => t.tube === tube);
};

const mockFetchAllTasks = async (): Promise<TaskPageDto[]> => {
  await new Promise((resolve) => setTimeout(resolve, 500));
  return generateMockTasks();
};

function Tasks(): React.JSX.Element {
  const [tasks, setTasks] = useState<TaskPageDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [customTube, setCustomTube] = useState<string>('');
  const [createdAt, setCreatedAt] = useState<Dayjs | null>(null);
  const [completedAt, setCompletedAt] = useState<Dayjs | null>(null);
  const [searchId, setSearchId] = useState<string>('');
  const [searchName, setSearchName] = useState<string>('');
  const [searchTube, setSearchTube] = useState<string>('');
  const [searchStatus, setSearchStatus] = useState<string>('');

  const handleCustomTubeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setCustomTube(event.target.value);
  };

  const handleSearchIdChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchId(event.target.value);
  };

  const handleSearchNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchName(event.target.value);
  };

  const handleSearchTubeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTube(event.target.value);
  };

  const handleSearchStatusChange = (event: SelectChangeEvent<string>) => {
    setSearchStatus(event.target.value as string);
  };

  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleFetchTasks = async (
    createdAtValue?: Dayjs | null,
    completedAtValue?: Dayjs | null,
  ) => {
    setLoading(true);
    setPage(0);
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

      if (searchId) {
        fetchedTasks = fetchedTasks.filter((t) =>
          t.id.toLowerCase().includes(searchId.toLowerCase()),
        );
      }

      if (searchName) {
        fetchedTasks = fetchedTasks.filter((t) =>
          t.name.toLowerCase().includes(searchName.toLowerCase()),
        );
      }

      if (searchTube) {
        fetchedTasks = fetchedTasks.filter((t) =>
          t.tube.toLowerCase().includes(searchTube.toLowerCase()),
        );
      }

      if (searchStatus) {
        fetchedTasks = fetchedTasks.filter((t) => t.status === searchStatus);
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
    setPage(0);
    setCustomTube('');
    setCreatedAt(null);
    setCompletedAt(null);
    setSearchId('');
    setSearchName('');
    setSearchTube('');
    setSearchStatus('');
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
    <Container maxWidth="xl" sx={{ py: 3 }}>
      <TasksFormLayout
        customTube={customTube}
        createdAt={createdAt}
        completedAt={completedAt}
        searchId={searchId}
        searchName={searchName}
        searchTube={searchTube}
        searchStatus={searchStatus}
        loading={loading}
        handleCustomTubeChange={handleCustomTubeChange}
        handleCreatedAtChange={setCreatedAt}
        handleCompletedAtChange={setCompletedAt}
        handleSearchIdChange={handleSearchIdChange}
        handleSearchNameChange={handleSearchNameChange}
        handleSearchTubeChange={handleSearchTubeChange}
        handleSearchStatusChange={handleSearchStatusChange}
        handleFetchTasks={handleFetchTasks}
        handleFetchAll={handleFetchAll}
      />
      <TaskTableLayout
        loading={loading}
        tasks={tasks}
        page={page}
        rowsPerPage={rowsPerPage}
        onChangePage={handleChangePage}
        onChangeRowsPerPage={handleChangeRowsPerPage}
      />
    </Container>
  );
}

export default Tasks;
