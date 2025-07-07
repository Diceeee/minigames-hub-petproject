import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const neonBlue = '#00eaff';
const neonPink = '#ff00c8';
const darkBg = '#181a20';

const Register: React.FC = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(null);
    try {
      const res = await fetch('/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, email, password })
      });
      if (res.ok) {
        setSuccess('Registration successful! Please check your email to verify your account.');
        setTimeout(() => navigate('/login'), 2000);
      } else {
        const data = await res.json().catch(() => ({}));
        setError(data.message || 'Registration failed');
      }
    } catch (e) {
      setError('Network error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 400, margin: '2rem auto', padding: '2.5rem', background: darkBg, borderRadius: '1.2rem', boxShadow: `0 4px 32px ${neonBlue}33`, color: neonBlue, fontFamily: 'Orbitron, sans-serif', border: `2px solid ${neonPink}` }}>
      <h2 style={{ color: neonPink, textShadow: `0 0 8px ${neonPink}` }}>Register</h2>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '1rem' }}>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={e => setUsername(e.target.value)}
            required
            style={{ width: '100%', padding: '0.5rem', borderRadius: '0.5rem', border: `1px solid ${neonBlue}`, background: darkBg, color: neonBlue, fontWeight: 'bold', fontSize: '1.05rem' }}
          />
        </div>
        <div style={{ marginBottom: '1rem' }}>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={e => setEmail(e.target.value)}
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
        {success && <div style={{ color: neonBlue, marginBottom: '1rem', fontWeight: 'bold' }}>{success}</div>}
        <button type="submit" disabled={loading} style={{ width: '100%', padding: '0.75rem', background: neonPink, color: 'white', border: 'none', borderRadius: '0.5rem', fontSize: '1.1rem', fontWeight: 'bold', boxShadow: `0 0 8px ${neonPink}` }}>
          {loading ? 'Registering...' : 'Register'}
        </button>
      </form>
    </div>
  );
};

export default Register; 