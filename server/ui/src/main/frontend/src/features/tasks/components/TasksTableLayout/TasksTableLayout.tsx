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
} from '@mui/material';
import { JSX } from 'react';
import TaskPageDto from '../../models/TaskPageDto';
import * as DateTimeUtils from '../../../../shared/utils/DateTimeUtils';

interface TaskTableLayoutProps {
  loading: boolean;
  tasks: TaskPageDto[];
}

const getStatusColor = (
  status: TaskPageDto['status'],
): 'default' | 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success' => {
  const statusColorMap: Record<
    TaskPageDto['status'],
    'default' | 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success'
  > = {
    PENDING: 'warning',
    PROCESSING: 'info',
    COMPLETED: 'success',
    ABORTED: 'error',
  };
  return statusColorMap[status];
};

function TaskTableLayout(props: TaskTableLayoutProps): JSX.Element {
  const { loading, tasks } = props;
  return (
    <>
      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress />
        </Box>
      ) : tasks.length > 0 ? (
        <TableContainer component={Paper}>
          <Table>
            <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 600 }}>Name</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Tube</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Status</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Created At</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Updated At</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Completed At</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Handled By</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {tasks.map((task) => (
                <TableRow key={task.id} hover>
                  <TableCell>{task.name}</TableCell>
                  <TableCell>{task.tube}</TableCell>
                  <TableCell>
                    <Chip label={task.status} color={getStatusColor(task.status)} size="small" />
                  </TableCell>
                  <TableCell>{DateTimeUtils.formatDateTime(task.createdAt)}</TableCell>
                  <TableCell>{DateTimeUtils.formatDateTime(task.updatedAt)}</TableCell>
                  <TableCell>{DateTimeUtils.formatDateTime(task.completedAt)}</TableCell>
                  <TableCell>{task.handledBy}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
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
