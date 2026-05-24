import { Grid, Paper, Typography } from '@mui/material';
import JsonView from '@uiw/react-json-view';
import { TaskTubeTaskResponse } from '../model/TaskTubeTaskResponse';
import { lightTheme } from '@uiw/react-json-view/light';
import { memo } from 'react';

function TaskTubeTaskInputOutput(props: { task: TaskTubeTaskResponse }) {
  const { task } = props;

  return (
    <Grid container spacing={2}>
      <Grid size={6}>
        <Paper elevation={2} sx={{ p: 2 }}>
          <Typography variant="h5" sx={{ mb: 1 }}>
            Input
          </Typography>
          {task.input ? (
            <JsonView value={task.input} style={lightTheme} collapsed={2} />
          ) : (
            <Typography>No input data yet</Typography>
          )}
        </Paper>
      </Grid>
      <Grid size={6}>
        <Paper elevation={2} sx={{ p: 2 }}>
          <Typography variant="h5" sx={{ mb: 1 }}>
            Output
          </Typography>
          {task.output ? (
            <JsonView value={task.output} style={lightTheme} collapsed={2} />
          ) : (
            <Typography>No output data yet</Typography>
          )}
        </Paper>
      </Grid>
    </Grid>
  );
}

export default memo(TaskTubeTaskInputOutput);
