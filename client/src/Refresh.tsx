import React from 'react';

const Refresh: React.FC = () => (
  <div style={{ maxWidth: 400, margin: '2rem auto', padding: '2rem', background: 'white', borderRadius: '1rem', boxShadow: '0 4px 24px rgba(0,0,0,0.08)', textAlign: 'center' }}>
    <h2>Refreshing Session</h2>
    <div style={{ margin: '1rem 0' }}>
      <span role="img" aria-label="refresh" style={{ fontSize: '2rem' }}>🔄</span>
    </div>
    <div>Please wait while we refresh your session...</div>
  </div>
);

export default Refresh; 