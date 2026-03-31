import { JSX, useEffect, useState } from 'react';
import {
  Box,
  Button,
  TextField,
  Alert,
  CircularProgress,
  Stack,
  Paper,
  Typography,
} from '@mui/material';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import { UUID } from 'crypto';
import { PushedTask } from '../../../pages/TaskTubePushPage/storage/PushedTasks';
import axios from 'axios';
import { isUUID } from '../../../shared/utils/UuidUtils';
import { TaskTubeTaskResponse } from '../TaskTubeTaskLayout/model/TaskTubeTaskResponse';

dayjs.extend(utc);

interface TaskTubePushFormLayoutData {
  id: string;
  name: string;
  tube: string;
  correlationId: string;
  input: string;
  settings: string;
}

type TaskTubePushFormLayoutError = Record<keyof TaskTubePushFormLayoutData, string | null>;

interface TaskTubePushFormLayoutProps {
  addTask: (task: PushedTask) => void;
  loading: boolean;
  task: TaskTubeTaskResponse | null;
}

const defaultFormData: TaskTubePushFormLayoutData = {
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

const defaultFormError: TaskTubePushFormLayoutError = {
  id: null,
  name: null,
  tube: null,
  correlationId: null,
  input: null,
  settings: null,
};

const getTaskOrDefaultFormData = (
  task: TaskTubeTaskResponse | null,
): TaskTubePushFormLayoutData => {
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

const fetchPushTask = async (params: TaskTubePushFormLayoutData): Promise<UUID> => {
  const response = await axios.post<UUID>(`/api/v1/tasktube/push`, {
    ...params,
    createdAt: dayjs().toISOString(),
    input: JSON.parse(params.input),
    settings: JSON.parse(params.settings),
  });

  return response.data;
};

function TaskTubePushFormLayout(props: TaskTubePushFormLayoutProps): JSX.Element {
  const { addTask, loading: loadingProp, task } = props;
  const [formData, setFormData] = useState<TaskTubePushFormLayoutData>(
    getTaskOrDefaultFormData(task),
  );
  const [formErrors, setFormErrors] = useState<TaskTubePushFormLayoutError>(defaultFormError);
  const [loading, setLoading] = useState(loadingProp);
  const [message, setMessage] = useState<{
    type: 'success' | 'error';
    text: string;
  } | null>(null);

  useEffect(() => {
    setFormData(getTaskOrDefaultFormData(task));
    setLoading(loadingProp);
  }, [loadingProp, task]);

  const generateUUID = (field: 'id' | 'correlationId'): void => {
    setFormData((prev) => ({
      ...prev,
      [field]: crypto.randomUUID(),
    }));
    setFormErrors((prev) => ({
      ...prev,
      [field]: null,
    }));
  };

  const validFormInput = (
    field: keyof TaskTubePushFormLayoutData,
    value: string | null,
  ): string | null => {
    if (field === 'id') {
      if (!value || !isUUID(value)) {
        return "The field 'id' is not valid.";
      }
    } else if (field === 'correlationId') {
      if (!value || !isUUID(value)) {
        return "The field 'correlationId' is not valid.";
      }
    } else if (field === 'name') {
      if (!value) {
        return "The field 'name' is not valid.";
      }
    } else if (field === 'tube') {
      if (!value) {
        return "The field 'tube' is not valid.";
      }
    } else if (field === 'input') {
      if (!value) {
        return "The field 'input' is not valid.";
      }
    } else if (field === 'settings') {
      if (!value) {
        return "The field 'settings' is not valid.";
      }
    }
    return null;
  };

  const resetForm = (): void => {
    setFormData(defaultFormData);
    setFormErrors(defaultFormError);
  };

  const handleInputChange = (field: keyof TaskTubePushFormLayoutData, value: string): void => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleInputBlur = (field: keyof TaskTubePushFormLayoutData, value: string): void => {
    const error = validFormInput(field, value);
    setFormErrors((prev) => ({
      ...prev,
      [field]: error,
    }));
  };

  const handleSubmit = async (e: React.FormEvent): Promise<void> => {
    e.preventDefault();
    try {
      setMessage(null);

      let errors = false;
      for (const formField in formData) {
        if (!Object.hasOwn(formData, formField)) continue;
        const field = formField as keyof TaskTubePushFormLayoutData;
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
        setMessage({
          type: 'error',
          text: 'The form has some errors.',
        });
        return;
      }

      setLoading(true);

      await fetchPushTask(formData);

      addTask({ id: formData.id, name: formData.name, correlationId: formData.correlationId });

      setMessage({
        type: 'success',
        text: `Task '${formData.id}' has pushed successfully!`,
      });

      resetForm();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error instanceof Error ? error.message : 'An error occurred',
      });
    } finally {
      setLoading(false);
    }
  };

  const handleResetButtonClick = (): void => {
    resetForm();
    setMessage(null);
  };

  return (
    <Box component="form" onSubmit={handleSubmit}>
      <Typography variant="h5" sx={{ mb: 2 }}>
        Push Task
      </Typography>

      <Paper elevation={2} sx={{ p: 3 }}>
        {message && (
          <Alert severity={message.type} sx={{ mb: 3 }}>
            {message.text}
          </Alert>
        )}

        <Stack spacing={2}>
          <Box sx={{ display: 'flex', gap: 1 }}>
            <TextField
              fullWidth
              label="Id"
              value={formData.id}
              onChange={(e) => handleInputChange('id', e.target.value)}
              onBlur={(e) => handleInputBlur('id', e.target.value)}
              size="medium"
              required
              type="text"
              disabled={loading}
              error={!!formErrors['id']}
              helperText={!!formErrors['id'] && formErrors['id']}
            />
            <Button
              size="medium"
              variant="outlined"
              onClick={() => generateUUID('id')}
              disabled={loading}
              sx={{ marginBottom: !!formErrors['id'] ? '23px' : '0' }}
            >
              UUID
            </Button>
          </Box>
          <TextField
            fullWidth
            label="Name"
            size="medium"
            value={formData.name}
            onChange={(e) => handleInputChange('name', e.target.value)}
            onBlur={(e) => handleInputBlur('name', e.target.value)}
            required
            type="text"
            placeholder="Enter task name"
            disabled={loading}
            error={!!formErrors['name']}
            helperText={!!formErrors['name'] && formErrors['name']}
          />
          <TextField
            fullWidth
            label="Tube"
            size="medium"
            value={formData.tube}
            onChange={(e) => handleInputChange('tube', e.target.value)}
            onBlur={(e) => handleInputBlur('tube', e.target.value)}
            required
            type="text"
            placeholder="Enter tube name"
            disabled={loading}
            error={!!formErrors['tube']}
            helperText={!!formErrors['tube'] && formErrors['tube']}
          />
          <Box sx={{ display: 'flex', gap: 1 }}>
            <TextField
              fullWidth
              label="Correlation ID"
              value={formData.correlationId}
              onChange={(e) => handleInputChange('correlationId', e.target.value)}
              onBlur={(e) => handleInputBlur('correlationId', e.target.value)}
              size="medium"
              required
              type="text"
              disabled={loading}
              error={!!formErrors['correlationId']}
              helperText={!!formErrors['correlationId'] && formErrors['correlationId']}
            />
            <Button
              variant="outlined"
              onClick={() => generateUUID('correlationId')}
              disabled={loading}
              size="medium"
              sx={{ marginBottom: !!formErrors['correlationId'] ? '23px' : '0' }}
            >
              UUID
            </Button>
          </Box>
          <TextField
            fullWidth
            label="Input (JSON)"
            multiline
            rows={7}
            value={formData.input}
            onChange={(e) => handleInputChange('input', e.target.value)}
            onBlur={(e) => handleInputBlur('input', e.target.value)}
            placeholder="Enter task input data"
            variant="outlined"
            required
            type="text"
            disabled={loading}
            error={!!formErrors['input']}
            helperText={!!formErrors['input'] && formErrors['input']}
          />
          <TextField
            fullWidth
            label="Settings (JSON)"
            multiline
            rows={7}
            value={formData.settings}
            onChange={(e) => handleInputChange('settings', e.target.value)}
            onBlur={(e) => handleInputBlur('settings', e.target.value)}
            variant="outlined"
            required
            type="text"
            disabled={loading}
            error={!!formErrors['settings']}
            helperText={!!formErrors['settings'] && formErrors['settings']}
          />
        </Stack>
        <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
          <Button type="submit" variant="contained" color="primary" size="large" disabled={loading}>
            {loading ? <CircularProgress size={24} /> : 'Push'}
          </Button>

          <Button
            variant="outlined"
            color="primary"
            size="large"
            disabled={loading}
            onClick={(_) => handleResetButtonClick()}
          >
            Reset
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}

export default TaskTubePushFormLayout;
