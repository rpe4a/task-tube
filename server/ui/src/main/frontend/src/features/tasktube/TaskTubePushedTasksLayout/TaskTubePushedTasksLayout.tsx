import { Alert, AlertTitle, Box, Paper, Typography } from '@mui/material';
import { PushedTask } from '../../../pages/TaskTubePushPage/storage/PushedTasks';
import OpenInNewIcon from '@mui/icons-material/OpenInNew';
import { Link } from 'react-router';

interface TaskTubePushedTasksLayoutProps {
  tasks: PushedTask[];
  removeTask: (task: PushedTask) => void;
}

function TaskTubePushedTasksLayout(props: TaskTubePushedTasksLayoutProps) {
  const { tasks, removeTask } = props;
  return (
    <>
      <Box component="div" sx={{ pl: 3, height: '100%' }}>
        <Typography variant="h5" sx={{ mb: 2 }}>
          Pushed Tasks
        </Typography>
        {tasks.length === 0 && (
          <Paper elevation={2} sx={{ p: 3 }}>
            There aren't any pushed tasks yet.
          </Paper>
        )}
        {tasks.length > 0 && (
          <Box component="div" sx={{ overflow: 'auto', height: 'calc(100% - 40px)' }}>
            {tasks.map((task, i) => (
              <Alert
                severity="success"
                sx={{ mb: 1 }}
                onClose={() => {
                  removeTask(task);
                }}
                key={i}
              >
                <AlertTitle>{task.id}</AlertTitle>
                <Box sx={{ display: 'inline-flex', gap: 0.5, alignItems: 'start' }}>
                  <span style={{ wordBreak: 'break-word' }}>{task.name}</span>
                  <Link
                    to={`/tasktube/${task.correlationId}/tasks/${task.id}`}
                    title="Show"
                    target="_blank"
                  >
                    <OpenInNewIcon color="success" sx={{ fontSize: 17 }} />
                  </Link>
                </Box>
              </Alert>
            ))}
          </Box>
        )}
      </Box>
    </>
  );
}

export default TaskTubePushedTasksLayout;
