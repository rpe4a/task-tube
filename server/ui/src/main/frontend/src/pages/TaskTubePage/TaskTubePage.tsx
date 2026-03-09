import { useState } from 'react';
import { useParams } from 'react-router';
import { Grid } from '@mui/material';
import TaskTubeTaskLayout from '../../features/tasktube/components/TaskTubeTaskLayout/TaskTubeTaskLayout';
import TaskTubeTreeLayout from '../../features/tasktube/components/TaskTubeTreeLayout/TaskTubeTreeLayout';

function TaskTubePage() {
  const { correlationIdParam, taskIdParam } = useParams();

  const [correlationId, setCorrelationId] = useState<string>(correlationIdParam || '');
  const [taskId, setTaskId] = useState<string>(taskIdParam || '');

  const updateTaskTubeTaskLayout = (correlationId: string, taskId: string) => {
    setCorrelationId(correlationId);
    setTaskId(taskId);
  };

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
          updateTaskTubeTaskLayout={updateTaskTubeTaskLayout}
        />
      </Grid>
      <Grid
        size={7}
        sx={{ borderLeft: '1px solid #ccc', height: '100vh', overflowX: 'auto', overflowY: 'auto' }}
      >
        <TaskTubeTaskLayout correlationId={correlationId} taskId={taskId} />
      </Grid>
    </Grid>
  );
}

export default TaskTubePage;
