import React from 'react';
import styles from '../../../styles/LifeSimulator.module.css';

interface StatsPanelProps {
  totalDollarsEarned: number;
  totalDollarsSpent: number;
  totalClicksDone: number;
}

const StatsPanel: React.FC<StatsPanelProps> = ({ totalDollarsEarned, totalDollarsSpent, totalClicksDone }) => (
  <div className={styles.statistics}>
    <h3>Statistics</h3>
    <ul className={styles.statsList}>
      <li>
        <span className={styles.statsLabel}>Total Dollars Earned:</span>
        <span className={styles.statsValue}>{totalDollarsEarned} $</span>
      </li>
      <li>
        <span className={styles.statsLabel}>Total Dollars Spent:</span>
        <span className={styles.statsValue}>{totalDollarsSpent} $</span>
      </li>
      <li>
        <span className={styles.statsLabel}>Total Clicks Done:</span>
        <span className={styles.statsValue}>{totalClicksDone}</span>
      </li>
    </ul>
  </div>
);

export default StatsPanel; 