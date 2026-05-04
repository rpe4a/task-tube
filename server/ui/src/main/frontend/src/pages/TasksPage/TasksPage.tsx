import { Container } from '@mui/material';
import React, { useCallback, useEffect, useState } from 'react';
import TasksFormLayout from '../../features/tasks/TaskFormLayout/TasksFormLayout';
import TasksPageTaskDto from './models/TasksPageTaskDto';
import TaskTableLayout from '../../features/tasks/TasksTableLayout/TasksTableLayout';
import { Dayjs } from 'dayjs';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import TasksPageResponse from './models/TasksPageResponse';
import { useSearchParams } from 'react-router';
import api from '../../shared/api';
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
  sort: string;
  by: string;
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
    sort,
    by,
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
  if (sort) searchParams.append('sort', sort);
  if (by) searchParams.append('by', by);

  const response = await api.get<TasksPageResponse>(`/api/v1/tasks?${searchParams.toString()}`);
  return response.data;
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
  const queryparameterSort = searchParams.get('sort') || 'created_at';
  const queryparameterBy = searchParams.get('by') || 'desc';

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
  const [sort, setSort] = useState<string>(queryparameterSort);
  const [by, setBy] = useState<'asc' | 'desc'>((queryparameterBy as 'asc' | 'desc') || 'desc');

  const { isPending, isFetching, isError, data, error, refetch } = useQuery({
    queryKey: [
      'tasks',
      searchId,
      searchName,
      searchTube,
      searchStatus,
      createdFrom ? createdFrom.toISOString() : '',
      createdTo ? createdTo.toISOString() : '',
      page,
      rowsPerPage,
      sort,
      by,
    ],
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
        sort,
        by,
      }),
    placeholderData: keepPreviousData,
  });

  const setSearchParamsToUrlObj = useCallback(
    (params: { [key: string]: string }) => {
      setSearchParams((prev) => {
        const searchParams = new URLSearchParams(prev);

        for (const [key, value] of Object.entries(params)) {
          if (value) {
            searchParams.set(key, value);
          } else {
            searchParams.delete(key);
          }
        }
        return searchParams;
      });
    },
    [setSearchParams],
  );

  const setSearchParamsToUrl = useCallback(
    (key: string, value: string) => {
      return setSearchParamsToUrlObj({ [key]: value });
    },
    [setSearchParamsToUrlObj],
  );

  const handleCreatedFromChange = useCallback(
    (value: Dayjs | null) => {
      setCreatedFrom(value ? dayjs.utc(value) : null);
      setPage(0);

      setSearchParamsToUrlObj({ from: value ? value.toISOString() : '', page: '0' });
    },
    [setSearchParamsToUrlObj],
  );

  const handleCreatedToChange = useCallback(
    (value: Dayjs | null) => {
      setCreatedTo(value ? dayjs.utc(value) : null);
      setPage(0);

      setSearchParamsToUrlObj({ to: value ? value.toISOString() : '', page: '0' });
    },
    [setSearchParamsToUrlObj],
  );

  const handleSearchIdChange = useCallback(
    (value: string) => {
      setSearchId(value);
      setPage(0);

      setSearchParamsToUrlObj({ id: value, page: '0' });
    },
    [setSearchParamsToUrlObj],
  );

  const handleSearchNameChange = useCallback(
    (value: string) => {
      setSearchName(value);
      setPage(0);

      setSearchParamsToUrlObj({ name: value, page: '0' });
    },
    [setSearchParamsToUrlObj],
  );

  const handleSearchTubeChange = useCallback(
    (value: string) => {
      setSearchTube(value);
      setPage(0);

      setSearchParamsToUrlObj({ tube: value, page: '0' });
    },
    [setSearchParamsToUrlObj],
  );

  const handleSearchStatusChange = useCallback(
    (value: string) => {
      setSearchStatus(value);
      setPage(0);

      setSearchParamsToUrlObj({ status: value, page: '0' });
    },
    [setSearchParamsToUrlObj],
  );

  const handleChangeRowsPerPage = useCallback(
    (value: string) => {
      setRowsPerPage(parseInt(value, 10));
      setPage(0);

      setSearchParamsToUrlObj({ size: value, page: '0' });
    },
    [setSearchParamsToUrlObj],
  );

  const handleChangePage = useCallback(
    (value: number) => {
      setPage(value);
      setSearchParamsToUrl('page', value.toString());
    },
    [setSearchParamsToUrl],
  );

  const handleSortChange = useCallback(
    (sort: string, by: 'asc' | 'desc') => {
      setSort(sort);
      setBy(by);

      setSearchParamsToUrlObj({ sort: sort, by: by });
    },
    [setSearchParamsToUrlObj],
  );

  const handleFetchTasks = useCallback(
    (event: React.MouseEvent<HTMLButtonElement>) => {
      event.preventDefault();
      refetch();
    },
    [refetch],
  );

  useEffect(() => {
    if (isPending) {
      setLoading(true);
    } else if (isError) {
      console.error('Error fetching tasks:', error);
      setLoading(false);
      handleChangePage(0);
    } else if (data) {
      setTasks(data.tasks);
      setTotalCount(data.totalCount);
      setLoading(false);
    }
  }, [isPending, isError, data, error, handleChangePage]);

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
        isError={isError}
        tasks={tasks}
        page={page}
        rowsPerPage={rowsPerPage}
        totalCount={totalCount}
        onChangePage={handleChangePage}
        onChangeRowsPerPage={handleChangeRowsPerPage}
        sort={sort}
        by={by}
        onSortChange={handleSortChange}
      />
    </Container>
  );
}

export default TasksPage;
