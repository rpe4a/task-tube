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
} from '@mui/material';
import { JSX } from 'react';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { Dayjs } from 'dayjs';

interface TasksFormLayoutProps {
  customTube: string;
  createdAt: Dayjs | null;
  completedAt: Dayjs | null;
  searchId: string;
  searchName: string;
  searchTube: string;
  searchStatus: string;
  loading: boolean;
  handleCustomTubeChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleCreatedAtChange: (value: Dayjs | null) => void;
  handleCompletedAtChange: (value: Dayjs | null) => void;
  handleSearchIdChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleSearchNameChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleSearchTubeChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleSearchStatusChange: (event: SelectChangeEvent<string>) => void;
  handleFetchTasks: (createdAt?: Dayjs | null, completedAt?: Dayjs | null) => void;
  handleFetchAll: () => void;
}

function TaskFormLayout(props: TasksFormLayoutProps): JSX.Element {
  const {
    customTube,
    createdAt,
    completedAt,
    searchId,
    searchName,
    searchTube,
    searchStatus,
    loading,
    handleCustomTubeChange,
    handleCreatedAtChange,
    handleCompletedAtChange,
    handleSearchIdChange,
    handleSearchNameChange,
    handleSearchTubeChange,
    handleSearchStatusChange,
    handleFetchTasks,
    handleFetchAll,
  } = props;

  return (
    <>
      <Typography variant="h4" sx={{ mb: 3 }}>
        Search Executions
      </Typography>

      <Paper elevation={2} sx={{ p: 3, mb: 4 }}>
        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'flex-end', mb: 3 }}>
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

        <Typography variant="h6" sx={{ mb: 2 }}>
          Search Filters
        </Typography>

        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'flex-end' }}>
          <TextField
            label="Search by ID"
            value={searchId}
            onChange={handleSearchIdChange}
            placeholder="e.g., c7a1b6f2"
            sx={{ minWidth: 200 }}
            disabled={loading}
          />

          <TextField
            label="Search by Name"
            value={searchName}
            onChange={handleSearchNameChange}
            placeholder="e.g., Generate Report"
            sx={{ minWidth: 200 }}
            disabled={loading}
          />

          <TextField
            label="Search by Tube"
            value={searchTube}
            onChange={handleSearchTubeChange}
            placeholder="e.g., reporting"
            sx={{ minWidth: 200 }}
            disabled={loading}
          />

          <FormControl sx={{ minWidth: 150 }} disabled={loading}>
            <InputLabel>Status</InputLabel>
            <Select value={searchStatus} onChange={handleSearchStatusChange} label="Status">
              <MenuItem value="">All</MenuItem>
              <MenuItem value="PENDING">PENDING</MenuItem>
              <MenuItem value="PROCESSING">PROCESSING</MenuItem>
              <MenuItem value="COMPLETED">COMPLETED</MenuItem>
              <MenuItem value="ABORTED">ABORTED</MenuItem>
            </Select>
          </FormControl>
        </Box>
      </Paper>
    </>
  );
}

export default TaskFormLayout;
