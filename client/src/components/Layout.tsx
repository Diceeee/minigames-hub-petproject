import React from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from './contexts/AuthContext';
import styles from '../styles/Layout.module.css';
import { neonBlue, neonPink, darkBg, darkHeader } from '../constants/colors';
import { orbitron, fontSizeMedium, fontSizeXL } from '../constants/fonts';

const Layout: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div className={styles.layout}>
      <header className={styles.header}>
        {/* Left: Logo */}
        <div className={styles.logo}>
          <span className={styles.logoText}>
            <span role="img" aria-label="joystick">🎮</span> Minigames Hub
          </span>
        </div>
        {/* Center: Navigation */}
        <nav className={styles.nav}>
          <Link to="/" className={styles.navLink}>Home</Link>
          <Link to="/games" className={styles.navLink}>Games</Link>
          {user && user.registered && (
            <Link to="/dashboard" className={styles.navLink}>Dashboard</Link>
          )}
        </nav>
        {/* Right: Auth/Profile */}
        <div className={styles.auth}>
          {user ? (
            <>
              <span className={styles.welcome}>Hi, {user.username}</span>
              <button onClick={handleLogout} className={styles.logoutButton}>Logout</button>
            </>
          ) : (
            <>
              <Link to="/login" className={styles.loginLink}>Login</Link>
              <Link to="/register" className={styles.registerLink}>Register</Link>
            </>
          )}
        </div>
      </header>
      <main className={styles.main}>
        <Outlet />
      </main>
      <footer className={styles.footer}>
        &copy; {new Date().getFullYear()} Minigames Hub. All rights reserved.
      </footer>
    </div>
  );
};

export default Layout; 