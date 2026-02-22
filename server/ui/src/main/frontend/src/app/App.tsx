import React from 'react';
import { Route, Routes } from 'react-router';
import TasksPage from '../pages/TasksPage/TasksPage';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

const queryClient = new QueryClient();

function App(): React.JSX.Element {
  return (
    <QueryClientProvider client={queryClient}>
      <Routes>
        <Route index path="/" element={<TasksPage />} />
      </Routes>
    </QueryClientProvider>
  );
}

export default App;
