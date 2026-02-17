import React from 'react';
import { Route, Routes } from 'react-router';
import Tasks from '../tasks/Tasks';

function App(): React.JSX.Element {
  return (
    <Routes>
      <Route index path="/" element={<Tasks />} />
    </Routes>
  );
}

export default App;
