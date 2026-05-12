import AccountTreeIcon from '@mui/icons-material/AccountTree';
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
  TablePagination,
  Tooltip,
  Button,
  Alert,
  LinearProgress,
} from '@mui/material';
import { JSX, memo, useCallback } from 'react';
import TasksPageTaskDto from '../../../app/pages/TasksPage/models/TasksPageTaskDto';
import * as DateTimeUtils from '../../../shared/utils/DateTimeUtils';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import { Link } from 'react-router';
import { getStatusColor } from '../../../shared/utils/ColorUtils';
import PlaylistAddIcon from '@mui/icons-material/PlaylistAdd';
import TableSkeleton from '../../../shared/component/TableSkeleton';
import IconLink from '../../../shared/component/IconLink';
dayjs.extend(utc);

interface TaskTableProps {
  isPending: boolean;
  isFetching: boolean;
  isError: boolean;
  tasks: TasksPageTaskDto[];
  totalCount: number;
  page: number;
  rowsPerPage: number;
  sort: string;
  by: 'asc' | 'desc';
  onChangePage: (value: number) => void;
  onChangeRowsPerPage: (value: string) => void;
  onSortChange: (sort: string, by: 'asc' | 'desc') => void;
}

function TaskTable(props: TaskTableProps): JSX.Element {
  const {
    isPending,
    isFetching,
    isError,
    tasks,
    totalCount,
    page,
    rowsPerPage,
    sort,
    by,
    onChangePage,
    onChangeRowsPerPage,
    onSortChange,
  } = props;

  const handleSortClick = useCallback(
    (columnName: string) => {
      const newDirection = sort === columnName && by === 'asc' ? 'desc' : 'asc';
      onSortChange(columnName, newDirection);
    },
    [onSortChange, sort, by],
  );

  const renderSortIcon = useCallback(
    (columnName: string) => {
      if (sort === columnName) {
        return by === 'asc' ? ' ▲' : ' ▼';
      }
      return '';
    },
    [sort, by],
  );

  const handleCellClick = useCallback((event: React.MouseEvent<HTMLTableCellElement>) => {
    const cellText = event.currentTarget.textContent;
    if (cellText) {
      navigator.clipboard.writeText(cellText).catch((err) => {
        console.error('Failed to copy to clipboard:', err);
      });
    }
  }, []);

  return (
    <>
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Button
            component={Link}
            to={`/tasktube/push`}
            title="Push tasktube"
            target="_blank"
            variant="text"
            size="large"
          >
            PUSH TASK
          </Button>
        </Box>
        <TablePagination
          rowsPerPageOptions={[5, 10, 25, 50]}
          component="div"
          count={totalCount}
          rowsPerPage={rowsPerPage}
          page={page}
          onPageChange={(_, newPage) => onChangePage(newPage)}
          onRowsPerPageChange={(e) => onChangeRowsPerPage(e.target.value as string)}
        />
      </Box>
      <Box sx={{ height: '4.444px', marginBottom: '-4.444px' }}>
        {(isPending || isFetching) && <LinearProgress />}
      </Box>
      <TableContainer component={Paper}>
        <Table>
          <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
            <TableRow>
              <TableCell sx={{ fontWeight: 600, width: '100px' }}></TableCell>
              <TableCell
                sx={{
                  fontWeight: 600,
                  cursor: 'pointer',
                  userSelect: 'none',
                  '&:hover': { backgroundColor: '#e0e0e0' },
                  minWidth: 100,
                }}
                onClick={() => handleSortClick('id')}
              >
                ID{renderSortIcon('id')}
              </TableCell>
              <TableCell
                sx={{
                  fontWeight: 600,
                  cursor: 'pointer',
                  userSelect: 'none',
                  '&:hover': { backgroundColor: '#e0e0e0' },
                  minWidth: 150,
                }}
                onClick={() => handleSortClick('name')}
              >
                Name{renderSortIcon('name')}
              </TableCell>
              <TableCell
                sx={{
                  fontWeight: 600,
                  cursor: 'pointer',
                  userSelect: 'none',
                  '&:hover': { backgroundColor: '#e0e0e0' },
                }}
                onClick={() => handleSortClick('tube')}
              >
                Tube{renderSortIcon('tube')}
              </TableCell>
              <TableCell
                sx={{
                  fontWeight: 600,
                  cursor: 'pointer',
                  userSelect: 'none',
                  '&:hover': { backgroundColor: '#e0e0e0' },
                  minWidth: 100,
                }}
                onClick={() => handleSortClick('status')}
              >
                Status{renderSortIcon('status')}
              </TableCell>
              <TableCell
                sx={{
                  fontWeight: 600,
                  cursor: 'pointer',
                  userSelect: 'none',
                  '&:hover': { backgroundColor: '#e0e0e0' },
                }}
                onClick={() => handleSortClick('created_at')}
              >
                Created At{renderSortIcon('created_at')}
              </TableCell>
              <TableCell
                sx={{
                  fontWeight: 600,
                  cursor: 'pointer',
                  userSelect: 'none',
                  '&:hover': { backgroundColor: '#e0e0e0' },
                }}
                onClick={() => handleSortClick('updated_at')}
              >
                Updated At{renderSortIcon('updated_at')}
              </TableCell>
              <TableCell
                sx={{
                  fontWeight: 600,
                  cursor: 'pointer',
                  userSelect: 'none',
                  '&:hover': { backgroundColor: '#e0e0e0' },
                }}
                onClick={() => handleSortClick('completed_at')}
              >
                Completed At{renderSortIcon('completed_at')}
              </TableCell>
              <TableCell
                sx={{
                  fontWeight: 600,
                  cursor: 'pointer',
                  userSelect: 'none',
                  '&:hover': { backgroundColor: '#e0e0e0' },
                }}
                onClick={() => handleSortClick('aborted_at')}
              >
                Aborted At{renderSortIcon('aborted_at')}
              </TableCell>
              <TableCell
                sx={{
                  fontWeight: 600,
                }}
              >
                Duration
              </TableCell>
              <TableCell
                sx={{
                  fontWeight: 600,
                  cursor: 'pointer',
                  userSelect: 'none',
                  '&:hover': { backgroundColor: '#e0e0e0' },
                }}
                onClick={() => handleSortClick('handled_by')}
              >
                Handled By{renderSortIcon('handled_by')}
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody sx={{ opacity: isFetching ? 0.5 : 1, transition: 'opacity 0.25s' }}>
            {isPending ? (
              <TableSkeleton rowsNum={rowsPerPage} colsNum={11} />
            ) : isError ? (
              <TableRow>
                <TableCell colSpan={11} sx={{ padding: 0, borderBottom: '0px' }}>
                  <Alert severity="error" sx={{ borderRadius: 0 }}>
                    Something went wrong while fetching tasks from server. Please try again later.
                  </Alert>
                </TableCell>
              </TableRow>
            ) : !isFetching && tasks.length === 0 ? (
              <TableRow>
                <TableCell colSpan={11} align="center">
                  No tasks found. Use the form above to fetch tasks.
                </TableCell>
              </TableRow>
            ) : (
              tasks.map((task) => (
                <TableRow key={task.id} hover>
                  <TableCell>
                    <Box sx={{ display: 'flex', flexDirection: 'row', gap: 1 }}>
                      <IconLink
                        to={`/tasktube/push?correlationId=${task.correlationId}&taskId=${task.id}`}
                        size="small"
                        title="Push the same task"
                      >
                        <PlaylistAddIcon fontSize="medium" />
                      </IconLink>
                      <IconLink
                        to={`/tasktube/${task.correlationId}/tasks/${task.id}`}
                        size="small"
                        title="View the task"
                      >
                        <AccountTreeIcon fontSize="medium" />
                      </IconLink>
                    </Box>
                  </TableCell>
                  <TableCell onClick={handleCellClick}>
                    <Tooltip
                      disableFocusListener
                      placement="right"
                      arrow
                      disableInteractive
                      title="Copy to clipboard"
                    >
                      <span style={{ cursor: 'pointer', wordBreak: 'break-all' }}>{task.id}</span>
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
                      <span style={{ cursor: 'pointer', wordBreak: 'break-all' }}>{task.name}</span>
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
                      <span style={{ cursor: 'pointer', wordBreak: 'break-all' }}>{task.tube}</span>
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
                          label={task.status}
                          color={getStatusColor(task.status)}
                          size="small"
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
                      <span style={{ cursor: 'pointer' }}>
                        {DateTimeUtils.formatDateTime(
                          task.createdAt,
                          DateTimeUtils.DateTimeFormater.CALENDAR,
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
                        {DateTimeUtils.formatDateTime(
                          task.updatedAt,
                          DateTimeUtils.DateTimeFormater.CALENDAR,
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
                        {DateTimeUtils.formatDateTime(
                          task.completedAt,
                          DateTimeUtils.DateTimeFormater.CALENDAR,
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
                        {DateTimeUtils.formatDateTime(
                          task.abortedAt,
                          DateTimeUtils.DateTimeFormater.CALENDAR,
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
                        {DateTimeUtils.calculateDuration(
                          task.createdAt,
                          task.completedAt,
                          task.abortedAt,
                          task.canceledAt,
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
                      <span style={{ cursor: 'pointer', wordBreak: 'break-all' }}>
                        {task.handledBy}
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
          rowsPerPageOptions={[5, 10, 25, 50]}
          component="div"
          count={totalCount}
          rowsPerPage={rowsPerPage}
          page={page}
          onPageChange={(_, newPage) => onChangePage(newPage)}
          onRowsPerPageChange={(e) => onChangeRowsPerPage(e.target.value as string)}
        />
      </Box>
    </>
  );
}

export default memo(TaskTable);
