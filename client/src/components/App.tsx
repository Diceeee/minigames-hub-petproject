import React from 'react';
import {AuthProvider} from './contexts/AuthContext';
import {ApiProvider} from "./contexts/ApiContext";
import {ProtectedRoute} from './ProtectedRoute';
import {BrowserRouter, Routes, Route} from 'react-router-dom';
import Layout from './Layout';
import Login from './Login';
import Register from './Register';
import Dashboard from './Dashboard';
import Home from './Home';
import Games from './Games';

const NotFound = () => <div>404 - Page Not Found</div>;

function App() {
    return (
        <BrowserRouter>
            <ApiProvider>
                <AuthProvider>
                    <Routes>
                        <Route path="/" element={<Layout/>}>
                            <Route index element={<Home/>}/>
                            <Route path="login" element={<Login/>}/>
                            <Route path="register" element={<Register/>}/>
                            <Route path="dashboard" element={
                                <ProtectedRoute>
                                    <Dashboard/>
                                </ProtectedRoute>
                            }/>
                            <Route path="games" element={<Games/>}/>
                            <Route path="*" element={<NotFound/>}/>
                        </Route>
                    </Routes>
                </AuthProvider>
            </ApiProvider>
        </BrowserRouter>
    );
}

export default App;
