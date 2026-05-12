import React from 'react';
import { Route, Routes } from 'react-router';
import TasksPage from './pages/TasksPage/TasksPage';
import TaskTubePage from './pages/TaskTubePage/TaskTubePage';
import TaskTubePushPage from './pages/TaskTubePushPage/TaskTubePushPage';
import NotificationProvider from '../features/shared/notification/NotificationProvider';
import { QueryClientProvider } from '@tanstack/react-query';
import queryClient from '../shared/queryClient';

function App(): React.JSX.Element {
  return (
    <QueryClientProvider client={queryClient}>
      <NotificationProvider>
        <Routes>
          <Route index path="/" element={<TasksPage />} />
          <Route
            index
            path="/tasktube/:correlationIdParam/tasks/:taskIdParam"
            element={<TaskTubePage />}
          />
          <Route index path="/tasktube/push" element={<TaskTubePushPage />} />
        </Routes>
      </NotificationProvider>
    </QueryClientProvider>
  );
}

export default App;
