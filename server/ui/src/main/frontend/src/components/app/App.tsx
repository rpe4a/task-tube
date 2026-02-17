import React from 'react';
import Tasks from '../tasks/Tasks';
import { Route, Routes } from 'react-router';

function App(): React.JSX.Element {
  return (
    <Routes>
      <Route path="/" element={<Tasks />} />
    </Routes>
  );
}

export default App;
