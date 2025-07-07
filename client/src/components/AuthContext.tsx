import React, { createContext, useContext, useEffect, useState } from 'react';
import api from 'api/api';
import { API_BASE_URL } from 'api/urls';
import axios from 'axios';

type User = {
  id: string;
  username: string;
  email: string;
  // ...other fields
};

type AuthContextType = {
  user: User | null;
  loading: boolean;
  error: string | null;
  refresh: () => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchProfile = async (): Promise<void> => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.get<User>('/auth/user/me', { withCredentials: true });
      setUser(res.data);
    } catch (e: unknown) {
      if (axios.isAxiosError(e) && e.response) {
        setUser(null);
      } else {
        setError('Network error');
        setUser(null);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  useEffect(() => {
      api.get("/csrf");
  }, []);

  const refresh = fetchProfile;
  const logout = async () => {
      await api.post('auth/refresh/logout', null, { withCredentials: true });
      setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, error, refresh, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}; 