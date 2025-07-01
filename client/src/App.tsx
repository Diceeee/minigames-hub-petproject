import React, { useState } from 'react';
import './App.css';

function App() {
  const [count, setCount] = useState(0);

  return (
    <div className="App">
      <header className="App-header">
        <h1>Welcome to the Test Page!</h1>
        <p>This is a simple React + TypeScript example.</p>
        <button onClick={() => setCount(count + 1)}>
          You clicked {count} times
        </button>
      </header>
    </div>
  );
}

export default App;
