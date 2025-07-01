import React from 'react';
import { AuthProvider } from './AuthContext';
import { ProtectedRoute } from './ProtectedRoute';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './Layout';
import Login from './Login';
import Register from './Register';
import EmailVerification from './EmailVerification';
import Dashboard from './Dashboard';
import Home from './Home';
import Refresh from './Refresh';
import Games from './Games';

const NotFound = () => <div>404 - Page Not Found</div>;

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Home />} />
            <Route path="login" element={<Login />} />
            <Route path="register" element={<Register />} />
            <Route path="dashboard" element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            } />
            <Route path="games" element={<Games />} />
            <Route path="email/verification/:tokenId" element={<EmailVerification />} />
            <Route path="refresh" element={<Refresh />} />
            <Route path="*" element={<NotFound />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
