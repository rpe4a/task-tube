import { Container, SelectChangeEvent } from '@mui/material';
import React, { useEffect, useState } from 'react';
import TasksFormLayout from '../../features/tasks/TaskFormLayout/TasksFormLayout';
import TasksPageTaskDto from './models/TasksPageTaskDto';
import TaskTableLayout from '../../features/tasks/TasksTableLayout/TasksTableLayout';
import { Dayjs } from 'dayjs';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import TasksPageResponse from './models/TasksPageResponse';
dayjs.extend(utc);

interface FetchTasksParams {
  page: number;
  rowsPerPage: number;
  createdFrom: Dayjs | null;
  createdTo: Dayjs | null;
  searchId: string;
  searchName: string;
  searchTube: string;
  searchStatus: string;
}

const fetchTasks = async (params: FetchTasksParams): Promise<TasksPageResponse> => {
  const {
    page,
    rowsPerPage,
    createdFrom,
    createdTo,
    searchId,
    searchName,
    searchTube,
    searchStatus,
  } = params;

  let searchParams = new URLSearchParams();
  searchParams.append('page', page.toString());
  searchParams.append('size', rowsPerPage.toString());
  if (createdFrom) searchParams.append('createdFrom', createdFrom.toISOString());
  if (createdTo) searchParams.append('createdTo', createdTo.toISOString());
  if (searchId) searchParams.append('id', searchId);
  if (searchName) searchParams.append('name', searchName);
  if (searchTube) searchParams.append('tube', searchTube);
  if (searchStatus) searchParams.append('status', searchStatus);

  const response = await fetch(`/api/v1/tasks?${searchParams.toString()}`);
  return response.json();
};

function TasksPage(): React.JSX.Element {
  const [tasks, setTasks] = useState<TasksPageTaskDto[]>([]);
  const [totalCount, setTotalCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [createdFrom, setCreatedFrom] = useState<Dayjs | null>(null);
  const [createdTo, setCreatedTo] = useState<Dayjs | null>(null);
  const [searchId, setSearchId] = useState<string>('');
  const [searchName, setSearchName] = useState<string>('');
  const [searchTube, setSearchTube] = useState<string>('');
  const [searchStatus, setSearchStatus] = useState<string>('');

  const { isPending, isFetching, isError, data, error, isPlaceholderData, refetch } = useQuery({
    queryKey: ['tasks', page],
    queryFn: () =>
      fetchTasks({
        page,
        rowsPerPage,
        createdFrom,
        createdTo,
        searchId,
        searchName,
        searchTube,
        searchStatus,
      }),
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
  });

  useEffect(() => {
    handleResponse(isPending, isError, data, error);
  }, [isPending, isError, data, error]);

  const handleResponse = (
    isPending: boolean,
    isError: boolean,
    data: TasksPageResponse | undefined,
    error: Error | null,
  ) => {
    if (isPending) {
      setLoading(true);
    } else if (isError) {
      console.error('Error fetching tasks:', error);
      setLoading(false);
    } else if (data) {
      setTasks(data.tasks);
      setTotalCount(data.totalCount);
      setLoading(false);
    }
  };

  const handleCreatedFromChange = (value: Dayjs | null) => {
    setCreatedFrom(dayjs.utc(value));
  };

  const handleCreatedToChange = (value: Dayjs | null) => {
    setCreatedTo(dayjs.utc(value));
  };

  const handleSearchIdChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    event.preventDefault();
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
    if (!isPlaceholderData) {
      setPage(newPage);
    }
  };

  const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleFetchTasks = async () => {
    refetch();
  };

  return (
    <Container maxWidth="xl" sx={{ py: 3 }}>
      <TasksFormLayout
        createdFrom={createdFrom}
        createdTo={createdTo}
        searchId={searchId}
        searchName={searchName}
        searchTube={searchTube}
        searchStatus={searchStatus}
        loading={loading}
        handleCreatedFromChange={handleCreatedFromChange}
        handleCreatedToChange={handleCreatedToChange}
        handleSearchIdChange={handleSearchIdChange}
        handleSearchNameChange={handleSearchNameChange}
        handleSearchTubeChange={handleSearchTubeChange}
        handleSearchStatusChange={handleSearchStatusChange}
        handleSearchTasks={handleFetchTasks}
      />
      <TaskTableLayout
        loading={loading}
        isFetching={isFetching}
        tasks={tasks}
        page={page}
        rowsPerPage={rowsPerPage}
        totalCount={totalCount}
        onChangePage={handleChangePage}
        onChangeRowsPerPage={handleChangeRowsPerPage}
      />
    </Container>
  );
}

export default TasksPage;
