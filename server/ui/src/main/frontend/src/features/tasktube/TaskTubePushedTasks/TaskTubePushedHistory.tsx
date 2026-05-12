import { Box, Paper, Typography, IconButton } from '@mui/material';
import { PushedTask } from './storage/PushedTasksHistory';
import OpenInNewIcon from '@mui/icons-material/OpenInNew';
import DeleteIcon from '@mui/icons-material/Delete';
import { memo } from 'react';
import IconLink from '../../../shared/component/IconLink';

interface TaskTubePushedHistoryProps {
  tasks: PushedTask[];
  removeTask: (task: PushedTask) => void;
}

function TaskTubePushedHistory(props: TaskTubePushedHistoryProps) {
  const { tasks, removeTask } = props;

  return (
    <>
      <Box component="div" sx={{ pl: 2, height: '100%' }}>
        <Typography variant="h5" sx={{ mb: 2, ml: 1 }}>
          History
        </Typography>
        {tasks.length === 0 && (
          <Paper elevation={2} sx={{ p: 3 }}>
            There aren't any pushed tasks yet.
          </Paper>
        )}
        {tasks.length > 0 && (
          <Box component="div" sx={{ pt: '2px', overflow: 'auto', height: 'calc(100% - 42px)' }}>
            {tasks.map((task, i) => (
              <Paper
                key={i}
                elevation={2}
                sx={{
                  p: 2,
                  mb: 2,
                  mx: 1,
                }}
              >
                <Box
                  sx={{
                    display: 'flex',
                    alignItems: 'flex-start',
                    justifyContent: 'space-between',
                  }}
                >
                  <Box sx={{ flex: 1, minWidth: 0 }}>
                    <Typography
                      variant="h6"
                      sx={{
                        fontWeight: 600,
                        mb: 1,
                        color: 'text.primary',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        whiteSpace: 'nowrap',
                      }}
                    >
                      {task.name}
                    </Typography>
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 1 }}>
                      <Typography variant="body2" sx={{ fontWeight: 500, color: 'text.primary' }}>
                        Tube:
                      </Typography>
                      <Typography
                        variant="body2"
                        sx={{
                          fontWeight: 600,
                          color: 'text.secondary',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          whiteSpace: 'nowrap',
                        }}
                      >
                        {task.tube}
                      </Typography>
                    </Box>
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 1 }}>
                      <Typography variant="body2" sx={{ fontWeight: 500, color: 'text.primary' }}>
                        ID:
                      </Typography>
                      <Typography
                        variant="body2"
                        sx={{
                          fontFamily: 'monospace',
                          bgcolor: 'grey.100',
                          px: 1,
                          py: 0.5,
                          borderRadius: 1,
                          fontSize: '0.8rem',
                        }}
                      >
                        {task.id}
                      </Typography>
                    </Box>
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                      <Typography variant="body2" sx={{ fontWeight: 500, color: 'text.primary' }}>
                        Correlation:
                      </Typography>
                      <Typography
                        variant="body2"
                        sx={{
                          fontFamily: 'monospace',
                          bgcolor: 'grey.100',
                          px: 1,
                          py: 0.5,
                          borderRadius: 1,
                          fontSize: '0.8rem',
                        }}
                      >
                        {task.correlationId}
                      </Typography>
                    </Box>
                  </Box>
                  <Box sx={{ display: 'flex', gap: 1, ml: 2 }}>
                    <IconLink
                      to={`/tasktube/${task.correlationId}/tasks/${task.id}`}
                      size="small"
                      title="View the task"
                    >
                      <OpenInNewIcon fontSize="medium" />
                    </IconLink>
                    <IconButton
                      onClick={() => removeTask(task)}
                      size="small"
                      title="Remove from history"
                      sx={{
                        color: 'primary.main',
                        '&:hover': {
                          bgcolor: 'primary.light',
                          color: 'white',
                          borderRadius: 1,
                        },
                      }}
                    >
                      <DeleteIcon fontSize="medium" />
                    </IconButton>
                  </Box>
                </Box>
              </Paper>
            ))}
          </Box>
        )}
      </Box>
    </>
  );
}

export default memo(TaskTubePushedHistory);
