import {
  Box,
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
  LinearProgress,
  Alert,
} from '@mui/material';
import { JSX, useState, useEffect, useCallback, memo } from 'react';
import { LogLevel, TaskTubeTaskLog } from '../model/TaskTubeTaskLog';
import * as DateTimeUtils from '../../../../shared/utils/DateTimeUtils';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import { OverridableStringUnion } from '@mui/types';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import TaskTubeTaskLogsResponse from '../model/TaskTubeTaskLogsResponse';
import api from '../../../../shared/api';
import TableSkeleton from '../../../../shared/component/TableSkeleton';

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

const fetchTaskLogsAsync = async (
  params: FetchTaskLogsParams,
): Promise<TaskTubeTaskLogsResponse> => {
  const { page, rowsPerPage, taskId, correlationId } = params;

  let searchParams = new URLSearchParams();
  searchParams.append('page', page.toString());
  searchParams.append('size', rowsPerPage.toString());

  const response = await api.get<TaskTubeTaskLogsResponse>(
    `/api/v1/tasktube/${correlationId}/task/${taskId}/logs?${searchParams.toString()}`,
  );

  return response.data;
};

function TaskTubeTaskLogs(props: TaskTubeTaskLogsProps): JSX.Element {
  const { correlationId, taskId } = props;

  const [logs, setLogs] = useState<TaskTubeTaskLog[]>([]);
  const [totalCount, setTotalCount] = useState(0);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const { isPending, isError, data, error, isFetching, isPlaceholderData } = useQuery({
    queryKey: ['taskLogs', correlationId, taskId, page, rowsPerPage],
    queryFn: () =>
      fetchTaskLogsAsync({
        correlationId,
        taskId,
        page,
        rowsPerPage,
      }),
    placeholderData: keepPreviousData,
  });

  useEffect(() => {
    if (isError) {
      console.error('Error fetching tasks:', error);
    } else if (data) {
      setLogs(data.logs);
      setTotalCount(data.totalCount);
    }
  }, [isPending, isError, data, error]);

  const handleCellClick = useCallback((event: React.MouseEvent<HTMLTableCellElement>) => {
    const cellText = event.currentTarget.textContent;
    if (cellText) {
      navigator.clipboard.writeText(cellText).catch((err) => {
        console.error('Failed to copy to clipboard:', err);
      });
    }
  }, []);

  const handleChangePage = useCallback(
    (_: unknown, newPage: number) => {
      if (!isPlaceholderData) {
        setPage(newPage);
      }
    },
    [isPlaceholderData],
  );

  const handleChangeRowsPerPage = useCallback((event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  }, []);

  return (
    <>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Typography variant="h5">Logs</Typography>
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
      <Box sx={{ height: '4.444px', marginBottom: '-4.444px' }}>
        {(isPending || isFetching) && <LinearProgress />}
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
          <TableBody sx={{ opacity: isFetching ? 0.5 : 1, transition: 'opacity 0.25s' }}>
            {isPending ? (
              <TableSkeleton rowsNum={rowsPerPage} colsNum={5} />
            ) : isError ? (
              <TableRow>
                <TableCell colSpan={5} sx={{ padding: 0, borderBottom: '0px' }}>
                  <Alert severity="error" sx={{ borderRadius: 0 }}>
                    Something went wrong while fetching logs from server. Please try again later.
                  </Alert>
                </TableCell>
              </TableRow>
            ) : !isFetching && logs.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  No logs found.
                </TableCell>
              </TableRow>
            ) : (
              logs.map((log) => (
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
                        <Chip label={log.level} size="small" color={getSeveretyColor(log.level)} />
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
              ))
            )}
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
  );
}

export default memo(TaskTubeTaskLogs);
