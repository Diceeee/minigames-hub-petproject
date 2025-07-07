import React, { useState } from 'react';
import { useAuth } from './AuthContext';
import { useNavigate } from 'react-router-dom';
import api from "api/api";

const neonBlue = '#00eaff';
const neonPink = '#ff00c8';
const darkBg = '#181a20';

const GoogleLogo = () => (
  <svg width="24" height="24" viewBox="0 0 24 24" style={{ marginRight: 12 }}>
    <g>
      <path fill="#4285F4" d="M21.805 10.023h-9.765v3.977h5.617c-.242 1.242-1.469 3.648-5.617 3.648-3.375 0-6.125-2.789-6.125-6.25s2.75-6.25 6.125-6.25c1.922 0 3.211.773 3.953 1.438l2.703-2.625c-1.703-1.547-3.898-2.5-6.656-2.5-5.523 0-10 4.477-10 10s4.477 10 10 10c5.75 0 9.563-4.031 9.563-9.719 0-.656-.07-1.156-.156-1.719z"/>
      <path fill="#34A853" d="M3.545 7.545l3.273 2.402c.891-1.672 2.531-2.797 4.457-2.797 1.172 0 2.242.406 3.078 1.188l2.312-2.25c-1.406-1.297-3.219-2.078-5.39-2.078-3.672 0-6.75 2.984-6.75 6.75 0 1.078.25 2.094.672 2.984z"/>
      <path fill="#FBBC05" d="M12 22c2.438 0 4.484-.797 5.984-2.156l-2.828-2.312c-.797.547-1.828.875-3.156.875-2.438 0-4.5-1.641-5.25-3.844l-3.25 2.516c1.484 2.938 4.594 4.921 8.5 4.921z"/>
      <path fill="#EA4335" d="M21.805 10.023h-9.765v3.977h5.617c-.242 1.242-1.469 3.648-5.617 3.648-3.375 0-6.125-2.789-6.125-6.25s2.75-6.25 6.125-6.25c1.922 0 3.211.773 3.953 1.438l2.703-2.625c-1.703-1.547-3.898-2.5-6.656-2.5-5.523 0-10 4.477-10 10s4.477 10 10 10c5.75 0 9.563-4.031 9.563-9.719 0-.656-.07-1.156-.156-1.719z" opacity=".1"/>
    </g>
  </svg>
);

const Login: React.FC = () => {
  const { refresh } = useAuth();
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
      const res = await api.post('/auth/login', { principal, password }, {
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
      if (e.response && e.response.status === 401) {
        setError('Invalid credentials');
      } else {
        setError('Network error');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 400, margin: '2rem auto', padding: '2.5rem', background: darkBg, borderRadius: '1.2rem', boxShadow: `0 4px 32px ${neonBlue}33`, color: neonBlue, fontFamily: 'Orbitron, sans-serif', border: `2px solid ${neonPink}` }}>
      <h2 style={{ color: neonPink, textShadow: `0 0 8px ${neonPink}` }}>Login</h2>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '1rem' }}>
          <input
            type="text"
            placeholder="Username"
            value={principal}
            onChange={e => setPrincipal(e.target.value)}
            required
            style={{ width: '100%', padding: '0.5rem', borderRadius: '0.5rem', border: `1px solid ${neonBlue}`, background: darkBg, color: neonBlue, fontWeight: 'bold', fontSize: '1.05rem' }}
          />
        </div>
        <div style={{ marginBottom: '1rem' }}>
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
            style={{ width: '100%', padding: '0.5rem', borderRadius: '0.5rem', border: `1px solid ${neonBlue}`, background: darkBg, color: neonBlue, fontWeight: 'bold', fontSize: '1.05rem' }}
          />
        </div>
        {error && <div style={{ color: neonPink, marginBottom: '1rem', fontWeight: 'bold' }}>{error}</div>}
        <button type="submit" disabled={loading} style={{ width: '100%', padding: '0.75rem', background: neonPink, color: 'white', border: 'none', borderRadius: '0.5rem', fontSize: '1.1rem', fontWeight: 'bold', boxShadow: `0 0 8px ${neonPink}` }}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>
      <div style={{ margin: '2rem 0', borderBottom: `1px solid ${neonBlue}55`, textAlign: 'center', position: 'relative' }}>
        <span style={{ position: 'relative', top: 12, background: darkBg, padding: '0 1rem', color: neonBlue, fontWeight: 'bold', fontSize: '1rem' }}>or</span>
      </div>
      <a href="http://localhost:9000/auth/oauth2/authorization/google" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'white', color: '#222', border: 'none', borderRadius: '0.5rem', padding: '0.75rem', fontWeight: 'bold', fontSize: '1.1rem', boxShadow: `0 0 8px ${neonBlue}`, textDecoration: 'none', transition: 'background 0.2s', cursor: 'pointer' }}>
        <GoogleLogo />
        Login with Google
      </a>
    </div>
  );
};

export default Login; 