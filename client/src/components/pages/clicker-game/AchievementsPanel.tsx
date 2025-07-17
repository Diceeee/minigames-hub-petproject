import React from 'react';
import styles from '../../../styles/LifeSimulator.module.css';

interface Achievement {
  id: string;
  name: string;
  description: string;
  type: string;
  target: number;
}

interface AchievementsPanelProps {
  achievements: Achievement[];
  isAchievementDone: (ach: Achievement) => boolean;
  getAchievementProgress: (ach: Achievement) => number;
}

const AchievementsPanel: React.FC<AchievementsPanelProps> = ({ achievements, isAchievementDone, getAchievementProgress }) => (
  <div className={styles.achievements}>
    <h3>Achievements</h3>
    <ul className={styles.achievementsList}>
      {achievements.map(ach => {
        const done = isAchievementDone(ach);
        const progress = getAchievementProgress(ach);
        return (
          <li
            key={ach.id}
            className={
              done
                ? `${styles.achievementItem} ${styles.achievementDone}`
                : styles.achievementItem
            }
          >
            <div className={styles.achievementHeader}>
              <span className={styles.achievementName}>{ach.name}</span>
              {done && <span className={styles.achievementCheck}>✔</span>}
            </div>
            <div className={styles.achievementDescription}>{ach.description}</div>
            {ach.type === 'dollars' && (
              <div className={styles.achievementProgressBarWrapper}>
                <div className={styles.achievementProgressBarBg}>
                  <div
                    className={styles.achievementProgressBar}
                    style={{ width: `${(progress / ach.target) * 100}%` }}
                  />
                </div>
                <span className={styles.achievementProgressText}>
                  {progress} $ / {ach.target} $
                </span>
              </div>
            )}
          </li>
        );
      })}
    </ul>
  </div>
);

export default AchievementsPanel; 