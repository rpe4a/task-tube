import { QueryCache, QueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import { enqueueSnackbar } from 'notistack';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 1,
      retry: 2,
      retryDelay: 1000 * 1,
      refetchOnWindowFocus: false,
      refetchOnReconnect: true,
    },
  },
  queryCache: new QueryCache({
    onError: (error, _) => {
      const { message, status, code } = error as AxiosError;
      enqueueSnackbar(`Error fetching data: ${message} (status: ${status}, code: ${code})`, {
        variant: 'error',
      });
    },
  }),
});

export default queryClient;
