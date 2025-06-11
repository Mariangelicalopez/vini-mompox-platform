// products-frontend/src/index.js
import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css'; // Si no tienes un index.css personalizado, puedes removerlo.
import App from './App';
import reportWebVitals from './reportWebVitals'; // Para medir el rendimiento (opcional)

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

// Si quieres empezar a medir el rendimiento en tu app, pasa una función
// para registrar resultados (por ejemplo: reportWebVitals(console.log))
// o enviar a un endpoint de análisis. Más info: https://bit.ly/CRA-vitals
reportWebVitals();