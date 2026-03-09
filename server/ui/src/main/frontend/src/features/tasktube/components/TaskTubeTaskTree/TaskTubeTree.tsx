import { Alert, AlertColor, Badge, Box, Button, Chip, Typography } from '@mui/material';
import { TaskStatus } from '../../../../shared/models/TaskStatus';
import TaskTubeTreeNodeResponse from '../../../../pages/TaskTubePage/models/TaskTubeTreeNodeResponse';
import { useEffect, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getStatusColor } from '../../../../shared/utils/ColorUtils';
import * as DateTimeUtils from '../../../../shared/utils/DateTimeUtils';
import SchemaIcon from '@mui/icons-material/Schema';
import TaskTubeTreeNode from '../../../../pages/TaskTubePage/models/TaskTubeTreeNode';
import { s } from 'react-router/dist/development/index-react-server-client-1TI9M9o1';
import dayjs from 'dayjs';

interface TaskTubeTreeProps {
  correlationId: string;
  taskId: string;
  rootNode: TaskTubeTreeNode | null;
  isRefreshChildren: boolean;
  updateTaskTubeTaskLayout: (correlationId: string, taskId: string) => void;
}

function getSeveretyColor(status: TaskStatus): AlertColor {
  const statusColorMap: Record<TaskStatus, AlertColor> = {
    CREATED: 'info',
    SCHEDULED: 'warning',
    CANCELED: 'error',
    PROCESSING: 'warning',
    COMPLETED: 'success',
    ABORTED: 'error',
    FINISHED: 'info',
  };

  return statusColorMap[status];
}

const fetchTaskTubeTaskChildren = async (
  correlationId: string | undefined,
  taskId: string | undefined,
): Promise<TaskTubeTreeNodeResponse> => {
  const response = await fetch(`/api/v1/tasktube/${correlationId}/task/${taskId}/treenode`);
  return response.json();
};

const isTaskTerminated = (task: TaskTubeTreeNode): boolean => {
  return task.status === 'COMPLETED' || task.status === 'ABORTED' || task.status === 'CANCELED';
};

function TaskTubeTree(props: TaskTubeTreeProps) {
  const {
    correlationId,
    taskId,
    rootNode,
    isRefreshChildren: isRefreshChildrenProp,
    updateTaskTubeTaskLayout,
  } = props;

  const [root, setRoot] = useState<TaskTubeTreeNode | null>(rootNode);
  const [children, setChildren] = useState<TaskTubeTreeNode[] | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isRefreshChildren, setIsRefreshChildren] = useState(isRefreshChildrenProp);

  const { isError, isFetching, data, error } = useQuery({
    queryKey: ['tasktube.treeNode', correlationId, taskId],
    queryFn: () => fetchTaskTubeTaskChildren(correlationId, taskId),
    refetchOnWindowFocus: false,
    refetchIntervalInBackground: true,
    refetchInterval: () => {
      if (root && isTaskTerminated(root)) {
        return false;
      }

      return 15000;
    },
    enabled: !root || isRefreshChildren,
  });

  useEffect(() => {
    setRoot(rootNode);
  }, [rootNode]);

  useEffect(() => {
    handleResponse(isFetching, isError, data, error);
  }, [isError, isFetching, data, error]);

  const handleResponse = (
    isFetching: boolean,
    isError: boolean,
    data: TaskTubeTreeNodeResponse | undefined,
    error: Error | null,
  ) => {
    if (isFetching) {
      setIsLoading(true);
    } else if (isError) {
      console.error('Error fetching task tree:', error);
      setIsLoading(false);
    } else if (data) {
      setRoot(data.root);
      setChildren(data.children);
      setIsLoading(false);
    }
  };

  function handleChildrenLoad(event: React.MouseEvent<HTMLButtonElement>): void {
    event.preventDefault();
    setIsRefreshChildren(!isRefreshChildren);
  }

  function handleTaskClick(event: React.MouseEvent<HTMLDivElement>): void {
    event.preventDefault();
    updateTaskTubeTaskLayout(correlationId, taskId);
  }

  return (
    <>
      {root && (
        <Box
          sx={{
            mt: '-1px',
            borderTop: '1px solid #ccc',
            minWidth: '600px',
          }}
        >
          <Alert
            icon={false}
            severity={getSeveretyColor(root.status)}
            sx={{ borderRadius: 0, borderBottom: '1px solid #ccc', borderLeft: '1px solid #ccc' }}
          >
            <Typography gutterBottom sx={{ color: 'text.main', fontSize: 14 }}>
              {root.id}{' '}
              <Chip label={root.status} color={getStatusColor(root.status)} size="small" />
            </Typography>
            <Typography
              onClick={handleTaskClick}
              variant="h6"
              style={{ wordBreak: 'break-all', cursor: 'pointer' }}
            >
              {root.name}
            </Typography>
            <Typography gutterBottom sx={{ color: 'text.main', fontSize: 14 }}>
              Duration:{' '}
              {DateTimeUtils.calculateDuration(root.createdAt, root.completedAt, root.abortedAt, root.canceledAt)}
            </Typography>
            {isFetching && (
              <Button loading variant="outlined" loadingPosition="start">
                Load children
              </Button>
            )}
            {!isFetching && root && root.childrenCount > 0 && (
              <Button
                onClick={handleChildrenLoad}
                size="small"
                variant="outlined"
                color="inherit"
                startIcon={
                  <Badge
                    color="default"
                    overlap="rectangular"
                    badgeContent={root.childrenCount}
                    sx={{ mt: 0.5 }}
                  >
                    <SchemaIcon />
                  </Badge>
                }
              >
                {isRefreshChildren ? 'Hide children' : 'Show children'}
              </Button>
            )}
          </Alert>
          {isRefreshChildren && children && (
            <Box sx={{ ml: 2 }}>
              {children
                .sort((a, b) => (dayjs(a.completedAt).isBefore(dayjs(b.completedAt)) ? -1 : 1))
                .map((childNood) => (
                  <TaskTubeTree
                    key={childNood.id}
                    correlationId={correlationId}
                    taskId={childNood.id}
                    isRefreshChildren={false}
                    rootNode={childNood}
                    updateTaskTubeTaskLayout={updateTaskTubeTaskLayout}
                  />
                ))}
            </Box>
          )}
        </Box>
      )}
    </>
  );
}

export default TaskTubeTree;
