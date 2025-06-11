import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './App.css'; // Asegúrate de que las clases CSS estén disponibles

const API_BASE_URL = 'http://localhost:8081/api'; // ¡IMPORTANTE: Asegúrate de que este puerto sea el de tu backend de Spring Boot!

function RegisterPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState(''); // ¡NUEVO: Estado para la confirmación de contraseña!
    const [message, setMessage] = useState(''); // Para mostrar mensajes de éxito o error
    const [error, setError] = useState(''); // Para mensajes de error específicos
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        setMessage('');
        setError('');

        // --- Validación del frontend: Las contraseñas deben coincidir ---
        if (password !== confirmPassword) {
            setError('Las contraseñas no coinciden.');
            return; // Detiene la ejecución si las contraseñas no coinciden
        }

        try {
            const response = await axios.post(`${API_BASE_URL}/auth/register`, {
                username,
                password,
                confirmPassword // ¡NUEVO: Envía el campo confirmPassword al backend!
            });

            // Si la respuesta es exitosa (código 2xx)
            setMessage(response.data); // Asume que el backend devuelve un String como "Usuario registrado exitosamente..."
            setUsername('');
            setPassword('');
            setConfirmPassword(''); // Limpia también el campo de confirmación
            alert('¡Registro exitoso! Ahora puedes iniciar sesión.');
            navigate('/'); // Redirige al usuario a la página de inicio de sesión
        } catch (err) {
            console.error('Error al registrar usuario:', err);
            if (err.response) {
                // El servidor respondió con un estado fuera del rango 2xx
                // Si el backend devuelve un mensaje de error específico, lo usamos
                if (err.response.data && typeof err.response.data === 'string') {
                    setError(err.response.data);
                } else if (err.response.data && err.response.data.message) {
                    // Si el backend devuelve un objeto de error con un campo 'message'
                    setError(err.response.data.message);
                } else {
                    setError(`Error al registrar usuario: ${err.response.status} ${err.response.statusText}`);
                }
            } else if (err.request) {
                // La solicitud fue hecha pero no se recibió respuesta (ej. backend no está corriendo)
                setError('No se pudo conectar con el servidor. Verifica que el backend esté corriendo.');
            } else {
                // Algo más causó el error (ej. error en la configuración de Axios)
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
                {/* ¡NUEVO: Campo para confirmar contraseña! */}
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
                {/* Aseguramos que el botón ocupe todo el ancho del formulario */}
                <button type="submit" style={{ width: '100%' }}>Registrar</button>
            </form>
            {message && <p className="success-message">{message}</p>}
            {error && <p className="error-message">{error}</p>}
            <p className="login-link">
                ¿Ya tienes una cuenta? <Link to="/">Inicia Sesión</Link>
            </p>
        </div>
    );
}

export default RegisterPage;