import React, { memo, useCallback, useEffect, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { TaskTubeTaskResponse } from './model/TaskTubeTaskResponse';
import { Box, Tabs, Tab, Typography, CircularProgress, Grid, Chip } from '@mui/material';
import TaskTubeTaskLogs from './components/TaskTubeTaskLogs';
import TaskTubeTaskSummary from './components/TaskTubeTaskSummary';
import api from '../../../shared/api';
import TaskTubeTaskInputOutput from './components/TaskTubeTaskInputOutput';

interface TaskTubeTaskProps {
  correlationId: string;
  taskId: string;
}

const fetchTaskTubeTaskAsync = async (
  correlationId: string,
  taskId: string,
): Promise<TaskTubeTaskResponse> => {
  const response = await api.get<TaskTubeTaskResponse>(
    `/api/v1/tasktube/${correlationId}/task/${taskId}`,
  );
  return response.data;
};

// helper components for accessibility and panels
interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`task-tabpanel-${index}`}
      aria-labelledby={`task-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 2 }}>{children}</Box>}
    </div>
  );
}

function a11yProps(index: number) {
  return {
    id: `task-tab-${index}`,
    'aria-controls': `task-tabpanel-${index}`,
  };
}

const isTaskTerminated = (task: TaskTubeTaskResponse): boolean => {
  return task.status === 'COMPLETED' || task.status === 'ABORTED' || task.status === 'CANCELED';
};

function TaskTubeTask(props: TaskTubeTaskProps) {
  const { correlationId, taskId } = props;

  const [task, setTask] = useState<TaskTubeTaskResponse | null>(null);
  const [showLog, setShowLog] = useState(false);
  const [tabIndex, setTabIndex] = useState(0);

  const { isPending, isError, isFetching, data, error } = useQuery({
    queryKey: ['tasktube', correlationId, taskId],
    queryFn: () => fetchTaskTubeTaskAsync(correlationId, taskId),
    refetchIntervalInBackground: true,
    refetchInterval: () => {
      if (task && isTaskTerminated(task)) {
        return false;
      }
      return 15000;
    },
  });

  useEffect(() => {
    if (isError) {
      console.error('Error fetching task:', error);
    } else if (data) {
      setTask(data);
    }
  }, [isError, data, error]);

  useEffect(() => {
    setTabIndex(0);
  }, [correlationId, taskId]);

  const handleTabChange = useCallback((_: React.SyntheticEvent, newValue: number) => {
    setTabIndex(newValue);

    if (newValue === 2) {
      setShowLog(true);
    }
  }, []);

  return (
    <Box sx={{ width: 1, height: 1 }}>
      {(isPending || isFetching) && (
        <Box
          sx={{
            display: 'flex',
            mx: 'auto',
            alignItems: 'center',
            justifyContent: 'center',
            height: 1,
          }}
        >
          <CircularProgress />
        </Box>
      )}
      {!isFetching && task && (
        <>
          <Tabs
            value={tabIndex}
            onChange={handleTabChange}
            aria-label="Task details tabs"
            variant="fullWidth"
            scrollButtons="auto"
            centered
            sx={{ borderBottom: 1, borderColor: 'divider' }}
          >
            <Tab label="Summary" {...a11yProps(0)} />
            <Tab label="Arguments" {...a11yProps(1)} />
            <Tab label="Logs" {...a11yProps(2)} />
            <Tab label="Input / Output" {...a11yProps(3)} />
            <Tab label="Settings" {...a11yProps(4)} />
            <Tab label="Children" {...a11yProps(5)} />
          </Tabs>

          <TabPanel value={tabIndex} index={0}>
            <TaskTubeTaskSummary task={task} />
          </TabPanel>

          <TabPanel value={tabIndex} index={1}>
            <Typography>Arguments are not ready yet</Typography>
          </TabPanel>

          <TabPanel value={tabIndex} index={2}>
            {showLog ? (
              <TaskTubeTaskLogs correlationId={correlationId} taskId={taskId} />
            ) : (
              <Typography>No logs available</Typography>
            )}
          </TabPanel>

          <TabPanel value={tabIndex} index={3}>
            <TaskTubeTaskInputOutput task={task} />
          </TabPanel>

          <TabPanel value={tabIndex} index={4}>
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
          </TabPanel>

          <TabPanel value={tabIndex} index={5}>
            <Typography>Children count: {task.countChildren}</Typography>
            {/* future: list child tasks */}
          </TabPanel>
        </>
      )}
    </Box>
  );
}

export default memo(TaskTubeTask);
