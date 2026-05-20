import { Grid, Typography } from '@mui/material';
import { TaskTubeTaskResponse } from '../model/TaskTubeTaskResponse';
import { memo } from 'react';

function TaskTubeTaskSettings(props: { task: TaskTubeTaskResponse }) {
  const { task } = props;

  return (
    <Grid container spacing={2}>
      <Grid size={6}>
        <Typography variant="subtitle2">Max Failures</Typography>
        <Typography>{task.settings.maxFailures}</Typography>
      </Grid>
      <Grid size={6}>
        <Typography variant="subtitle2">Failure Retry (s)</Typography>
        <Typography>{task.settings.failureRetryTimeoutSeconds}</Typography>
      </Grid>
      <Grid size={6}>
        <Typography variant="subtitle2">Timeout (s)</Typography>
        <Typography>{task.settings.timeoutSeconds}</Typography>
      </Grid>
      <Grid size={6}>
        <Typography variant="subtitle2">Heartbeat Timeout (s)</Typography>
        <Typography>{task.settings.heartbeatTimeoutSeconds}</Typography>
      </Grid>
    </Grid>
  );
}

export default memo(TaskTubeTaskSettings);
