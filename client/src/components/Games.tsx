import React, { useState } from 'react';

const neonBlue = '#00eaff';
const neonPink = '#ff00c8';
const darkBg = '#181a20';
const darkPanel = '#23263a';

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
    <div style={{ display: 'flex', maxWidth: 1000, margin: '2rem auto', background: darkBg, borderRadius: '1.2rem', boxShadow: `0 4px 32px ${neonBlue}33`, minHeight: 400, border: `2px solid ${neonPink}` }}>
      {/* Left panel: game list */}
      <div style={{ flex: '0 0 28%', borderRight: `2px solid ${neonBlue}`, padding: '2rem 1rem', background: darkPanel, borderTopLeftRadius: '1.2rem', borderBottomLeftRadius: '1.2rem' }}>
        <h3 style={{ marginBottom: '1.5rem', color: neonPink, textShadow: `0 0 8px ${neonPink}` }}>Games</h3>
        <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
          {games.map(game => (
            <li key={game.id} style={{ marginBottom: '1rem' }}>
              <button
                onClick={() => setSelected(game)}
                style={{
                  background: selected.id === game.id ? neonBlue : 'transparent',
                  color: selected.id === game.id ? darkBg : neonBlue,
                  border: `2px solid ${neonBlue}`,
                  borderRadius: '0.5rem',
                  padding: '0.75rem 1rem',
                  width: '100%',
                  textAlign: 'left',
                  fontWeight: 'bold',
                  cursor: 'pointer',
                  fontFamily: 'Orbitron, sans-serif',
                  fontSize: '1.1rem',
                  boxShadow: selected.id === game.id ? `0 0 12px ${neonBlue}` : undefined,
                  transition: 'background 0.2s, color 0.2s',
                }}
              >
                {game.name}
              </button>
            </li>
          ))}
        </ul>
      </div>
      {/* Right panel: game details */}
      <div style={{ flex: 1, padding: '2.5rem', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', color: neonBlue, fontFamily: 'Orbitron, sans-serif' }}>
        <h2 style={{ color: neonPink, marginBottom: '1.5rem', textShadow: `0 0 12px ${neonPink}` }}>{selected.name}</h2>
        <div style={{ fontSize: '1.15rem', marginBottom: '2rem', textAlign: 'center', textShadow: `0 0 8px ${neonBlue}` }}>{selected.description}</div>
        <button style={{ background: neonPink, color: 'white', border: 'none', borderRadius: '0.5rem', padding: '0.75rem 2.5rem', fontSize: '1.25rem', fontWeight: 'bold', cursor: 'pointer', boxShadow: `0 0 12px ${neonPink}` }}>
          Play
        </button>
      </div>
    </div>
  );
};

export default Games; 