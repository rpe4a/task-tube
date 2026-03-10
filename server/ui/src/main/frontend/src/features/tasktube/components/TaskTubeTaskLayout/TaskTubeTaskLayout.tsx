import React, { useEffect, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { TaskTubeTaskResponse } from '../../../../pages/TaskTubePage/models/TaskTubeTaskResponse';
import { Box, Tabs, Tab, Typography, CircularProgress, Grid, Chip } from '@mui/material';
import { formatDateTime, calculateDuration } from '../../../../shared/utils/DateTimeUtils';
import { lightTheme } from '@uiw/react-json-view/light';
import JsonView from '@uiw/react-json-view';
import { getStatusColor } from '../../../../shared/utils/ColorUtils';
import TaskTubeTaskLogs from '../TaskTubeTaskLogs/TaskTubeTaskLogs';

interface TaskTubeTaskLayoutProps {
  correlationId: string;
  taskId: string;
}

const fetchTaskTubeTask = async (
  correlationId: string,
  taskId: string,
): Promise<TaskTubeTaskResponse> => {
  const response = await fetch(`/api/v1/tasktube/${correlationId}/task/${taskId}`);
  return response.json();
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

function TaskTubeTaskLayout(props: TaskTubeTaskLayoutProps) {
  const { correlationId, taskId } = props;

  const [task, setTask] = useState<TaskTubeTaskResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [showLog, setShowLog] = useState(false);
  const [tabIndex, setTabIndex] = useState(0);

  const { isPending, isError, isFetching, data, error } = useQuery({
    queryKey: ['tasktube', correlationId, taskId],
    queryFn: () => fetchTaskTubeTask(correlationId, taskId),
    refetchOnWindowFocus: false,
    refetchIntervalInBackground: true,
    refetchInterval: () => {
      if (task && isTaskTerminated(task)) {
        return false;
      }
      return 15000;
    },
  });

  useEffect(() => {
    handleResponse(isPending, isError, data, error);
  }, [isPending, isError, isFetching, data, error]);

  useEffect(() => {
    setTabIndex(0);
  }, [correlationId, taskId]);

  const handleResponse = (
    isPending: boolean,
    isError: boolean,
    data: TaskTubeTaskResponse | undefined,
    error: Error | null,
  ) => {
    if (isPending) {
      setLoading(true);
    } else if (isError) {
      console.error('Error fetching tasks:', error);
      setLoading(false);
    } else if (data) {
      setTask(data);
      setLoading(false);
    }
  };

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabIndex(newValue);

    if (newValue === 1) {
      setShowLog(true);
    }
  };

  // render
  return (
    <Box sx={{ width: '100%' }}>
      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}>
          <CircularProgress />
        </Box>
      )}
      {!loading && task && (
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
            <Tab label="Logs" {...a11yProps(1)} />
            <Tab label="Input" {...a11yProps(2)} />
            <Tab label="Output" {...a11yProps(3)} />
            <Tab label="Settings" {...a11yProps(4)} />
            <Tab label="Children" {...a11yProps(5)} />
          </Tabs>

          <TabPanel value={tabIndex} index={0}>
            <Grid container spacing={2}>
              <Grid size={12}>
                <Typography variant="h5">{task.name}</Typography>
              </Grid>
              <Grid size={6}>
                <Typography variant="subtitle2">ID</Typography>
                <Typography>{task.id}</Typography>
              </Grid>
              <Grid size={6}>
                <Typography variant="subtitle2">Correlation</Typography>
                <Typography>{task.correlationId}</Typography>
              </Grid>
              <Grid size={12}>
                <Typography variant="subtitle2">Tube</Typography>
                <Typography>{task.tube}</Typography>
              </Grid>
              <Grid size={12}>
                <Typography variant="subtitle2">Status</Typography>
                <Chip color={getStatusColor(task.status)} label={task.status} />
              </Grid>
              <Grid container direction={'column'} size={6}>
                <Typography variant="h6">Server processing time</Typography>
                <Grid size={12}>
                  <Typography variant="subtitle2">Created At</Typography>
                  <Typography>{formatDateTime(task.createdAt)}</Typography>
                </Grid>
                <Grid size={12}>
                  <Typography variant="subtitle2">Scheduled At</Typography>
                  <Typography>{formatDateTime(task.scheduledAt)}</Typography>
                </Grid>
                <Grid size={12}>
                  <Typography variant="subtitle2">Aborted At</Typography>
                  <Typography>{formatDateTime(task.abortedAt)}</Typography>
                </Grid>
                <Grid size={12}>
                  <Typography variant="subtitle2">Canceled At</Typography>
                  <Typography>{formatDateTime(task.canceledAt)}</Typography>
                </Grid>
                <Grid size={12}>
                  <Typography variant="subtitle2">Completed At</Typography>
                  <Typography>{formatDateTime(task.completedAt)}</Typography>
                </Grid>
                <Grid size={12}>
                  <Typography variant="subtitle2">Duration</Typography>
                  <Typography>
                    {calculateDuration(
                      task.createdAt,
                      task.completedAt,
                      task.abortedAt,
                      task.canceledAt,
                    )}
                  </Typography>
                </Grid>
              </Grid>
              <Grid container direction={'column'} size={6}>
                <Typography variant="h6">Client processing time</Typography>
                <Grid size={12}>
                  <Typography variant="subtitle2">Started At</Typography>
                  <Typography>{formatDateTime(task.startedAt)}</Typography>
                </Grid>
                <Grid size={12}>
                  <Typography variant="subtitle2">Hearthbeat At</Typography>
                  <Typography>{formatDateTime(task.heartbeatAt)}</Typography>
                </Grid>
                <Grid size={12}>
                  <Typography variant="subtitle2">Finished At</Typography>
                  <Typography>{formatDateTime(task.finishedAt)}</Typography>
                </Grid>
                <Grid size={12}>
                  <Typography variant="subtitle2">Duration</Typography>
                  <Typography>
                    {calculateDuration(task.startedAt, task.finishedAt, null, null)}
                  </Typography>
                </Grid>
                <Grid size={12}>
                  <Typography variant="subtitle2">Handled By</Typography>
                  <Typography>{task.handledBy}</Typography>
                </Grid>
              </Grid>
              {task.failures > 0 && (
                <>
                  <Grid size={12}>
                    <Typography variant="subtitle2">Failed At</Typography>
                    <Typography>{formatDateTime(task.failedAt)}</Typography>
                  </Grid>
                  <Grid size={12}>
                    <Typography variant="subtitle2">Failure Reason</Typography>
                    <Typography>{task.failureReason}</Typography>
                  </Grid>
                  <Grid size={12}>
                    <Typography variant="subtitle2">Failures Count</Typography>
                    <Typography>{task.failures}</Typography>
                  </Grid>
                </>
              )}
            </Grid>
          </TabPanel>

          <TabPanel value={tabIndex} index={1}>
            {showLog ? (
              <TaskTubeTaskLogs correlationId={correlationId} taskId={taskId} />
            ) : (
              <Typography>No logs available</Typography>
            )}
          </TabPanel>

          <TabPanel value={tabIndex} index={2}>
            {task.input && task.input !== 'undefined' ? (
              <JsonView value={JSON.parse(task.input)} style={lightTheme} collapsed={2} />
            ) : (
              <Typography>No input data yet</Typography>
            )}
          </TabPanel>

          <TabPanel value={tabIndex} index={3}>
            {task.output && task.output !== 'undefined' ? (
              <JsonView value={JSON.parse(task.output)} style={lightTheme} collapsed={2} />
            ) : (
              <Typography>No output data yet</Typography>
            )}
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

export default TaskTubeTaskLayout;
