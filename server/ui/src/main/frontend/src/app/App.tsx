import React from 'react';
import { Route, Routes } from 'react-router';
import TasksPage from '../pages/TasksPage/TasksPage';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import TaskTubePage from '../pages/TaskTubePage/TaskTubePage';
import TaskTubePushPage from '../pages/TaskTubePushPage/TaskTubePushPage';

const queryClient = new QueryClient();

function App(): React.JSX.Element {
  return (
    <QueryClientProvider client={queryClient}>
      <Routes>
        <Route index path="/" element={<TasksPage />} />
        <Route
          index
          path="/tasktube/:correlationIdParam/tasks/:taskIdParam"
          element={<TaskTubePage />}
        />
        <Route index path="/tasktube/push" element={<TaskTubePushPage />} />
      </Routes>
    </QueryClientProvider>
  );
}

export default App;
