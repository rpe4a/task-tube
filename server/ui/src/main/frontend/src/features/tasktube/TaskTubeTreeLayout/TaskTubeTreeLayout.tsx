import { Box } from '@mui/material';
import TaskTubeTree from './components/TaskTubeTree';

interface TaskTubeTreeLayoutProps {
  correlationId: string;
  taskId: string;
  updateTaskTubeTaskLayout: (correlationId: string, taskId: string) => void;
}

function TaskTubeTreeLayout(props: TaskTubeTreeLayoutProps) {
  const { correlationId, taskId, updateTaskTubeTaskLayout } = props;
  return (
    <Box sx={{ width: '100%' }}>
      <TaskTubeTree
        correlationId={correlationId}
        taskId={taskId}
        isRefreshChildren={true}
        rootNode={null}
        updateTaskTubeTaskLayout={updateTaskTubeTaskLayout}
      />
    </Box>
  );
}

export default TaskTubeTreeLayout;
