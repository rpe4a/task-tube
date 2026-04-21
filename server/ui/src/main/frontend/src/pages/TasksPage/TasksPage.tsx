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
import { useSearchParams } from 'react-router';
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
  const [searchParams, setSearchParams] = useSearchParams();
  const queryparameterPage = searchParams.get('page')
    ? parseInt(searchParams.get('page') as string, 10)
    : 0;
  const queryparameterSize = searchParams.get('size')
    ? parseInt(searchParams.get('size') as string, 10)
    : 10;
  const queryparameterCreatedFrom = searchParams.get('from')
    ? dayjs.utc(searchParams.get('from') as string)
    : null;
  const queryparameterCreatedTo = searchParams.get('to')
    ? dayjs.utc(searchParams.get('to') as string)
    : null;
  const queryparameterId = searchParams.get('id') || '';
  const queryparameterName = searchParams.get('name') || '';
  const queryparameterTube = searchParams.get('tube') || '';
  const queryparameterStatus = searchParams.get('status') || '';

  const [tasks, setTasks] = useState<TasksPageTaskDto[]>([]);
  const [totalCount, setTotalCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState<number>(queryparameterPage);
  const [rowsPerPage, setRowsPerPage] = useState<number>(queryparameterSize);
  const [createdFrom, setCreatedFrom] = useState<Dayjs | null>(queryparameterCreatedFrom);
  const [createdTo, setCreatedTo] = useState<Dayjs | null>(queryparameterCreatedTo);
  const [searchId, setSearchId] = useState<string>(queryparameterId);
  const [searchName, setSearchName] = useState<string>(queryparameterName);
  const [searchTube, setSearchTube] = useState<string>(queryparameterTube);
  const [searchStatus, setSearchStatus] = useState<string>(queryparameterStatus);

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

  const setSearchParamsToUrl = (key: string, value: string) => {
    return setSearchParamsToUrlObj({ [key]: value });
  };

  const setSearchParamsToUrlObj = (params: { [key: string]: string }) => {
    setSearchParams((searchParams) => {
      for (const [key, value] of Object.entries(params)) {
        if (value) {
          searchParams.set(key, value);
        } else {
          searchParams.delete(key);
        }
      }
      return searchParams;
    });
  };

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
    setCreatedFrom(value ? dayjs.utc(value) : null);
    setSearchParamsToUrl('from', value ? value.toISOString() : '');
  };

  const handleCreatedToChange = (value: Dayjs | null) => {
    setCreatedTo(value ? dayjs.utc(value) : null);
    setSearchParamsToUrl('to', value ? value.toISOString() : '');
  };

  const handleSearchIdChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    event.preventDefault();

    setSearchId(event.target.value);
    setSearchParamsToUrl('id', event.target.value);
  };

  const handleSearchNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    event.preventDefault();

    setSearchName(event.target.value);
    setSearchParamsToUrl('name', event.target.value);
  };

  const handleSearchTubeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    event.preventDefault();

    setSearchTube(event.target.value);
    setSearchParamsToUrl('tube', event.target.value);
  };

  const handleSearchStatusChange = (event: SelectChangeEvent<string>) => {
    event.preventDefault();

    setSearchStatus(event.target.value as string);
    setSearchParamsToUrl('status', event.target.value as string);
  };

  const handleChangePage = (event: unknown, newPage: number) => {
    if (!isPlaceholderData) {
      setPage(newPage);
      setSearchParamsToUrl('page', newPage.toString());
    }
  };

  const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
    event.preventDefault();

    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);

    setSearchParamsToUrlObj({ size: event.target.value, page: '0' });
  };

  const handleFetchTasks = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
    refetch();
  };

  return (
    <Container maxWidth="xl" sx={{ py: 1 }}>
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
