import React, { useState } from 'react';
import styles from '../styles/Games.module.css';
import { neonBlue, neonPink, darkBg, darkPanel } from '../constants/colors';
import { orbitron, fontSizeMedium, fontSizeLarge } from '../constants/fonts';

const games = [
  {
    id: 'life-simulator',
    name: 'Life Simulator',
    description: (
      <>
        <b>Life Simulator</b> is an addictive clicker game!<br />
        💸 Click to earn money actively.<br />
        🏢 Invest in businesses and assets to start making money passively.<br />
        🛒 Buy upgrades, manage your empire, and climb the wealth leaderboard!
      </>
    ),
  },
  {
    id: 'death-roulette',
    name: 'Death Roulette Rock-Paper-Scissor',
    description: (
      <>
        <b>Death Roulette Rock-Paper-Scissor</b> is a thrilling twist on the classic game!<br />
        ✊🖐✌ Play rock-paper-scissors against the computer.<br />
        💀 Each time someone loses, the loser faces the deadly roulette.<br />
        ☠️ Survive the roulette, or meet your doom. Last one standing wins!
      </>
    ),
  },
];

const Games: React.FC = () => {
  const [selected, setSelected] = useState(games[0]);

  return (
    <div className={styles.gameContainer}>
      {/* Left panel: game list */}
      <div className={styles.gameList}>
        <h3 className={styles.gameListHeader}>Games</h3>
        <ul className={styles.gameListItems}>
          {games.map(game => (
            <li key={game.id} className={styles.gameListItem}>
              <button
                onClick={() => setSelected(game)}
                className={`${styles.gameButton} ${selected.id === game.id ? styles.selectedGameButton : ''}`}
              >
                {game.name}
              </button>
            </li>
          ))}
        </ul>
      </div>
      {/* Right panel: game details */}
      <div className={styles.gameDetails}>
        <h2 className={styles.gameDetailsHeader}>{selected.name}</h2>
        <div className={styles.gameDetailsDescription}>{selected.description}</div>
        <button className={styles.playButton}>
          Play
        </button>
      </div>
    </div>
  );
};

export default Games; 