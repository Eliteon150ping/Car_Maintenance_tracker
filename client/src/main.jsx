import { StrictMode } from 'react';   // Used for development(It helps find potential issues and alerts you) 
import { createRoot } from 'react-dom/client'; // This gives React the ability to render your application into the browser.
import './index.css'; // Loads global styling
import App from './App.jsx'; // Imports your root component

{/*Find the HTML element called root and put the App component inside it, usually called 'index.html'*/}
createRoot(document.getElementById('root')).render( 
  <StrictMode>      
    <App />
  </StrictMode>,
)

/*

To create a react app for the frontend, type:

1) npm create my-react-app(older way) 
               OR
   npm create vite@latest .(the '.' is for the app to be created in the current folder, vite is a more modern quicker 
                            way to load and start a react app)

2) npm install(for libraries/packages)

3) choose React and javascript in the terminal prompt and make sure npm and node versions are up to date
   node -v(for node version)
   npm -v(for npm version)

4) npm run dev(start your app on a localhost port)

5) npm install react-router-dom(a web-specific NPM package that enables client-side routing in React web applications. 
                                It allows you to build Single Page Applications (SPAs) that change views and update 
                                the browser URL dynamically without triggering a full page reload.)
  

*/
