// products-frontend/src/ProductsTablePage.js
import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './ProductsTablePage.css';

// Define la URL base de la API aquí.
// Es una buena práctica tenerla en un archivo de configuración o en variables de entorno para entornos de producción.
const API_BASE_URL = 'http://localhost:8081/api'; // Asegúrate de que esta URL sea correcta

function ProductsTablePage({ isAuthenticated }) {
    const [products, setProducts] = useState([]);
    const [message, setMessage] = useState('Cargando productos...');
    const navigate = useNavigate();

    // getAuthHeaders se envuelve en useCallback porque es una función interna
    // que se usa en otras funciones dependientes de Hooks (fetchProducts, handleDeleteProduct).
    // Su única dependencia es 'navigate'.
    const getAuthHeaders = useCallback(() => {
        const storedUsername = localStorage.getItem('username');
        const storedPassword = localStorage.getItem('password');
        if (!storedUsername || !storedPassword) {
            navigate('/'); // Redirigir si no hay credenciales
            return null;
        }
        return {
            auth: {
                username: storedUsername,
                password: storedPassword
            }
        };
    }, [navigate]); // 'navigate' es una dependencia porque se usa dentro de esta función.

    // fetchProducts se envuelve en useCallback para ser una dependencia estable de useEffect.
    // Sus dependencias son 'isAuthenticated', 'navigate' y 'getAuthHeaders'.
    const fetchProducts = useCallback(async () => {
        if (!isAuthenticated) {
            navigate('/');
            return;
        }

        const authHeaders = getAuthHeaders();
        if (!authHeaders) return;

        try {
            const response = await axios.get(`${API_BASE_URL}/products`, authHeaders);
            setProducts(response.data);
            setMessage('Vinos cargados exitosamente:');
        } catch (err) {
            if (err.response && err.response.status === 401) {
                setMessage('No autorizado. Por favor, inicia sesión de nuevo.');
                localStorage.removeItem('username');
                localStorage.removeItem('password');
                navigate('/');
            } else {
                setMessage('Error al cargar productos: ' + (err.message || 'Error desconocido'));
            }
            console.error('Error fetching products:', err);
        }
    }, [isAuthenticated, navigate, getAuthHeaders]); // Asegúrate de incluir todas las dependencias aquí.

    // useEffect para cargar productos cuando el componente se monta o cuando 'fetchProducts' cambia.
    // Gracias a useCallback, 'fetchProducts' solo cambiará si sus propias dependencias cambian.
    useEffect(() => {
        fetchProducts();
    }, [fetchProducts]); // 'fetchProducts' es la única dependencia aquí para evitar bucles infinitos.

    // handleEditClick no necesita useCallback porque no se pasa como dependencia a otros Hooks
    // y sus argumentos son estables (el 'product' se pasa en el onClick).
    const handleEditClick = (product) => {
        navigate('/products', { state: { productToEdit: product } });
    };

    // handleDeleteProduct se envuelve en useCallback porque llama a 'getAuthHeaders' y 'fetchProducts'.
    const handleDeleteProduct = useCallback(async (id) => {
        if (!window.confirm('¿Estás seguro de que quieres eliminar este producto?')) {
            return;
        }

        const authHeaders = getAuthHeaders();
        if (!authHeaders) {
            alert('No autenticado. Por favor, inicia sesión.');
            return;
        }

        try {
            await axios.delete(`${API_BASE_URL}/products/${id}`, authHeaders);
            alert('Producto eliminado exitosamente!');
            fetchProducts(); // Vuelve a cargar la lista de productos para reflejar el cambio
        } catch (err) {
            alert('Error al eliminar producto: ' + (err.response?.data?.message || err.message));
            console.error('Error deleting product:', err);
        }
    }, [getAuthHeaders, fetchProducts]); // Dependencias de handleDeleteProduct

    return (
        <div className="products-table-container">
            <h2>Lista de Vinos Existentes</h2>
            <p>{message}</p>
            {products.length > 0 ? (
                <table className="products-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Descripción</th>
                            <th>Precio</th>
                            <th>Cantidad</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {products.map(product => (
                            <tr key={product.id}>
                                <td>{product.id}</td>
                                <td>{product.name}</td>
                                <td>{product.description}</td>
                                <td>${product.price ? product.price.toFixed(2) : 'N/A'}</td>
                                <td>{product.stock}</td>
                                <td>
                                    <button
                                        className="edit-button"
                                        onClick={() => handleEditClick(product)}
                                    >
                                        Editar
                                    </button>
                                    <button
                                        className="delete-button"
                                        onClick={() => handleDeleteProduct(product.id)}
                                    >
                                        Eliminar
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                message.includes('cargando') ? <p>Cargando...</p> : <p>No hay vinos para mostrar.</p>
            )}
            <div className="table-actions">
                <button onClick={() => navigate('/products')}>Volver a Gestionar vino</button>
            </div>
        </div>
    );
}

export default ProductsTablePage;