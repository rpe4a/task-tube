import { memo, useCallback, useState } from 'react';
import { useParams } from 'react-router';
import { Grid } from '@mui/material';
import TaskTubeTask from '../../../features/tasktube/TaskTubeTaskLayout/TaskTubeTask';
import TaskTubeTreeLayout from '../../../features/tasktube/TaskTubeTreeLayout/TaskTubeTreeLayout';

function TaskTubePage() {
  const { correlationIdParam, taskIdParam } = useParams();

  const [correlationId, setCorrelationId] = useState<string>(correlationIdParam || '');
  const [taskId, setTaskId] = useState<string>(taskIdParam || '');

  const updateTaskTubeTaskLayout = useCallback((correlationId: string, taskId: string) => {
    setCorrelationId(correlationId);
    setTaskId(taskId);
  }, []);

  return (
    <Grid container>
      <Grid
        size={5}
        sx={{
          height: '100vh',
          overflowX: 'auto',
          overflowY: 'auto',
        }}
      >
        <TaskTubeTreeLayout
          correlationId={correlationIdParam || ''}
          taskId={taskIdParam || ''}
          taskIdSummary={taskId}
          updateTaskTubeTaskLayout={updateTaskTubeTaskLayout}
        />
      </Grid>
      <Grid
        size={7}
        sx={{ borderLeft: '1px solid #ccc', height: '100vh', overflowX: 'auto', overflowY: 'auto' }}
      >
        <TaskTubeTask correlationId={correlationId} taskId={taskId} />
      </Grid>
    </Grid>
  );
}

export default memo(TaskTubePage);
