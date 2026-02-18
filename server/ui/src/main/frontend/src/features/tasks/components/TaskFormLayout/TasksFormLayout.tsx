import { Box, Button, CircularProgress, Paper, TextField, Typography } from '@mui/material';
import { JSX } from 'react';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { Dayjs } from 'dayjs';

interface TasksFormLayoutProps {
  customTube: string;
  createdAt: Dayjs | null;
  completedAt: Dayjs | null;
  loading: boolean;
  handleCustomTubeChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleCreatedAtChange: (value: Dayjs | null) => void;
  handleCompletedAtChange: (value: Dayjs | null) => void;
  handleFetchTasks: (createdAt?: Dayjs | null, completedAt?: Dayjs | null) => void;
  handleFetchAll: () => void;
}

function TaskFormLayout(props: TasksFormLayoutProps): JSX.Element {
  const {
    customTube,
    createdAt,
    completedAt,
    loading,
    handleCustomTubeChange,
    handleCreatedAtChange,
    handleCompletedAtChange,
    handleFetchTasks,
    handleFetchAll,
  } = props;

  return (
    <>
      <Paper elevation={2} sx={{ p: 3, mb: 4 }}>
        <Typography variant="h5" sx={{ mb: 3 }}>
          Fetch Tasks
        </Typography>

        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'flex-end' }}>
          <TextField
            label="Or enter custom tube name"
            value={customTube}
            onChange={handleCustomTubeChange}
            placeholder="e.g., custom-tube"
            sx={{ minWidth: 200 }}
          />

          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DateTimePicker
              label="Created At"
              value={createdAt}
              onChange={handleCreatedAtChange}
              disabled={loading}
            />
            <DateTimePicker
              label="Completed At"
              value={completedAt}
              onChange={handleCompletedAtChange}
              disabled={loading}
            />
          </LocalizationProvider>

          <Button
            variant="contained"
            onClick={() => handleFetchTasks(createdAt, completedAt)}
            disabled={loading}
            sx={{ textTransform: 'none' }}
          >
            {loading ? <CircularProgress size={24} /> : 'Fetch Tasks'}
          </Button>

          <Button
            variant="outlined"
            onClick={handleFetchAll}
            disabled={loading}
            sx={{ textTransform: 'none' }}
          >
            Fetch All
          </Button>
        </Box>
      </Paper>
    </>
  );
}

export default TaskFormLayout;
