import { IconButton } from '@mui/material';
import { closeSnackbar, SnackbarProvider } from 'notistack';
import DeleteIcon from '@mui/icons-material/Delete';

export const NotificationProvider = (props: React.PropsWithChildren<unknown>) => {
  return (
    <SnackbarProvider
      maxSnack={3}
      autoHideDuration={1000 * 1 * 30}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      action={(snackbarId) => (
        <IconButton
          onClick={() => closeSnackbar(snackbarId)}
          color="inherit"
          aria-label="delete"
          size="large"
          title="Delete"
        >
          <DeleteIcon />
        </IconButton>
      )}
    >
      {props.children}
    </SnackbarProvider>
  );
};
