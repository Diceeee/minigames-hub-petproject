import React from 'react';
import styles from '../../../styles/LifeSimulator.module.css';

interface BankPanelProps {
    userStatistics?: import('../../../types/gameClickerTypes').UserStatisticsResponse;
}

{/* TODO: finish  */}
const BankPanel: React.FC<BankPanelProps> = (props) => (
    <div className={styles.statistics}>
        <h3>Work in progress</h3>
    </div>
);

export default BankPanel;