import { JSX, memo, useCallback, useEffect, useState } from 'react';
import { Box, Button, TextField, Stack, Paper, Typography } from '@mui/material';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import { UUID } from 'crypto';
import { PushedTask } from '../TaskTubePushedTasks/storage/PushedTasksHistory';
import { isUUID } from '../../../shared/utils/UuidUtils';
import { TaskTubeTaskResponse } from '../TaskTubeTaskLayout/model/TaskTubeTaskResponse';
import { enqueueSnackbar } from 'notistack';
import api from '../../../shared/api';

dayjs.extend(utc);

interface TaskTubePushFormData {
  id: string;
  name: string;
  tube: string;
  correlationId: string;
  input: string;
  settings: string;
}

type TaskTubePushFormError = Record<keyof TaskTubePushFormData, string | null>;

interface TaskTubePushFormLayoutProps {
  addTask: (task: PushedTask) => void;
  isFetching: boolean;
  task: TaskTubeTaskResponse | null;
}

const defaultFormData: TaskTubePushFormData = {
  id: '',
  name: '',
  tube: '',
  correlationId: '',
  input: JSON.stringify([], null, 2),
  settings: JSON.stringify(
    {
      maxFailures: 3,
      failureRetryTimeoutSeconds: 60,
      timeoutSeconds: 60 * 60,
      heartbeatTimeoutSeconds: 60,
    },
    null,
    2,
  ),
};

const defaultFormErrors: TaskTubePushFormError = {
  id: null,
  name: null,
  tube: null,
  correlationId: null,
  input: null,
  settings: null,
};

const getTaskOrDefaultFormData = (task: TaskTubeTaskResponse | null): TaskTubePushFormData => {
  return task
    ? {
        id: crypto.randomUUID(),
        name: task.name,
        tube: task.tube,
        correlationId: crypto.randomUUID(),
        input: JSON.stringify(task.input, null, 2),
        settings: JSON.stringify(task.settings, null, 2),
      }
    : defaultFormData;
};

const fetchPushTaskAsync = async (params: TaskTubePushFormData): Promise<UUID> => {
  const response = await api.post<UUID>(`/api/v1/tasktube/push`, {
    ...params,
    createdAt: dayjs().toISOString(),
    input: JSON.parse(params.input),
    settings: JSON.parse(params.settings),
  });

  return response.data;
};

