import React from 'react';
import { useAuth } from './contexts/AuthContext';
import styles from '../styles/Dashboard.module.css';

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  return (
    <div className={styles.dashboardContainer}>
      <h2>Welcome to your Dashboard{user ? `, ${user.username}` : ''}!</h2>
      <p>Here you can manage your profile, view your minigame stats, and more.</p>
    </div>
  );
};

export default Dashboard; 