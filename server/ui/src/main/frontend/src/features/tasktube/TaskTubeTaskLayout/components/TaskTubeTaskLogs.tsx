import {
  Box,
  CircularProgress,
  TableContainer,
  Paper,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Chip,
  Typography,
  TablePagination,
  Tooltip,
  ChipPropsColorOverrides,
} from '@mui/material';
import { JSX, useState, useEffect } from 'react';
import { LogLevel, TaskTubeTaskLog } from '../model/TaskTubeTaskLog';
import * as DateTimeUtils from '../../../../shared/utils/DateTimeUtils';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import { OverridableStringUnion } from '@mui/types';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import TaskTubeTaskLogsResponse from '../model/TaskTubeTaskLogsResponse';

dayjs.extend(utc);

interface TaskTubeTaskLogsProps {
  correlationId: string;
  taskId: string;
}

function getSeveretyColor(
  status: LogLevel,
): OverridableStringUnion<
  'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning',
  ChipPropsColorOverrides
> {
  const statusColorMap: Record<
    LogLevel,
    OverridableStringUnion<
      'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning',
      ChipPropsColorOverrides
    >
  > = {
    TRACE: 'default',
    DEBUG: 'default',
    INFO: 'info',
    WARN: 'warning',
    ERROR: 'error',
  };

  return statusColorMap[status];
}

interface FetchTaskLogsParams {
  correlationId: string;
  taskId: string;
  page: number;
  rowsPerPage: number;
}

const fetchTaskLogs = async (params: FetchTaskLogsParams): Promise<TaskTubeTaskLogsResponse> => {
  const { page, rowsPerPage, taskId, correlationId } = params;

  let searchParams = new URLSearchParams();
  searchParams.append('page', page.toString());
  searchParams.append('size', rowsPerPage.toString());

  const response = await fetch(
    `/api/v1/tasktube/${correlationId}/task/${taskId}/logs?${searchParams.toString()}`,
  );

  return response.json();
};

function TaskTubeTaskLogs(props: TaskTubeTaskLogsProps): JSX.Element {
  const { correlationId, taskId } = props;

  const [logs, setLogs] = useState<TaskTubeTaskLog[]>([]);
  const [totalCount, setTotalCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const { isPending, isError, data, error, isFetching, isPlaceholderData } = useQuery({
    queryKey: ['taskLogs', taskId, page, rowsPerPage],
    queryFn: () =>
      fetchTaskLogs({
        correlationId,
        taskId,
        page,
        rowsPerPage,
      }),
    refetchOnWindowFocus: false,
    refetchIntervalInBackground: false,
    placeholderData: keepPreviousData,
    staleTime: 5000,
  });

  useEffect(() => {
    handleResponse(isPending, isError, data, error);
  }, [isPending, isError, data, error]);

  const handleResponse = (
    isPending: boolean,
    isError: boolean,
    data: TaskTubeTaskLogsResponse | undefined,
    error: Error | null,
  ) => {
    if (isPending) {
      setLoading(true);
    } else if (isError) {
      console.error('Error fetching tasks:', error);
      setLoading(false);
    } else if (data) {
      setLogs(data.logs);
      setTotalCount(data.totalCount);
      setLoading(false);
    }
  };

  const handleCellClick = (event: React.MouseEvent<HTMLTableCellElement>) => {
    const cellText = event.currentTarget.textContent;
    if (cellText) {
      navigator.clipboard.writeText(cellText).catch((err) => {
        console.error('Failed to copy to clipboard:', err);
      });
    }
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

  return (
    <>
      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress />
        </Box>
      ) : logs.length > 0 ? (
        <>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Typography variant="h5">Results</Typography>
              {isFetching && <CircularProgress size="1.5rem" sx={{ ml: 1 }} />}
            </Box>
            <TablePagination
              rowsPerPageOptions={[5, 10, 25, 50, 100]}
              component="div"
              count={totalCount}
              rowsPerPage={rowsPerPage}
              page={page}
              onPageChange={handleChangePage}
              onRowsPerPageChange={handleChangeRowsPerPage}
            />
          </Box>
          <TableContainer component={Paper}>
            <Table>
              <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
                <TableRow>
                  <TableCell sx={{ fontWeight: 600 }}>Timestamp</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Level</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Type</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Message</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Exception</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {logs.map((log) => (
                  <TableRow key={log.id} hover>
                    <TableCell onClick={handleCellClick}>
                      <Tooltip
                        disableFocusListener
                        placement="right"
                        arrow
                        disableInteractive
                        title="Copy to clipboard"
                      >
                        <span style={{ cursor: 'pointer' }}>
                          {DateTimeUtils.formatDateTime(
                            log.timestamp,
                            DateTimeUtils.DateTimeFormater.DEFAULT,
                          )}
                        </span>
                      </Tooltip>
                    </TableCell>
                    <TableCell onClick={handleCellClick}>
                      <Tooltip
                        disableFocusListener
                        placement="right"
                        arrow
                        disableInteractive
                        title="Copy to clipboard"
                      >
                        <span style={{ cursor: 'pointer' }}>
                          <Chip
                            label={log.level}
                            size="small"
                            color={getSeveretyColor(log.level)}
                          />
                        </span>
                      </Tooltip>
                    </TableCell>
                    <TableCell onClick={handleCellClick}>
                      <Tooltip
                        disableFocusListener
                        placement="right"
                        arrow
                        disableInteractive
                        title="Copy to clipboard"
                      >
                        <span style={{ cursor: 'pointer' }}>{log.type}</span>
                      </Tooltip>
                    </TableCell>

                    <TableCell onClick={handleCellClick}>
                      <Tooltip
                        disableFocusListener
                        placement="right"
                        arrow
                        disableInteractive
                        title="Copy to clipboard"
                      >
                        <span style={{ cursor: 'pointer', wordBreak: 'break-all' }}>
                          {log.message}
                        </span>
                      </Tooltip>
                    </TableCell>
                    <TableCell onClick={handleCellClick}>
                      <Tooltip
                        disableFocusListener
                        placement="right"
                        arrow
                        disableInteractive
                        title="Copy to clipboard"
                      >
                        <span style={{ cursor: 'pointer', wordBreak: 'break-all' }}>
                          {log.exceptionMessage || '-'}
                        </span>
                      </Tooltip>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            <TablePagination
              rowsPerPageOptions={[5, 10, 25, 50, 100]}
              component="div"
              count={totalCount}
              rowsPerPage={rowsPerPage}
              page={page}
              onPageChange={handleChangePage}
              onRowsPerPageChange={handleChangeRowsPerPage}
            />
          </Box>
        </>
      ) : (
        <Paper sx={{ p: 3, textAlign: 'center' }}>
          <Typography color="textSecondary">No logs found for this task. 1</Typography>
        </Paper>
      )}
    </>
  );
}

export default TaskTubeTaskLogs;
