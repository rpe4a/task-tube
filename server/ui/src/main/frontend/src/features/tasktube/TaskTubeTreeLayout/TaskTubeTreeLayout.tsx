import { Box } from '@mui/material';
import TaskTubeTree from './components/TaskTubeTree';
import { memo } from 'react';

interface TaskTubeTreeLayoutProps {
  correlationId: string;
  taskId: string;
  taskIdSummary: string;
  updateTaskTubeTaskLayout: (correlationId: string, taskId: string) => void;
}

function TaskTubeTreeLayout(props: TaskTubeTreeLayoutProps) {
  const { correlationId, taskId, taskIdSummary, updateTaskTubeTaskLayout } = props;
  return (
    <Box sx={{ width: '100%' }}>
      <TaskTubeTree
        correlationId={correlationId}
        taskId={taskId}
        taskIdSummary={taskIdSummary}
        isRefreshChildren={true}
        rootNode={null}
        updateTaskTubeTaskLayout={updateTaskTubeTaskLayout}
      />
    </Box>
  );
}

export default memo(TaskTubeTreeLayout);
