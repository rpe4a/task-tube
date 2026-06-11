import { Alert, AlertColor, Badge, Box, Button, Chip, Typography } from '@mui/material';
import { isTaskTerminal, TaskStatus } from '../../../../shared/models/TaskStatus';
import TaskTubeTreeNodeResponse from '../model/TaskTubeTreeNodeResponse';
import { memo, useCallback, useEffect, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getStatusColor } from '../../../../shared/utils/ColorUtils';
import * as DateTimeUtils from '../../../../shared/utils/DateTimeUtils';
import SchemaIcon from '@mui/icons-material/Schema';
import TaskTubeTreeNode from '../model/TaskTubeTreeNode';
import dayjs from 'dayjs';
import api from '../../../../shared/api';

interface TaskTubeTreeProps {
  correlationId: string;
  taskId: string;
  taskIdSummary: string;
  rootNode: TaskTubeTreeNode | null;
  isRefreshChildren: boolean;
  updateTaskTubeTaskLayout: (correlationId: string, taskId: string) => void;
}

function getSeveretyColor(status: TaskStatus): AlertColor {
  const statusColorMap: Record<TaskStatus, AlertColor> = {
    CREATED: 'info',
    SCHEDULED: 'info',
    FINISHED: 'info',
    PROCESSING: 'warning',
    COMPLETED: 'success',
    ABORTED: 'error',
    TERMINATED: 'error',
  };

  return statusColorMap[status];
}

const fetchTaskTubeTaskChildren = async (
  correlationId: string | undefined,
  taskId: string | undefined,
): Promise<TaskTubeTreeNodeResponse> => {
  const response = await api.get<TaskTubeTreeNodeResponse>(
    `/api/v1/tasktube/${correlationId}/task/${taskId}/treenode`,
  );
  return response.data;
};

function TaskTubeTree(props: TaskTubeTreeProps) {
  const {
    correlationId,
    taskId,
    taskIdSummary,
    rootNode,
    isRefreshChildren: isRefreshChildrenProp,
    updateTaskTubeTaskLayout,
  } = props;

  const [root, setRoot] = useState<TaskTubeTreeNode | null>(rootNode);
  const [children, setChildren] = useState<TaskTubeTreeNode[] | null>(null);
  const [isRefreshChildren, setIsRefreshChildren] = useState(isRefreshChildrenProp);

  const { isError, isFetching, data, error } = useQuery({
    queryKey: ['tasktube.treeNode', correlationId, taskId],
    queryFn: () => fetchTaskTubeTaskChildren(correlationId, taskId),
    refetchOnWindowFocus: false,
    refetchIntervalInBackground: true,
    refetchInterval: () => {
      if (root && isTaskTerminal(root)) {
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
    if (isError) {
      console.error('Error fetching task tree:', error);
    } else if (data) {
      setRoot(data.root);
      setChildren(data.children);
    }
  }, [isError, isFetching, data, error]);

  const handleChildrenLoad = useCallback(
    (event: React.MouseEvent<HTMLButtonElement>): void => {
      event.preventDefault();
      setIsRefreshChildren(!isRefreshChildren);
    },
    [isRefreshChildren],
  );

  const handleTaskClick = useCallback(
    (event: React.MouseEvent<HTMLDivElement>): void => {
      event.preventDefault();
      updateTaskTubeTaskLayout(correlationId, taskId);
    },
    [correlationId, taskId, updateTaskTubeTaskLayout],
  );

  return (
    <>
      {root && (
        <Box
          sx={{
            mt: '-1px',
            borderTop: '1px solid #ccc',
            width: 1,
          }}
        >
          <Alert
            icon={false}
            severity={getSeveretyColor(root.status)}
            variant={root.id === taskIdSummary ? 'outlined' : 'standard'}
            sx={{
              borderRadius: 0,
              borderBottom: '1px solid #ccc',
              borderLeft: '1px solid #ccc',
              borderRight: 'none',
              borderTop: 'none',
            }}
          >
            <Typography gutterBottom sx={{ color: 'text.main', fontSize: 14 }}>
              {root.id}{' '}
              <Chip
                component="span"
                label={root.status}
                color={getStatusColor(root.status)}
                size="small"
              />
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
              {DateTimeUtils.calculateDuration(
                root.createdAt,
                root.completedAt,
                root.abortedAt,
                root.canceledAt,
              )}
            </Typography>
            {isFetching && root && root.childrenCount > 0 && (
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
                .sort((a, b) => (dayjs(a.createdAt).isBefore(dayjs(b.createdAt)) ? -1 : 1))
                .sort((a, b) => (dayjs(a.scheduledAt).isBefore(dayjs(b.scheduledAt)) ? -1 : 1))
                .sort((a, b) => (dayjs(a.completedAt).isBefore(dayjs(b.completedAt)) ? -1 : 1))
                .map((childNood) => (
                  <TaskTubeTree
                    key={childNood.id}
                    correlationId={correlationId}
                    taskId={childNood.id}
                    taskIdSummary={taskIdSummary}
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

export default memo(TaskTubeTree);
