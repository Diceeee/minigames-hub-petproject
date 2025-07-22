import React from 'react';
import styles from '../../../styles/LifeSimulator.module.css';

interface ClickerPanelProps {
  dollars: number;
  dollarsPerClick: number;
  dollarsPerMinute: number;
  onClick: () => void;
}

const ClickerPanel: React.FC<ClickerPanelProps> = ({ dollars, dollarsPerClick, dollarsPerMinute, onClick }) => (
  <div className={styles.leftPanel}>
    <button className={styles.hammerButton} onClick={onClick}>
      <span role="img" aria-label="hammer" className={styles.hammerIcon}>🔨</span>
    </button>
    <div className={styles.currencyInfo}>
      <div className={styles.currencyLabel}>Total Dollars:</div>
      <div className={styles.currencyValue}>{dollars} $</div>
      <div className={styles.currencyLabel}>Dollars per Click:</div>
      <div className={styles.currencyValue}>{dollarsPerClick} $</div>
      {/* TODO: uncomment when passive income feature will be implemented  */}
      {/*<div className={styles.currencyLabel}>Dollars per Minute:</div>*/}
      {/*<div className={styles.currencyValue}>{dollarsPerMinute} $</div>*/}
    </div>
  </div>
);

export default ClickerPanel; 