import {
  Box,
  Button,
  Paper,
  TextField,
  Typography,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Grid,
} from '@mui/material';
import { JSX, memo, useCallback, useState } from 'react';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { Dayjs } from 'dayjs';
import 'dayjs/locale/en-gb';
import { isUUID } from '../../../shared/utils/UuidUtils';
import { SearchTasksParams } from '../../../app/pages/TasksPage/TasksPage';

interface TasksFormProps {
  searchCreatedFrom: Dayjs | null;
  searchCreatedTo: Dayjs | null;
  searchId: string;
  searchName: string;
  searchTube: string;
  searchStatus: string;
  isLoading: boolean;
  searchTasks: (search: SearchTasksParams) => void;
  resetSearchTasksParams: () => void;
}

function TaskForm(props: TasksFormProps): JSX.Element {
  const {
    searchCreatedFrom,
    searchCreatedTo,
    searchId,
    searchName,
    searchTube,
    searchStatus,
    isLoading,
    searchTasks,
    resetSearchTasksParams,
  } = props;

  const [idError, setIdError] = useState<string>('');
  const [id, setId] = useState<string>(searchId || '');
  const [name, setName] = useState<string>(searchName || '');
  const [tube, setTube] = useState<string>(searchTube || '');
  const [status, setStatus] = useState<string>(searchStatus || '');
  const [createdFrom, setCreatedFromValue] = useState<Dayjs | null>(searchCreatedFrom);
  const [createdTo, setCreatedToValue] = useState<Dayjs | null>(searchCreatedTo);

  const handleIdChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;

    if (!value || isUUID(value)) {
      setIdError('');
      setId(value);
    } else {
      setIdError("The field 'id' is not valid.");
    }
  };

  const handleNameChange = useCallback((event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    setName(value);
  }, []);

  const handleTubeChange = useCallback((event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    setTube(value);
  }, []);

  const handleStatusChange = useCallback((value: string) => {
    setStatus(value);
  }, []);

  const handleCreatedFromChange = useCallback((value: Dayjs | null) => {
    setCreatedFromValue(value);
  }, []);

  const handleCreatedToChange = useCallback((value: Dayjs | null) => {
    setCreatedToValue(value);
  }, []);

  const handleSearchClick = useCallback(
    (event: React.MouseEvent<HTMLButtonElement>) => {
      event.preventDefault();

      searchTasks({ id, name, tube, status, createdFrom, createdTo });
    },
    [searchTasks, id, name, tube, status, createdFrom, createdTo],
  );

  const handleResetClick = useCallback(
    (event: React.MouseEvent<HTMLButtonElement>) => {
      event.preventDefault();
      setIdError('');
      setId('');
      setName('');
      setTube('');
      setStatus('');
      setCreatedFromValue(null);
      setCreatedToValue(null);
      setIdError('');

      resetSearchTasksParams();
    },
    [resetSearchTasksParams],
  );

  return (
    <>
      <Typography variant="h5" sx={{ mb: 2 }}>
        Search Executions
      </Typography>

      <Paper elevation={2} sx={{ p: 3, mb: 4 }}>
        <Grid container spacing={2} sx={{ mb: 3 }}>
          <Grid size={{ xs: 12, md: 6, lg: 3, xl: 3 }} component="form">
            <TextField
              fullWidth
              label="ID"
              name="id"
              value={id}
              onChange={handleIdChange}
              placeholder="e.g., c7a1b6f2-1234-5678-9abc-def012345678"
              sx={{ minWidth: 200 }}
              error={!!idError}
              helperText={idError}
            />
          </Grid>
          <Grid size={{ xs: 12, md: 6, lg: 4, xl: 4 }}>
            <TextField
              fullWidth
              label="Name"
              name="name"
              value={name}
              onChange={handleNameChange}
              placeholder="e.g., simple.task"
              sx={{ minWidth: 200 }}
            />
          </Grid>
          <Grid size={{ xs: 12, md: 6, lg: 3, xl: 3 }}>
            <TextField
              fullWidth
              label="Tube"
              name="tube"
              value={tube}
              onChange={handleTubeChange}
              placeholder="e.g., simple-tube"
              sx={{ minWidth: 200 }}
            />
          </Grid>
          <Grid size={{ xs: 12, md: 6, lg: 2, xl: 2 }}>
            <FormControl fullWidth sx={{ minWidth: 150 }}>
              <InputLabel>Status</InputLabel>
              <Select
                value={status}
                onChange={(e) => handleStatusChange(e.target.value as string)}
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
              timezone="UTC"
              slotProps={{ field: { clearable: true } }}
            />
            <DateTimePicker
              label="Created - To"
              name="createdTo"
              value={createdTo}
              onChange={handleCreatedToChange}
              timezone="UTC"
              slotProps={{ field: { clearable: true } }}
            />
          </LocalizationProvider>

          <Button
            variant="contained"
            size="large"
            onClick={handleSearchClick}
            loading={isLoading}
            title="Search tasktube"
          >
            SEARCH
          </Button>
          <Button
            variant="outlined"
            size="large"
            onClick={handleResetClick}
            title="Reset search form"
          >
            RESET FORM
          </Button>
        </Box>
      </Paper>
    </>
  );
}

export default memo(TaskForm);
