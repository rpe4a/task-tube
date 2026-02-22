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
} from '@mui/material';
import { JSX } from 'react';
import TaskPageDto from '../../models/TaskPageDto';
import * as DateTimeUtils from '../../../../shared/utils/DateTimeUtils';

interface TaskTableLayoutProps {
  loading: boolean;
  tasks: TaskPageDto[];
  page: number;
  rowsPerPage: number;
  onChangePage: (event: unknown, newPage: number) => void;
  onChangeRowsPerPage: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

const getStatusColor = (
  status: TaskPageDto['status'],
): 'default' | 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success' => {
  const statusColorMap: Record<
    TaskPageDto['status'],
    'default' | 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success'
  > = {
    CREATED: 'default',
    SCHEDULED: 'primary',
    CANCELED: 'warning',
    PROCESSING: 'info',
    COMPLETED: 'success',
    ABORTED: 'error',
    FINISHED: 'secondary',
  };
  return statusColorMap[status];
};

function TaskTableLayout(props: TaskTableLayoutProps): JSX.Element {
  const { loading, tasks, page, rowsPerPage, onChangePage, onChangeRowsPerPage } = props;
  const paginatedTasks = tasks.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);

  const handleCellDoubleClick = (event: React.MouseEvent<HTMLTableCellElement>) => {
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
              <Typography variant="h5">Results</Typography>
            </Box>
            <TablePagination
              rowsPerPageOptions={[5, 10, 25, 50]}
              component="div"
              count={tasks.length}
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
                  <TableCell sx={{ fontWeight: 600 }}>Id</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Name</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Tube</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Status</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Created At</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Updated At</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Completed At</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Aborted At</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Duration</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Handled By</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {paginatedTasks.map((task) => (
                  <TableRow key={task.id} hover>
                    <TableCell onDoubleClick={handleCellDoubleClick}>{task.id}</TableCell>
                    <TableCell onDoubleClick={handleCellDoubleClick}>{task.name}</TableCell>
                    <TableCell onDoubleClick={handleCellDoubleClick}>{task.tube}</TableCell>
                    <TableCell onDoubleClick={handleCellDoubleClick}>
                      <Chip label={task.status} color={getStatusColor(task.status)} size="small" />
                    </TableCell>
                    <TableCell onDoubleClick={handleCellDoubleClick}>
                      {DateTimeUtils.formatDateTime(task.createdAt)}
                    </TableCell>
                    <TableCell onDoubleClick={handleCellDoubleClick}>
                      {DateTimeUtils.formatDateTime(task.updatedAt)}
                    </TableCell>
                    <TableCell onDoubleClick={handleCellDoubleClick}>
                      {DateTimeUtils.formatDateTime(task.completedAt)}
                    </TableCell>
                    <TableCell onDoubleClick={handleCellDoubleClick}>
                      {DateTimeUtils.formatDateTime(task.abortedAt)}
                    </TableCell>
                    <TableCell onDoubleClick={handleCellDoubleClick}>
                      {DateTimeUtils.calculateDuration(
                        task.createdAt,
                        task.completedAt,
                        task.abortedAt,
                      )}
                    </TableCell>
                    <TableCell onDoubleClick={handleCellDoubleClick}>{task.handledBy}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            <TablePagination
              rowsPerPageOptions={[5, 10, 25, 50]}
              component="div"
              count={tasks.length}
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
