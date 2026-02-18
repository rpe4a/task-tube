import React from 'react';
import { Route, Routes } from 'react-router';
import TasksPage from '../pages/TasksPage/TasksPage';

function App(): React.JSX.Element {
  return (
    <Routes>
      <Route index path="/" element={<TasksPage />} />
    </Routes>
  );
}

export default App;
