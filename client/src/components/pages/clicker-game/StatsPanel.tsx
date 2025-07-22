import React from 'react';
import styles from '../../../styles/LifeSimulator.module.css';

interface StatsPanelProps {
  userStatistics?: import('../../../types/gameClickerTypes').UserStatisticsResponse;
}

const StatsPanel: React.FC<StatsPanelProps> = ({ userStatistics }) => (
  <div className={styles.statistics}>
    <h3>Statistics</h3>
    <ul className={styles.statsList}>
      <li>
        <span className={styles.statsLabel}>Total Dollars Earned:</span>
        <span className={styles.statsValue}>{userStatistics?.totalCurrencyEarned ?? '-' } $</span>
      </li>
      <li>
        <span className={styles.statsLabel}>Total Dollars Spent:</span>
        <span className={styles.statsValue}>{userStatistics?.totalCurrencySpent ?? '-' } $</span>
      </li>
      <li>
        <span className={styles.statsLabel}>Total Clicks Done:</span>
        <span className={styles.statsValue}>{userStatistics?.totalClicks ?? '-'}</span>
      </li>
    </ul>
  </div>
);

export default StatsPanel; 