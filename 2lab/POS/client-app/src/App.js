import React from 'react';
import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Login } from './components/Login';
import { EmployeeHome } from './pages/EmployeeHome';
import { AdminHome } from './pages/AdminHome';
import { OwnerHome } from './pages/OwnerHome';

function App() {
  return (
      <div className="App">
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Login />} />
            <Route path="/employee-home" element={<EmployeeHome />} />
            <Route path={"/admin-home"} element={<AdminHome />} />
            <Route path={"/owner-home"} element={<OwnerHome />} />
          </Routes>
        </BrowserRouter>
      </div>
  );
}

export default App;
