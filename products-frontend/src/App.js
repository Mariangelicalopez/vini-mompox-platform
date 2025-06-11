// products-frontend/src/App.js
import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './App.css';
import ProductsTablePage from './ProductsTablePage';
import ProductsPage from './ProductsPage';
import RegisterPage from './RegisterPage'; 

const API_BASE_URL = 'http://localhost:8081/api'; 

function AppContent() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        try {
            // Intenta autenticar contra /api/products o cualquier otro endpoint seguro
            // Usamos una ruta protegida para verificar la autenticación
            await axios.get(`${API_BASE_URL}/products`, {
                auth: {
                    username: username,
                    password: password
                }
            });
            localStorage.setItem('username', username);
            localStorage.setItem('password', password);
            setIsAuthenticated(true);
            setError('');
            alert('Login exitoso!');
            navigate('/products'); // O a donde quieras después del login
        } catch (err) {
            setError('Credenciales inválidas. Intenta de nuevo.');
            console.error('Login error:', err);
            setIsAuthenticated(false);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('username');
        localStorage.removeItem('password');
        setIsAuthenticated(false);
        setUsername('');
        setPassword('');
        alert('Sesión cerrada.');
        navigate('/');
    };

    useEffect(() => {
        const storedUsername = localStorage.getItem('username');
        const storedPassword = localStorage.getItem('password');
        if (storedUsername && storedPassword) {
            axios.get(`${API_BASE_URL}/products`, { // Intenta validar la sesión existente
                auth: {
                    username: storedUsername,
                    password: storedPassword
                }
            })
            .then(() => {
                setIsAuthenticated(true);
                setUsername(storedUsername);
            })
            .catch(() => {
                localStorage.removeItem('username');
                localStorage.removeItem('password');
                setIsAuthenticated(false);
            });
        }
    }, []);

    return (
        <div className="App">
            <header className="App-header">
                <h1>VinoMompox</h1>
                <nav>
                    {isAuthenticated && (
                        <>
                            <Link to="/products"><button>Gestionar Vinos</button></Link>
                            <Link to="/products-list"><button style={{ marginLeft: '10px' }}>Ver Lista de Vinos</button></Link>
                            {/* Descomentar cuando OrdersPage.js esté listo */}
                            {/* <Link to="/orders"><button style={{ marginLeft: '10px' }}>Gestionar Pedidos</button></Link> */}
                            {/* Descomentar cuando SalesReportsPage.js esté listo */}
                            {/* <Link to="/reports"><button style={{ marginLeft: '10px' }}>Reportes de Ventas</button></Link> */}
                            <button onClick={handleLogout} style={{ marginLeft: '10px' }}>Cerrar Sesión</button>
                        </>
                    )}
                </nav>
            </header>

            <main>
                <Routes>
                    <Route path="/" element={
                        <HomePage
                            isAuthenticated={isAuthenticated}
                            username={username}
                            setUsername={setUsername}
                            password={password}
                            setPassword={setPassword}
                            handleLogin={handleLogin}
                            error={error}
                        />
                    } />
                    {/* ¡NUEVA RUTA para el registro! */}
                    <Route path="/register" element={<RegisterPage />} />
                    <Route
                        path="/products"
                        element={<ProductsPage isAuthenticated={isAuthenticated} API_BASE_URL={API_BASE_URL} />}
                    />
                    <Route
                        path="/products-list"
                        element={<ProductsTablePage isAuthenticated={isAuthenticated} API_BASE_URL={API_BASE_URL} />}
                    />
                    {/* Descomentar cuando OrdersPage.js esté listo */}
                    {/* <Route
                        path="/orders"
                        element={<OrdersPage isAuthenticated={isAuthenticated} API_BASE_URL={API_BASE_URL} />}
                    /> */}
                    {/* Descomentar cuando SalesReportsPage.js esté listo */}
                    {/* <Route
                        path="/reports"
                        element={<SalesReportsPage isAuthenticated={isAuthenticated} API_BASE_URL={API_BASE_URL} />}
                    /> */}
                </Routes>
            </main>
        </div>
    );
}

function App() {
    return (
        <Router>
            <AppContent />
        </Router>
    );
}

function HomePage({ isAuthenticated, username, setUsername, password, setPassword, handleLogin, error }) {
    return (
        <div>
            {!isAuthenticated ? (
                <div className="login-container">
                    <h2>Iniciar Sesión</h2>
                    <form onSubmit={handleLogin}>
                        <div className="form-group">
                            <label htmlFor="username">Usuario:</label>
                            <input
                                type="text"
                                id="username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="password">Contraseña:</label>
                            <input
                                type="password"
                                id="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>
                        {error && <p className="error-message">{error}</p>}
                        <button type="submit">Login</button>
                    </form>
                    {/* ¡NUEVO ENLACE para el registro! */}
                    <p className="register-link">
                        ¿No tienes una cuenta? <Link to="/register">Crear Usuario y Contraseña</Link>
                    </p>
                </div>
            ) : (
                <div>
                    <h2>¡Bienvenido!</h2>
                    <p>Redireccionando a la página de gestión de productos...</p>
                </div>
            )}
        </div>
    );
}

export default App;