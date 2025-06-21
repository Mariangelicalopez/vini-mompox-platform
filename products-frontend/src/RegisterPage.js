// products-frontend/src/RegisterPage.js
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './App.css'; // Asegúrate de que las clases CSS estén disponibles

// ¡IMPORTANTE PARA DOCKER!
// La URL base de la API debe apuntar a la ruta /api en el mismo dominio del frontend,
// ya que Nginx (en el contenedor del frontend) se encargará de redirigirla al backend.
const API_BASE_URL = '/api'; 

function RegisterPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        setMessage('');
        setError('');

        // --- VALIDACIÓN DE CONTRASEÑA EN EL FRONTEND ---
        if (password.length < 8) {
            setError('La contraseña debe tener al menos 8 caracteres.');
            return; // Detiene la ejecución si la validación falla
        }
        if (password !== confirmPassword) {
            setError('Las contraseñas no coinciden.');
            return; // Detiene la ejecución si las contraseñas no coinciden
        }
        // --- FIN VALIDACIÓN FRONTEND ---

        try {
            // Se envía 'confirmPassword' al backend, ya que el backend también lo está validando.
            // Esto corrige el error 'Validation failed for argument [0]... on field 'confirmPassword': rejected value [null]'
            const response = await axios.post(`${API_BASE_URL}/auth/register`, {
                username,
                password,
                confirmPassword // AHORA SE INCLUYE EL CAMPO confirmPassword
            });

            // Asume que el backend devuelve un String como "Usuario registrado exitosamente..."
            setMessage('¡Registro exitoso! Ahora puedes iniciar sesión.'); 
            setUsername('');
            setPassword('');
            setConfirmPassword(''); // Limpia también el campo de confirmación
            navigate('/'); // Redirige al usuario a la página de inicio de sesión
        } catch (err) {
            console.error('Error al registrar usuario:', err);
            if (err.response) {
                // Si el backend devuelve un objeto de errores de validación (como { password: "mensaje" })
                if (err.response.data && typeof err.response.data === 'object') {
                    // Puedes unir todos los mensajes de error en uno solo o mostrarlos por campo
                    const backendErrors = Object.values(err.response.data).join('; ');
                    setError(backendErrors);
                }
                // Si el backend devuelve un mensaje de error específico en el cuerpo de la respuesta
                else if (err.response.data && typeof err.response.data === 'string') {
                    setError(err.response.data);
                } else if (err.response.data && err.response.data.message) {
                    setError(err.response.data.message);
                } else {
                    setError(`Error al registrar usuario: ${err.response.status} ${err.response.statusText}`);
                }
            } else if (err.request) {
                setError('No se pudo conectar con el servidor. Verifica que el backend esté corriendo.');
            } else {
                setError('Error desconocido al registrar usuario.');
            }
        }
    };

    return (
        <div className="login-container">
            <h2>Crear Usuario y Contraseña</h2>
            <form onSubmit={handleRegister}>
                <div className="form-group">
                    <label htmlFor="reg-username">Usuario:</label>
                    <input
                        type="text"
                        id="reg-username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="reg-password">Contraseña:</label>
                    <input
                        type="password"
                        id="reg-password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                {/* Campo para confirmar contraseña */}
                <div className="form-group">
                    <label htmlFor="reg-confirm-password">Confirmar Contraseña:</label>
                    <input
                        type="password"
                        id="reg-confirm-password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" style={{ width: '100%' }}>Registrar</button>
            </form>
            {message && <p className="success-message">{message}</p>}
            {error && <p className="error-message">{error}</p>} {/* Muestra el error aquí */}
            <p className="login-link">
                ¿Ya tienes una cuenta? <Link to="/">Inicia Sesión</Link>
            </p>
        </div>
    );
}

export default RegisterPage;
