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
  SelectChangeEvent,
  Grid,
} from '@mui/material';
import { JSX, useState } from 'react';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { Dayjs } from 'dayjs';
import 'dayjs/locale/en-gb';
import { isUUID } from '../../../shared/utils/UuidUtils';



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
  handleSearchIdChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleSearchNameChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleSearchTubeChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleSearchStatusChange: (event: SelectChangeEvent<string>) => void;
  handleSearchTasks: (event: React.MouseEvent<HTMLButtonElement>) => void;
}

function TaskFormLayout(props: TasksFormLayoutProps): JSX.Element {
  const {
    createdFrom,
    createdTo,
    searchId,
    searchName,
    searchTube,
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
      handleSearchIdChange(event);
    } else {
      setIdError('Invalid UUID format. Expected: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx');
    }
  };

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
              label="Search by ID"
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
              label="Search by Name"
              value={searchName}
              onChange={handleSearchNameChange}
              placeholder="e.g., Generate Report"
              sx={{ minWidth: 200 }}
              disabled={loading}
            />
          </Grid>
          <Grid size={{ xs: 12, md: 6, lg: 3, xl: 3 }}>
            <TextField
              fullWidth
              label="Search by Tube"
              value={searchTube}
              onChange={handleSearchTubeChange}
              placeholder="e.g., reporting"
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
                onChange={handleSearchStatusChange}
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
              label="Created - From"
              value={createdFrom}
              onChange={handleCreatedFromChange}
              disabled={loading}
              timezone="UTC"
            />
            <DateTimePicker
              label="Created - To"
              value={createdTo}
              onChange={handleCreatedToChange}
              disabled={loading}
              timezone="UTC"
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

export default TaskFormLayout;
