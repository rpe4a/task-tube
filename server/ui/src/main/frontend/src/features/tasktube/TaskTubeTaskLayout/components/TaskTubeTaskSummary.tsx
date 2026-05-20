import { Chip, Grid, Typography } from '@mui/material';
import { getStatusColor } from '../../../../shared/utils/ColorUtils';
import { calculateDuration, formatDateTime } from '../../../../shared/utils/DateTimeUtils';
import { TaskTubeTaskResponse } from '../model/TaskTubeTaskResponse';
import { memo } from 'react';

function TaskTubeTaskSummary(props: { task: TaskTubeTaskResponse }) {
  const { task } = props;
  return (
    <Grid container spacing={2}>
      <Grid size={12}>
        <Typography variant="h5">{task.name}</Typography>
      </Grid>
      <Grid size={6}>
        <Typography variant="subtitle2">ID</Typography>
        <Typography>{task.id}</Typography>
      </Grid>
      <Grid size={6}>
        <Typography variant="subtitle2">Correlation</Typography>
        <Typography>{task.correlationId}</Typography>
      </Grid>
      <Grid size={12}>
        <Typography variant="subtitle2">Tube</Typography>
        <Typography>{task.tube}</Typography>
      </Grid>
      <Grid size={12}>
        <Typography variant="subtitle2">Status</Typography>
        <Chip color={getStatusColor(task.status)} label={task.status} />
      </Grid>
      <Grid container direction={'column'} size={6}>
        <Typography variant="h6">Server processing time</Typography>
        <Grid size={12}>
          <Typography variant="subtitle2">Created At</Typography>
          <Typography>{formatDateTime(task.createdAt)}</Typography>
        </Grid>
        <Grid size={12}>
          <Typography variant="subtitle2">Scheduled At</Typography>
          <Typography>{formatDateTime(task.scheduledAt)}</Typography>
        </Grid>
        <Grid size={12}>
          <Typography variant="subtitle2">Aborted At</Typography>
          <Typography>{formatDateTime(task.abortedAt)}</Typography>
        </Grid>
        <Grid size={12}>
          <Typography variant="subtitle2">Canceled At</Typography>
          <Typography>{formatDateTime(task.canceledAt)}</Typography>
        </Grid>
        <Grid size={12}>
          <Typography variant="subtitle2">Completed At</Typography>
          <Typography>{formatDateTime(task.completedAt)}</Typography>
        </Grid>
        <Grid size={12}>
          <Typography variant="subtitle2">Duration</Typography>
          <Typography>
            {calculateDuration(task.createdAt, task.completedAt, task.abortedAt, task.canceledAt)}
          </Typography>
        </Grid>
      </Grid>
      <Grid container direction={'column'} size={6}>
        <Typography variant="h6">Client processing time</Typography>
        <Grid size={12}>
          <Typography variant="subtitle2">Started At</Typography>
          <Typography>{formatDateTime(task.startedAt)}</Typography>
        </Grid>
        <Grid size={12}>
          <Typography variant="subtitle2">Hearthbeat At</Typography>
          <Typography>{formatDateTime(task.heartbeatAt)}</Typography>
        </Grid>
        <Grid size={12}>
          <Typography variant="subtitle2">Finished At</Typography>
          <Typography>{formatDateTime(task.finishedAt)}</Typography>
        </Grid>
        <Grid size={12}>
          <Typography variant="subtitle2">Duration</Typography>
          <Typography>{calculateDuration(task.startedAt, task.finishedAt, null, null)}</Typography>
        </Grid>
        <Grid size={12}>
          <Typography variant="subtitle2">Handled By</Typography>
          <Typography>{task.handledBy}</Typography>
        </Grid>
      </Grid>
      {task.failures > 0 && (
        <>
          <Grid size={12}>
            <Typography variant="subtitle2">Failed At</Typography>
            <Typography>{formatDateTime(task.failedAt)}</Typography>
          </Grid>
          <Grid size={12}>
            <Typography variant="subtitle2">Failure Reason</Typography>
            <Typography>{task.failureReason}</Typography>
          </Grid>
          <Grid size={12}>
            <Typography variant="subtitle2">Failures Count</Typography>
            <Typography>{task.failures}</Typography>
          </Grid>
        </>
      )}
    </Grid>
  );
}

export default memo(TaskTubeTaskSummary);
