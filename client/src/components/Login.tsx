import React, { useState } from 'react';
import { useAuth } from './contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import styles from '../styles/Login.module.css';
import {ErrorCode} from "../api/types";
import {GoogleLogo} from "../constants/svg-logos";
import {useApi} from "./contexts/ApiContext";
import {API_PUBLIC_URL} from "../api/urls";

const Login: React.FC = () => {
  const { refresh } = useAuth();
  const { api } = useApi();
  const navigate = useNavigate();
  const [principal, setPrincipal] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const res = await api.post('/public/auth/login', { principal, password }, {
        withCredentials: true,
        headers: { 'Content-Type': 'application/json' }
      });
      if (res.status === 200) {
        await refresh();
        navigate('/dashboard');
      } else {
        setError('Invalid credentials');
      }
    } catch (e: any) {
      if (e.response && e.response.status === 400) {
        const errorCode: ErrorCode = e.response?.data?.errorCode;
        if (errorCode == ErrorCode.InvalidCredentials) {
          setError('Invalid credentials');
        } else {
          setError("Failed to login, try again")
        }
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.loginContainer}>
      <h2 className={styles.title}>Login</h2>
      <form onSubmit={handleSubmit}>
        <div className={styles.inputContainer}>
          <input
            type="text"
            placeholder="Username"
            value={principal}
            onChange={e => setPrincipal(e.target.value)}
            required
            className={styles.input}
          />
        </div>
        <div className={styles.inputContainer}>
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
            className={styles.input}
          />
        </div>
        {error && <div className={styles.error}>{error}</div>}
        <button type="submit" disabled={loading} className={styles.submitButton}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>
      <div className={styles.orLine}>
        <span className={styles.orText}>or</span>
      </div>
      <a href={`${API_PUBLIC_URL}/auth/oauth2/authorization/google`} className={styles.googleButton}>
        <GoogleLogo />
        Login with Google
      </a>
    </div>
  );
};

export default Login; 