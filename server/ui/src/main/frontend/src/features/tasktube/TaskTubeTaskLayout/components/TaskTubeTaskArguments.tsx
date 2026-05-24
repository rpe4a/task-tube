import { Alert, AlertTitle, Grid, Paper, Skeleton, Typography } from '@mui/material';
import { lightTheme } from '@uiw/react-json-view/light';
import { memo, useEffect, useState } from 'react';
import api from '../../../../shared/api';
import { TaskTubeTaskArgumentsResponse } from '../model/TaskTubeTaskArgumentsResponse';
import JsonView from '@uiw/react-json-view';
import { keepPreviousData, useQuery } from '@tanstack/react-query';

interface TaskTubeTaskArgumentsProps {
  correlationId: string;
  taskId: string;
}

const fetchTaskArgumentsAsync = async (
  params: TaskTubeTaskArgumentsProps,
): Promise<TaskTubeTaskArgumentsResponse[]> => {
  const { taskId, correlationId } = params;

  const response = await api.get<TaskTubeTaskArgumentsResponse[]>(
    `/api/v1/tasktube/${correlationId}/task/${taskId}/arguments`,
  );

  return response.data;
};

function TaskTubeTaskArguments(props: TaskTubeTaskArgumentsProps) {
  const { correlationId, taskId } = props;

  const [taskArguments, setTaskArguments] = useState<TaskTubeTaskArgumentsResponse[] | null>(null);

  const { isPending, isError, data, error, isFetching } = useQuery({
    queryKey: ['taskArguments', correlationId, taskId],
    queryFn: () =>
      fetchTaskArgumentsAsync({
        correlationId,
        taskId,
      }),
    placeholderData: keepPreviousData,
  });

  useEffect(() => {
    if (isError) {
      console.error('Error fetching arguments:', error);
    } else if (data) {
      setTaskArguments(data);
    }
  }, [isPending, isError, data, error]);

  return (
    <Grid container spacing={2}>
      <Grid size={12}>
        <Paper elevation={2} sx={{ p: 2 }}>
          <Typography variant="h5" sx={{ mb: 1 }}>
            Arguments
          </Typography>
          {isError && (
            <Alert severity="error">
              <AlertTitle>Something went wrong</AlertTitle>
              Error fetching arguments from server.
            </Alert>
          )}
          {(isPending || isFetching) && <Skeleton variant="rounded" width="50%" height={100} />}
          {!isError && !isPending && !isFetching && (
            <>
              {!!taskArguments ? (
                <JsonView value={taskArguments} style={lightTheme} collapsed={5} />
              ) : (
                <Typography>Arguments haven't been ready yet.</Typography>
              )}
            </>
          )}
        </Paper>
      </Grid>
    </Grid>
  );
}

export default memo(TaskTubeTaskArguments);
