import React from 'react';

const neonBlue = '#00eaff';
const neonPink = '#ff00c8';
const darkBg = '#181a20';

const Home: React.FC = () => (
  <div style={{ maxWidth: 700, margin: '2rem auto', padding: '2.5rem', background: darkBg, borderRadius: '1.2rem', boxShadow: `0 4px 32px ${neonBlue}33`, textAlign: 'center', color: neonBlue, fontFamily: 'Orbitron, sans-serif', border: `2px solid ${neonPink}` }}>
    <h1 style={{ color: neonPink, fontSize: '2.5rem', textShadow: `0 0 12px ${neonPink}` }}>Welcome to Minigames Hub!</h1>
    <p style={{ fontSize: '1.25rem', margin: '2.5rem 0', textShadow: `0 0 8px ${neonBlue}` }}>
      <b>Minigames Hub</b> is your <span style={{ color: neonPink }}>online playground</span> for <span style={{ color: neonBlue }}>fun</span>, <span style={{ color: neonPink }}>competition</span>, and <span style={{ color: neonBlue }}>rewards</span>!<br /><br />
      🎮 Play a growing collection of unique minigames, from clickers to strategy and luck-based games.<br />
      🏆 Compete for high scores, earn achievements, and climb the leaderboards.<br />
      💰 Collect rewards, track your statistics, and show off your progress.<br />
      🤝 Challenge friends or play solo—there's always something new to try!<br /><br />
      <span style={{ color: neonPink, fontWeight: 'bold' }}>Join now and start your journey to become the ultimate minigames champion!</span>
    </p>
  </div>
);

export default Home; 