import { IconButton } from '@mui/material';
import { closeSnackbar, SnackbarProvider } from 'notistack';
import DeleteIcon from '@mui/icons-material/Delete';
import { memo } from 'react';

function NotificationProvider(props: React.PropsWithChildren<unknown>): React.JSX.Element {
  return (
    <SnackbarProvider
      maxSnack={3}
      autoHideDuration={1000 * 1 * 15}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      action={(snackbarId) => (
        <IconButton
          onClick={() => closeSnackbar(snackbarId)}
          color="inherit"
          size="medium"
          title="Delete"
        >
          <DeleteIcon />
        </IconButton>
      )}
    >
      {props.children}
    </SnackbarProvider>
  );
}

export default memo(NotificationProvider);