function TaskTubePushForm(props: TaskTubePushFormLayoutProps): JSX.Element {
  const { addTask, isFetching, task } = props;

  const [formData, setFormData] = useState<TaskTubePushFormData>(getTaskOrDefaultFormData(task));
  const [formErrors, setFormErrors] = useState<TaskTubePushFormError>(defaultFormErrors);

  const generateUUID = useCallback((field: 'id' | 'correlationId'): void => {
    setFormData((prev) => ({
      ...prev,
      [field]: crypto.randomUUID(),
    }));
    setFormErrors((prev) => ({
      ...prev,
      [field]: null,
    }));
  }, []);

  const validFormInput = useCallback(
    (field: keyof TaskTubePushFormData, value: string | null): string | null => {
      if (field === 'id' || field === 'correlationId') {
        if (!value || !isUUID(value)) {
          return `The field '${field}' is not valid.`;
        }
      } else {
        if (!value) {
          return `The field '${field}' is not valid.`;
        }
      }
      return null;
    },
    [],
  );

  const resetForm = useCallback((): void => {
    setFormData(defaultFormData);
    setFormErrors(defaultFormErrors);
  }, []);

  const handleInputChange = useCallback(
    (field: keyof TaskTubePushFormData, value: string): void => {
      setFormData((prev) => ({
        ...prev,
        [field]: value,
      }));
    },
    [],
  );

  const handleInputBlur = useCallback(
    (field: keyof TaskTubePushFormData, value: string): void => {
      const error = validFormInput(field, value);
      setFormErrors((prev) => ({
        ...prev,
        [field]: error,
      }));
    },
    [validFormInput],
  );

  const handleSubmit = useCallback(
    async (e: React.FormEvent): Promise<void> => {
      e.preventDefault();
      try {
        let errors = false;
        for (const formField in formData) {
          if (!Object.hasOwn(formData, formField)) continue;
          const field = formField as keyof TaskTubePushFormData;
          const value = formData[field];
          const error = validFormInput(field, value);

          setFormErrors((prev) => ({
            ...prev,
            [field]: error,
          }));

          if (error) {
            errors = true;
          }
        }

        if (errors) {
          return;
        }

        await fetchPushTaskAsync(formData);

        addTask({
          id: formData.id,
          name: formData.name,
          correlationId: formData.correlationId,
          tube: formData.tube,
        });

        resetForm();

        enqueueSnackbar(`Task ${formData.id} has been pushed successfully.`, {
          variant: 'success',
        });
      } catch (error) {
        console.error('Error pushing task:', error);
      }
    },
    [formData, resetForm, addTask, validFormInput],
  );

  const handleResetForm = useCallback((): void => {
    resetForm();
  }, [resetForm]);

  useEffect(() => {
    setFormData(getTaskOrDefaultFormData(task));
  }, [task]);

  return (
    <Box component="form" onSubmit={handleSubmit}>
      <Typography variant="h5" sx={{ mb: 2 }}>
        Push Task
      </Typography>

      <Paper elevation={2} sx={{ p: 3 }}>
        <Stack spacing={2}>
          <Box sx={{ display: 'flex', gap: 1 }}>
            <TextField
              fullWidth
              label="Id"
              name="id"
              placeholder="e.g., c7a1b6f2-1234-5678-9abc-def012345678"
              value={formData.id}
              onChange={(e) =>
                handleInputChange(e.target.name as keyof TaskTubePushFormData, e.target.value)
              }
              onBlur={(e) =>
                handleInputBlur(e.target.name as keyof TaskTubePushFormData, e.target.value)
              }
              size="medium"
              required
              type="text"
              disabled={isFetching}
              error={!!formErrors['id']}
              helperText={!!formErrors['id'] && formErrors['id']}
            />
            <Button
              size="medium"
              variant="outlined"
              title="Random UUID"
              onClick={(_) => generateUUID('id')}
              sx={{ marginBottom: !!formErrors['id'] ? '23px' : '0' }}
            >
              UUID
            </Button>
          </Box>
          <TextField
            fullWidth
            label="Name"
            name="name"
            size="medium"
            value={formData.name}
            onChange={(e) =>
              handleInputChange(e.target.name as keyof TaskTubePushFormData, e.target.value)
            }
            onBlur={(e) =>
              handleInputBlur(e.target.name as keyof TaskTubePushFormData, e.target.value)
            }
            required
            type="text"
            placeholder="e.g., simple.task"
            error={!!formErrors['name']}
            helperText={!!formErrors['name'] && formErrors['name']}
          />
          <TextField
            fullWidth
            label="Tube"
            name="tube"
            size="medium"
            value={formData.tube}
            onChange={(e) =>
              handleInputChange(e.target.name as keyof TaskTubePushFormData, e.target.value)
            }
            onBlur={(e) =>
              handleInputBlur(e.target.name as keyof TaskTubePushFormData, e.target.value)
            }
            required
            type="text"
            placeholder="e.g., simple-tube"
            error={!!formErrors['tube']}
            helperText={!!formErrors['tube'] && formErrors['tube']}
          />
          <Box sx={{ display: 'flex', gap: 1 }}>
            <TextField
              fullWidth
              label="Correlation ID"
              name="correlationId"
              value={formData.correlationId}
              onChange={(e) =>
                handleInputChange(e.target.name as keyof TaskTubePushFormData, e.target.value)
              }
              onBlur={(e) =>
                handleInputBlur(e.target.name as keyof TaskTubePushFormData, e.target.value)
              }
              size="medium"
              required
              type="text"
              placeholder="e.g., c7a1b6f2-1234-5678-9abc-def012345678"
              error={!!formErrors['correlationId']}
              helperText={!!formErrors['correlationId'] && formErrors['correlationId']}
            />
            <Button
              variant="outlined"
              onClick={(_) => generateUUID('correlationId')}
              size="medium"
              sx={{ marginBottom: !!formErrors['correlationId'] ? '23px' : '0' }}
            >
              UUID
            </Button>
          </Box>
          <TextField
            fullWidth
            label="Input (JSON)"
            name="input"
            multiline
            rows={7}
            value={formData.input}
            onChange={(e) =>
              handleInputChange(e.target.name as keyof TaskTubePushFormData, e.target.value)
            }
            onBlur={(e) =>
              handleInputBlur(e.target.name as keyof TaskTubePushFormData, e.target.value)
            }
            placeholder='[
  {
    "type": "CONSTANT",
    "value": "hello",
    "valueReferenceType": "java.lang.String"
  }
]'
            variant="outlined"
            required
            type="text"
            error={!!formErrors['input']}
            helperText={!!formErrors['input'] && formErrors['input']}
          />
          <TextField
            fullWidth
            label="Settings (JSON)"
            name="settings"
            placeholder='{
  "maxFailures": 3,
  "failureRetryTimeoutSeconds": 60,
  "timeoutSeconds": 3600,
  "heartbeatTimeoutSeconds": 60
}'
            multiline
            rows={7}
            value={formData.settings}
            onChange={(e) =>
              handleInputChange(e.target.name as keyof TaskTubePushFormData, e.target.value)
            }
            onBlur={(e) =>
              handleInputBlur(e.target.name as keyof TaskTubePushFormData, e.target.value)
            }
            variant="outlined"
            required
            type="text"
            error={!!formErrors['settings']}
            helperText={!!formErrors['settings'] && formErrors['settings']}
          />
        </Stack>
        <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
          <Button
            type="submit"
            variant="contained"
            color="primary"
            loading={isFetching}
            size="large"
          >
            PUSH
          </Button>

          <Button
            variant="outlined"
            color="primary"
            size="large"
            onClick={(_) => handleResetForm()}
          >
            RESET FORM
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}

export default memo(TaskTubePushForm);
