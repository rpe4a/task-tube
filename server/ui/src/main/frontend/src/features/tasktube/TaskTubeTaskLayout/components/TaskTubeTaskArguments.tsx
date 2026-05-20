import { Typography } from '@mui/material';
import { memo } from 'react';

interface TaskTubeTaskArgumentsProps {
  correlationId: string;
  taskId: string;
}

function TaskTubeTaskArguments(props: TaskTubeTaskArgumentsProps) {
  const { correlationId, taskId } = props;

  return <Typography>Arguments are not ready yet</Typography>;
}

export default memo(TaskTubeTaskArguments);
