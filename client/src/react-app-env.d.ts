/// <reference types="react-scripts" />

// Add global mp3 module declaration
// Allows importing mp3 files as modules

declare module '*.mp3' {
  const src: string;
  export default src;
}
