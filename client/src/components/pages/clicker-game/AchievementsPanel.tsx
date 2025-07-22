import React from 'react';
import styles from '../../../styles/LifeSimulator.module.css';
import type { AchievementResponse, AchievementState } from '../../../types/gameClickerTypes';

interface AchievementsPanelProps {
  achievements: AchievementResponse[];
  achievementStates?: Map<string, AchievementState>;
  getAchievementProgress: (ach: AchievementResponse) => number;
  getAchievementTarget: (ach: AchievementResponse) => number;
  isAchievementDone: (ach: AchievementResponse) => boolean;
  getAchievementProgressRatio: (ach: AchievementResponse) => number;
}

const AchievementsPanel: React.FC<AchievementsPanelProps> = ({ achievements, achievementStates, getAchievementProgress, getAchievementTarget, isAchievementDone, getAchievementProgressRatio }) => (
  <div className={styles.achievements}>
    <h3>Achievements</h3>
    <ul className={styles.achievementsList}>
      {achievements.map(ach => {
        const done = isAchievementDone(ach);
        const progress = getAchievementProgress(ach);
        const target = getAchievementTarget(ach);
        const ratio = getAchievementProgressRatio(ach);
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
              <span className={styles.achievementName}>{ach.target.name}</span>
              {done && <span className={styles.achievementCheck}>✔</span>}
            </div>
            <div className={styles.achievementDescription}>{ach.target.description}</div>
            {typeof progress === 'number' && typeof target === 'number' && target > 1 && (
              <div className={styles.achievementProgressBarWrapper}>
                <div className={styles.achievementProgressBarBg}>
                  <div
                    className={styles.achievementProgressBar}
                    style={{ width: `${Math.min(100, Math.round(ratio * 100))}%` }}
                  />
                </div>
                <span className={styles.achievementProgressText}>
                  {progress} / {target}
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