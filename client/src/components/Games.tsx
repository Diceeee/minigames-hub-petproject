import React, {useState} from 'react';
import styles from '../styles/Games.module.css';
import {useNavigate} from 'react-router-dom';
import {useAuth} from './contexts/AuthContext';

const games = [
    {
        id: 'game-clicker',
        name: 'Clicker',
        released: true,
        description: (
            <>
                <b>Clicker</b> is an addictive clicker game where you need to earn money!<br/>
                💸 Click to earn money!<br/>
                🛒 Buy items, companies and other stuff to increase your money income!<br/>
                🏆 Get all achievements and become the richest oligarch in the world!
            </>
        ),
    },
    {
        id: 'death-roulette',
        name: 'Death Roulette Rock-Paper-Scissor',
        released: false,
        description: (
            <>
                <b>Death Roulette Rock-Paper-Scissor</b> is a thrilling twist on the classic game!<br/>
                ✊🖐✌ Play rock-paper-scissors against the computer.<br/>
                💀 Each time someone loses, the loser faces the deadly roulette.<br/>
                ☠️ Survive the roulette, or meet your doom. Last one standing wins!
            </>
        ),
    },
];

const Games: React.FC = () => {
    const [selected, setSelected] = useState(games[0]);
    const navigate = useNavigate();
    const {user} = useAuth();

    const handlePlay = () => {
        if (selected.id === 'game-clicker') {
            if (!user) {
                navigate('/login');
            } else {
                navigate('/games/game-clicker');
            }
        } else {
            // Placeholder for other games
            alert('Coming soon!');
        }
    };

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
                <button disabled={!selected.released} className={styles.playButton} onClick={handlePlay}>
                    {selected.released ? 'Play' : 'Work in progress'}
                </button>
            </div>
        </div>
    );
};

export default Games; 