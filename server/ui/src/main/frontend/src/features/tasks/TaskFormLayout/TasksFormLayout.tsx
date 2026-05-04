import {
  Box,
  Button,
  CircularProgress,
  Paper,
  TextField,
  Typography,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Grid,
} from '@mui/material';
import { JSX, memo, useState } from 'react';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { Dayjs } from 'dayjs';
import 'dayjs/locale/en-gb';
import { isUUID } from '../../../shared/utils/UuidUtils';
import { useDebouncedCallback } from 'use-debounce';

interface TasksFormLayoutProps {
  createdFrom: Dayjs | null;
  createdTo: Dayjs | null;
  searchId: string;
  searchName: string;
  searchTube: string;
  searchStatus: string;
  loading: boolean;
  handleCreatedFromChange: (value: Dayjs | null) => void;
  handleCreatedToChange: (value: Dayjs | null) => void;
  handleSearchIdChange: (value: string) => void;
  handleSearchNameChange: (value: string) => void;
  handleSearchTubeChange: (value: string) => void;
  handleSearchStatusChange: (value: string) => void;
  handleSearchTasks: (event: React.MouseEvent<HTMLButtonElement>) => void;
}

function TaskFormLayout(props: TasksFormLayoutProps): JSX.Element {
  const {
    createdFrom,
    createdTo,
    searchId,
    searchStatus,
    loading,
    handleCreatedFromChange,
    handleCreatedToChange,
    handleSearchIdChange,
    handleSearchNameChange,
    handleSearchTubeChange,
    handleSearchStatusChange,
    handleSearchTasks,
  } = props;

  const [idError, setIdError] = useState<string>('');

  const handleIdChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;

    if (!value || isUUID(value)) {
      setIdError('');
      handleSearchIdChange(value);
    } else {
      setIdError('Invalid UUID format. Expected: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx');
    }
  };

  const debouncedNameChange = useDebouncedCallback((value) => {
    handleSearchNameChange(value);
  }, 500);

  const debouncedTubeChange = useDebouncedCallback((value) => {
    handleSearchTubeChange(value);
  }, 500);

  return (
    <>
      <Typography variant="h5" sx={{ mb: 2 }}>
        Search Executions
      </Typography>

      <Paper elevation={2} sx={{ p: 3, mb: 4 }}>
        <Grid container spacing={2} sx={{ mb: 3 }}>
          <Grid size={{ xs: 12, md: 6, lg: 3, xl: 3 }}>
            <TextField
              fullWidth
              label="ID"
              name="id"
              value={searchId}
              onChange={handleIdChange}
              placeholder="e.g., c7a1b6f2-1234-5678-9abc-def012345678"
              sx={{ minWidth: 200 }}
              disabled={loading}
              error={!!idError}
              helperText={idError}
            />
          </Grid>
          <Grid size={{ xs: 12, md: 6, lg: 4, xl: 4 }}>
            <TextField
              fullWidth
              label="Name"
              name="name"
              onChange={(e) => debouncedNameChange(e.target.value)}
              placeholder="e.g., simple.task"
              sx={{ minWidth: 200 }}
              disabled={loading}
            />
          </Grid>
          <Grid size={{ xs: 12, md: 6, lg: 3, xl: 3 }}>
            <TextField
              fullWidth
              label="Tube"
              name="tube"
              onChange={(e) => debouncedTubeChange(e.target.value)}
              placeholder="e.g., simple-tube"
              sx={{ minWidth: 200 }}
              disabled={loading}
            />
          </Grid>
          <Grid size={{ xs: 12, md: 6, lg: 2, xl: 2 }}>
            <FormControl fullWidth sx={{ minWidth: 150 }} disabled={loading}>
              <InputLabel>Status</InputLabel>
              <Select
                value={searchStatus}
                disabled={loading}
                onChange={(e) => handleSearchStatusChange(e.target.value as string)}
                label="Status"
              >
                <MenuItem value="">ALL</MenuItem>
                <MenuItem value="CREATED">CREATED</MenuItem>
                <MenuItem value="SCHEDULED">SCHEDULED</MenuItem>
                <MenuItem value="PROCESSING">PROCESSING</MenuItem>
                <MenuItem value="FINISHED">FINISHED</MenuItem>
                <MenuItem value="ABORTED">ABORTED</MenuItem>
                <MenuItem value="CANCELED">CANCELED</MenuItem>
                <MenuItem value="COMPLETED">COMPLETED</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>
        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'flex-end' }}>
          <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="en-gb">
            <DateTimePicker
              label="Created - from"
              name="createdFrom"
              value={createdFrom}
              onChange={handleCreatedFromChange}
              disabled={loading}
              timezone="UTC"
              slotProps={{ field: { clearable: true } }}
            />
            <DateTimePicker
              label="Created - To"
              name="createdTo"
              value={createdTo}
              onChange={handleCreatedToChange}
              disabled={loading}
              timezone="UTC"
              slotProps={{ field: { clearable: true } }}
            />
          </LocalizationProvider>

          <Button
            variant="contained"
            size="large"
            onClick={handleSearchTasks}
            disabled={loading}
            title="Search tasktube"
            sx={{ textTransform: 'none' }}
          >
            {loading ? <CircularProgress size={24} /> : 'SEARCH'}
          </Button>
        </Box>
      </Paper>
    </>
  );
}

export default memo(TaskFormLayout);
