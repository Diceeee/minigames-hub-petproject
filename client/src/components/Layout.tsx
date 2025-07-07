import React from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from './AuthContext';

const neonBlue = '#00eaff';
const darkBg = '#181a20';
const darkHeader = '#23263a';
const neonPink = '#ff00c8';

const Layout: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', background: darkBg }}>
      <header style={{ background: darkHeader, color: neonBlue, padding: '0.5rem 2rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', boxShadow: `0 2px 16px ${neonBlue}33` }}>
        {/* Left: Logo */}
        <div style={{ flex: '0 0 15%', display: 'flex', alignItems: 'center' }}>
          <span style={{ fontWeight: 'bold', fontSize: '1.7rem', letterSpacing: '2px', color: neonPink, textShadow: `0 0 8px ${neonPink}` }}>
            <span role="img" aria-label="joystick">🎮</span> Minigames Hub
          </span>
        </div>
        {/* Center: Navigation */}
        <nav style={{ flex: '0 0 70%', display: 'flex', justifyContent: 'center', gap: '2.5rem' }}>
          <Link to="/" style={{ color: neonBlue, textDecoration: 'none', fontWeight: 'bold', fontSize: '1.15rem', textShadow: `0 0 6px ${neonBlue}` }}>Home</Link>
          <Link to="/games" style={{ color: neonBlue, textDecoration: 'none', fontWeight: 'bold', fontSize: '1.15rem', textShadow: `0 0 6px ${neonBlue}` }}>Games</Link>
        </nav>
        {/* Right: Auth/Profile */}
        <div style={{ flex: '0 0 15%', display: 'flex', justifyContent: 'flex-end', alignItems: 'center', gap: '1rem' }}>
          {user ? (
            <>
              <span style={{ marginRight: '0.5rem', color: neonBlue, textShadow: `0 0 6px ${neonBlue}` }}>Hi, {user.username}</span>
              <button onClick={handleLogout} style={{ background: neonPink, color: 'white', border: 'none', borderRadius: '0.5rem', padding: '0.5rem 1rem', cursor: 'pointer', fontWeight: 'bold', boxShadow: `0 0 8px ${neonPink}` }}>Logout</button>
            </>
          ) : (
            <>
              <Link to="/login" style={{ color: neonBlue, textDecoration: 'none', fontWeight: 'bold', marginRight: '1rem', textShadow: `0 0 6px ${neonBlue}` }}>Login</Link>
              <Link to="/register" style={{ color: neonPink, textDecoration: 'none', fontWeight: 'bold', textShadow: `0 0 6px ${neonPink}` }}>Register</Link>
            </>
          )}
        </div>
      </header>
      <main style={{ flex: 1, padding: '2rem', background: darkBg }}>
        <Outlet />
      </main>
      <footer style={{ background: darkHeader, color: neonBlue, textAlign: 'center', padding: '1rem', boxShadow: `0 -2px 16px ${neonBlue}33` }}>
        &copy; {new Date().getFullYear()} Minigames Hub. All rights reserved.
      </footer>
    </div>
  );
};

export default Layout; 