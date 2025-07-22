import React from 'react';
import styles from '../styles/Home.module.css';

const Home: React.FC = () => (
  <div className={styles.homeContainer}>
    <h1 className={styles.title}>Welcome to Minigames Hub!</h1>
    <p className={styles.description}>
      <b>Minigames Hub</b> is your <span className={styles.highlight}>online playground</span> for <span className={styles.highlight}>fun</span>, <span className={styles.highlight}>competition</span>, and <span className={styles.highlight}>rewards</span>!<br /><br />
      🎮 Play a growing collection of unique minigames, from clickers to strategy and luck-based games.<br />
      🏆 Earn achievements.<br />
      💰 Collect rewards, track your statistics, and show off your progress.<br />
      🤝 Challenge friends or play solo—there's always something new to try!<br /><br />
      <span className={styles.highlight} style={{ fontWeight: 'bold' }}>Join now and start your journey to become the ultimate minigames champion!</span>
    </p>
  </div>
);

export default Home; 