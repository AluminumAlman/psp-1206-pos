import React from 'react';
import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import LogInPanel from './components/LogInPanel';

function App() {
  return (
      <div className="App">
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<LogInPanel />} />
          </Routes>
        </BrowserRouter>
      </div>
  );
}

export default App;
