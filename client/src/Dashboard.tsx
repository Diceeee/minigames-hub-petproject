import React from 'react';
import { useAuth } from './AuthContext';

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  return (
    <div style={{ maxWidth: 600, margin: '2rem auto', padding: '2rem', background: 'white', borderRadius: '1rem', boxShadow: '0 4px 24px rgba(0,0,0,0.08)' }}>
      <h2>Welcome to your Dashboard{user ? `, ${user.username}` : ''}!</h2>
      <p>Here you can manage your profile, view your minigame stats, and more.</p>
    </div>
  );
};

export default Dashboard; 