import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function ManageUsersPage({ API_BASE_URL, username, password }) {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    // Función para obtener todos los usuarios
    const fetchUsers = async () => {
        setLoading(true);
        setError(''); // Limpiar errores anteriores antes de cada solicitud
        try {
            const response = await axios.get(`${API_BASE_URL}/users`, {
                auth: {
                    username: username,
                    password: password
                }
            });
            setUsers(response.data);
        } catch (err) {
            console.error('Error al cargar usuarios:', err);
            let errorMessage = 'Error al cargar usuarios. Por favor, intenta de nuevo.';

            if (err.response) {
                // El servidor respondió con un código de estado que no es 2xx
                if (err.response.status === 403) {
                    errorMessage = 'Acceso denegado. No tienes permisos para ver esta página.';
                    // Dar un breve momento al usuario para leer el mensaje antes de redirigir
                    setTimeout(() => {
                        navigate('/'); // Redirigir al login si no tiene permisos de ADMIN
                    }, 2000);
                } else if (err.response.data && typeof err.response.data === 'string') {
                    errorMessage = err.response.data; // Si el backend envía un string directamente
                } else if (err.response.data && err.response.data.message) {
                    errorMessage = err.response.data.message; // Si el backend envía un objeto con 'message'
                } else {
                    errorMessage = `Error del servidor: ${err.response.status} ${err.response.statusText}`;
                }
            } else if (err.request) {
                // La solicitud fue hecha pero no se recibió respuesta (ej. el backend no está corriendo)
                errorMessage = 'No se pudo conectar con el servidor. Verifica que el backend esté ejecutándose.';
            } else {
                // Algo más causó el error (ej. un problema en la configuración de Axios)
                errorMessage = 'Error desconocido al cargar usuarios.';
            }

            setError(errorMessage);
        } finally {
            setLoading(false); // La carga ha terminado, independientemente del éxito o error
        }
    };

    // Función para eliminar un usuario
    const handleDeleteUser = async (userId) => {
        if (!window.confirm('¿Estás seguro de que quieres eliminar este usuario? Esta acción es irreversible.')) {
            return; // Si el usuario cancela, no hacemos nada
        }
        setError(''); // Limpiar errores anteriores de eliminación
        try {
            await axios.delete(`${API_BASE_URL}/users/${userId}`, {
                auth: {
                    username: username,
                    password: password
                }
            });
            alert('Usuario eliminado con éxito.'); // Considera reemplazar esto con un modal o notificación más elegante
            fetchUsers(); // Recargar la lista de usuarios para que el cambio se refleje
        } catch (err) {
            console.error('Error al eliminar usuario:', err);
            let errorMessage = 'Error al eliminar usuario. Por favor, intenta de nuevo.';

            if (err.response) {
                if (err.response.status === 403) {
                    errorMessage = 'No tienes permisos para eliminar usuarios.';
                } else if (err.response.data && typeof err.response.data === 'string') {
                    errorMessage = err.response.data;
                } else if (err.response.data && err.response.data.message) {
                    errorMessage = err.response.data.message;
                } else {
                    errorMessage = `Error del servidor: ${err.response.status} ${err.response.statusText}`;
                }
            } else if (err.request) {
                errorMessage = 'No se pudo conectar con el servidor para eliminar el usuario.';
            }
            setError(errorMessage);
        }
    };

    // Efecto para cargar usuarios cuando el componente se monta o las credenciales cambian
    useEffect(() => {
        // Solo intentamos cargar usuarios si tenemos un nombre de usuario y contraseña (es decir, el usuario está logueado)
        if (username && password) {
            fetchUsers();
        } else {
            // Si no hay credenciales, redirigir al login inmediatamente
            console.warn('No se encontraron credenciales de usuario. Redirigiendo al inicio de sesión.');
            navigate('/');
        }
    }, [username, password, API_BASE_URL, navigate]); // Dependencias: el efecto se ejecuta si estas cambian

    // --- Renderizado Condicional para Estados de la UI ---
    if (loading) {
        return (
            <div className="loading-container" style={{ textAlign: 'center', padding: '20px' }}>
                <p>Cargando usuarios...</p>
                {/* Aquí podrías añadir un spinner de carga para una mejor UX */}
            </div>
        );
    }

    if (error) {
        return (
            <div className="error-message-container" style={{ color: 'red', textAlign: 'center', padding: '20px', border: '1px solid red', borderRadius: '5px' }}>
                <h3>¡Error!</h3>
                <p>{error}</p>
                {error.includes('Acceso denegado') && (
                    <p>Serás redirigido a la página de inicio de sesión en breve.</p>
                )}
            </div>
        );
    }

    if (users.length === 0) {
        return (
            <div className="no-users-message" style={{ textAlign: 'center', padding: '20px' }}>
                <h2>Gestionar Usuarios</h2>
                <p>No hay usuarios para mostrar en este momento.</p>
            </div>
        );
    }

    return (
        <div className="manage-users-container" style={{ padding: '20px' }}>
            <h2>Gestionar Usuarios</h2>
            <table className="users-table" style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
                <thead>
                    <tr style={{ background: '#f2f2f2' }}>
                        <th style={{ padding: '10px', border: '1px solid #ddd', textAlign: 'left' }}>ID</th>
                        <th style={{ padding: '10px', border: '1px solid #ddd', textAlign: 'left' }}>Usuario</th>
                        <th style={{ padding: '10px', border: '1px solid #ddd', textAlign: 'left' }}>Roles</th>
                        <th style={{ padding: '10px', border: '1px solid #ddd', textAlign: 'left' }}>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map(user => (
                        <tr key={user.id} style={{ borderBottom: '1px solid #eee' }}> {/* Usa `user.id` como key, debe ser único */}
                            <td style={{ padding: '10px', border: '1px solid #ddd' }}>{user.id}</td>
                            <td style={{ padding: '10px', border: '1px solid #ddd' }}>{user.username}</td>
                            <td style={{ padding: '10px', border: '1px solid #ddd' }}>
                                {/* Mapea los roles. Asegúrate de que `user.roles` sea un array de objetos con `name` */}
                                {user.roles && user.roles.map(role => role.name).join(', ')}
                            </td>
                            <td style={{ padding: '10px', border: '1px solid #ddd' }}>
                                {/* Evitar que el usuario actualmente logueado (admin) pueda eliminarse a sí mismo */}
                                {user.username !== username && (
                                    <button
                                        onClick={() => handleDeleteUser(user.id)}
                                        className="delete-button"
                                        style={{ background: '#dc3545', color: 'white', border: 'none', padding: '8px 12px', borderRadius: '4px', cursor: 'pointer' }}
                                    >
                                        Eliminar
                                    </button>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default ManageUsersPage;