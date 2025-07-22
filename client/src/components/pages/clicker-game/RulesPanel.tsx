import React from 'react';
import styles from '../../../styles/LifeSimulator.module.css';

interface RulesPanelProps {
    userStatistics?: import('../../../types/gameClickerTypes').UserStatisticsResponse;
}

const RulesPanel: React.FC<RulesPanelProps> = (props) => (
    <div className={styles.statistics}>
        <h3>Rules</h3>

        <div>
            1. Get money by working!<br/>
            <br/>
            2. Spend money by buying items that will increase your active and passive income!<br/>
            <br/>
            3. Complete all achievements!<br/>
        </div>
    </div>
);

export default RulesPanel;