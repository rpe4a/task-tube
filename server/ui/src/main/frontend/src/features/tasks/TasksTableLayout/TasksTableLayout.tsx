import AccountTreeIcon from '@mui/icons-material/AccountTree';
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
  Button,
} from '@mui/material';
import { JSX } from 'react';
import TasksPageTaskDto from '../../../pages/TasksPage/models/TasksPageTaskDto';
import * as DateTimeUtils from '../../../shared/utils/DateTimeUtils';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import { Link } from 'react-router';
import { getStatusColor } from '../../../shared/utils/ColorUtils';
import PlaylistAddIcon from '@mui/icons-material/PlaylistAdd';
dayjs.extend(utc);

interface TaskTableLayoutProps {
  loading: boolean;
  isFetching: boolean;
  tasks: TasksPageTaskDto[];
  totalCount: number;
  page: number;
  rowsPerPage: number;
  sort: string;
  by: 'asc' | 'desc';
  onChangePage: (event: unknown, newPage: number) => void;
  onChangeRowsPerPage: (event: React.ChangeEvent<HTMLInputElement>) => void;
  onSortChange: (sort: string, by: 'asc' | 'desc') => void;
}

function TaskTableLayout(props: TaskTableLayoutProps): JSX.Element {
  const {
    loading,
    isFetching,
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

  const handleSortClick = (columnName: string) => {
    const newDirection = sort === columnName && by === 'asc' ? 'desc' : 'asc';
    onSortChange(columnName, newDirection);
  };

  const renderSortIcon = (columnName: string) => {
    if (sort === columnName) {
      return by === 'asc' ? ' ▲' : ' ▼';
    }
    return '';
  };

  const handleCellClick = (event: React.MouseEvent<HTMLTableCellElement>) => {
    const cellText = event.currentTarget.textContent;
    if (cellText) {
      navigator.clipboard.writeText(cellText).catch((err) => {
        console.error('Failed to copy to clipboard:', err);
      });
    }
  };

  return (
    <>
      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress />
        </Box>
      ) : tasks.length > 0 ? (
        <>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Link to={`/tasktube/push`} title="Push tasktube" target="_blank">
                <Button variant="contained" size="large" sx={{ textTransform: 'none' }}>
                  PUSH
                </Button>
              </Link>
              {isFetching && <CircularProgress size="1.5rem" sx={{ ml: 1 }} />}
            </Box>
            <TablePagination
              rowsPerPageOptions={[5, 10, 25, 50]}
              component="div"
              count={totalCount}
              rowsPerPage={rowsPerPage}
              page={page}
              onPageChange={onChangePage}
              onRowsPerPageChange={onChangeRowsPerPage}
            />
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
                    }}
                    onClick={() => handleSortClick('id')}
                  >
                    id{renderSortIcon('id')}
                  </TableCell>
                  <TableCell
                    sx={{
                      fontWeight: 600,
                      cursor: 'pointer',
                      userSelect: 'none',
                      '&:hover': { backgroundColor: '#e0e0e0' },
                    }}
                    onClick={() => handleSortClick('name')}
                  >
                    name{renderSortIcon('name')}
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
                    tube{renderSortIcon('tube')}
                  </TableCell>
                  <TableCell
                    sx={{
                      fontWeight: 600,
                      cursor: 'pointer',
                      userSelect: 'none',
                      '&:hover': { backgroundColor: '#e0e0e0' },
                    }}
                    onClick={() => handleSortClick('status')}
                  >
                    status{renderSortIcon('status')}
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
                    created_at{renderSortIcon('created_at')}
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
                    updated_at{renderSortIcon('updated_at')}
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
                    completed_at{renderSortIcon('completed_at')}
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
                    aborted_at{renderSortIcon('aborted_at')}
                  </TableCell>
                  <TableCell
                    sx={{
                      fontWeight: 600,
                    }}
                  >
                    duration
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
                    handled_by{renderSortIcon('handled_by')}
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {tasks.map((task) => (
                  <TableRow key={task.id} hover>
                    <TableCell>
                      <Box sx={{ display: 'flex', flexDirection: 'row', gap: 1.5 }}>
                        <Link
                          to={`/tasktube/push?correlationId=${task.correlationId}&taskId=${task.id}`}
                          title="Push"
                          target="_blank"
                        >
                          <PlaylistAddIcon color="primary" />
                        </Link>
                        <Link
                          to={`/tasktube/${task.correlationId}/tasks/${task.id}`}
                          title="Show"
                          target="_blank"
                        >
                          <AccountTreeIcon color="primary" />
                        </Link>
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
                        <span style={{ cursor: 'pointer', wordBreak: 'break-all' }}>
                          {task.name}
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
                          {task.tube}
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
                ))}
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
              onPageChange={onChangePage}
              onRowsPerPageChange={onChangeRowsPerPage}
            />
          </Box>
        </>
      ) : (
        <Paper sx={{ p: 3, textAlign: 'center' }}>
          <Typography color="textSecondary">
            No tasks found. Use the form above to fetch tasks.
          </Typography>
        </Paper>
      )}
    </>
  );
}

export default TaskTableLayout;
