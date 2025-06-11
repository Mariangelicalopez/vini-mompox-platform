// products-frontend/src/ProductsPage.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, Link, useLocation } from 'react-router-dom'; // Importa useLocation

// Define la URL base de la API aquí. Es mejor tenerla en un archivo de configuración o en las variables de entorno
// pero para este ejemplo, la dejamos aquí.
const API_BASE_URL = 'http://localhost:8081/api';

function ProductsPage({ isAuthenticated }) {
    // Estado para el formulario de creación de un nuevo producto
    const [newProduct, setNewProduct] = useState({
        name: '',
        description: '',
        category: '', // Añadido para consistencia con el backend
        vintage: null, // Añadido para consistencia con el backend
        price: null,
        stock: null
    });

    // Estado para el formulario de edición de un producto existente
    const [editingProduct, setEditingProduct] = useState(null);

    const navigate = useNavigate();
    const location = useLocation(); // Hook para acceder al objeto de ubicación de la ruta

    // Efecto para verificar la autenticación y redirigir si es necesario
    useEffect(() => {
        if (!isAuthenticated) {
            navigate('/'); // Redirige a la página de inicio de sesión si no está autenticado
        }
    }, [isAuthenticated, navigate]);

    // Efecto para precargar el formulario de edición si se navega desde ProductsTablePage
    useEffect(() => {
        // 'location.state' contendrá el objeto 'productToEdit' si se pasó al navegar
        if (location.state && location.state.productToEdit) {
            setEditingProduct(location.state.productToEdit);
            // Opcional: limpiar el estado de navegación para evitar que se cargue de nuevo
            // si el usuario recarga la página directamente en /products
            navigate(location.pathname, { replace: true, state: {} });
        }
    }, [location.state, location.pathname, navigate]);


    // Función para obtener los encabezados de autenticación (Basic Auth)
    const getAuthHeaders = () => {
        const storedUsername = localStorage.getItem('username');
        const storedPassword = localStorage.getItem('password');
        if (!storedUsername || !storedPassword) {
            // Si no hay credenciales, asume que no está autenticado y delega la redirección al useEffect
            return null;
        }
        return {
            auth: {
                username: storedUsername,
                password: storedPassword
            }
        };
    };

    // Maneja los cambios en los campos del formulario de NUEVO producto
    const handleNewProductChange = (e) => {
        const { name, value } = e.target;
        setNewProduct(prev => ({
            ...prev,
            [name]: (name === 'price' || name === 'stock' || name === 'vintage')
                        ? (value === '' ? null : Number(value)) // Convierte a número o null si está vacío
                        : value
        }));
    };

    // Maneja los cambios en los campos del formulario de EDICIÓN de producto
    const handleEditProductChange = (e) => {
        const { name, value } = e.target;
        setEditingProduct(prev => ({
            ...prev,
            [name]: (name === 'price' || name === 'stock' || name === 'vintage')
                        ? (value === '' ? null : Number(value)) // Convierte a número o null si está vacío
                        : value
        }));
    };

    // Envía el formulario para crear un nuevo producto
    const handleCreateProduct = async (e) => {
        e.preventDefault(); // Previene el comportamiento por defecto del formulario

        const authHeaders = getAuthHeaders();
        if (!authHeaders) {
            alert('No autenticado. Por favor, inicia sesión.');
            return;
        }

        // Validaciones básicas antes de enviar
        if (!newProduct.name || !newProduct.description || !newProduct.category || newProduct.price === null || newProduct.stock === null || newProduct.vintage === null) {
            alert('Todos los campos (nombre, descripción, categoría, cosecha, precio, stock) son obligatorios.');
            return;
        }
        if (isNaN(newProduct.price) || isNaN(newProduct.stock) || isNaN(newProduct.vintage)) {
            alert('Precio, Stock y Cosecha deben ser números válidos.');
            return;
        }

        try {
            // Asegúrate de que los números se envíen como números, no como strings vacíos si son null
            const productToSend = {
                ...newProduct,
                price: newProduct.price,
                stock: newProduct.stock,
                vintage: newProduct.vintage
            };
            await axios.post(`${API_BASE_URL}/products`, productToSend, authHeaders);
            setNewProduct({ name: '', description: '', category: '', vintage: null, price: null, stock: null }); // Limpia el formulario
            alert('Producto creado exitosamente!');
            navigate('/products-list'); // Redirige a la lista de productos
        } catch (err) {
            alert('Error al crear producto: ' + (err.response?.data?.message || err.message || 'Error desconocido'));
            console.error('Error creating product:', err.response || err);
        }
    };

    // Envía el formulario para actualizar un producto existente
    const handleUpdateProduct = async (e) => {
        e.preventDefault(); // Previene el comportamiento por defecto del formulario

        const authHeaders = getAuthHeaders();
        // Asegúrate de que hay un producto en edición y un ID válido
        if (!authHeaders || !editingProduct || !editingProduct.id) {
            alert('No autenticado o no hay producto seleccionado para actualizar.');
            return;
        }

        // Validaciones básicas
        if (!editingProduct.name || !editingProduct.description || !editingProduct.category || editingProduct.price === null || editingProduct.stock === null || editingProduct.vintage === null) {
            alert('Todos los campos (nombre, descripción, categoría, cosecha, precio, stock) son obligatorios.');
            return;
        }
        if (isNaN(editingProduct.price) || isNaN(editingProduct.stock) || isNaN(editingProduct.vintage)) {
            alert('Precio, Stock y Cosecha deben ser números válidos.');
            return;
        }


        try {
            const productToSend = {
                ...editingProduct,
                price: editingProduct.price,
                stock: editingProduct.stock,
                vintage: editingProduct.vintage
            };
            // Usa el método PUT con el ID del producto
            await axios.put(`${API_BASE_URL}/products/${productToSend.id}`, productToSend, authHeaders);
            setEditingProduct(null); // Limpia el estado de edición
            alert('Producto actualizado exitosamente!');
            navigate('/products-list'); // Redirige a la lista de productos
        } catch (err) {
            alert('Error al actualizar producto: ' + (err.response?.data?.message || err.message || 'Error desconocido'));
            console.error('Error updating product:', err.response || err);
        }
    };

    // Cancela la edición y limpia el formulario de edición
    const cancelEditing = () => {
        setEditingProduct(null);
    };

    return (
        <div className="products-page-container">
            <h2>Gestionar de Vino</h2>

            {/* Formulario de EDICIÓN (se muestra si hay un producto en editingProduct) */}
            {editingProduct ? (
                <div className="form-section edit-section">
                    <h3>Editar vino (ID: {editingProduct.id})</h3>
                    <form onSubmit={handleUpdateProduct} className="product-form">
                        <div className="form-group">
                            <label>Nombre:</label>
                            <input type="text" name="name" value={editingProduct.name} onChange={handleEditProductChange} required />
                        </div>
                        <div className="form-group">
                            <label>Descripción:</label>
                            <input type="text" name="description" value={editingProduct.description} onChange={handleEditProductChange} />
                        </div>
                        <div className="form-group">
                            <label>Categoría:</label>
                            <input type="text" name="category" value={editingProduct.category} onChange={handleEditProductChange} required />
                        </div>
                        <div className="form-group">
                            <label>Cosecha (Vintage):</label>
                            <input type="number" name="vintage" value={editingProduct.vintage || ''} onChange={handleEditProductChange} required />
                        </div>
                        <div className="form-group">
                            <label>Precio:</label>
                            <input type="number" name="price" value={editingProduct.price || ''} onChange={handleEditProductChange} step="0.01" required />
                        </div>
                        <div className="form-group">
                            <label>Cantidad:</label>
                            <input type="number" name="stock" value={editingProduct.stock || ''} onChange={handleEditProductChange} required />
                        </div>
                        <button type="submit" className="submit-button">Guardar Cambios</button>
                        <button type="button" onClick={cancelEditing} className="cancel-button">Cancelar</button>
                    </form>
                </div>
            ) : (
                // Formulario de CREACIÓN (se muestra si no hay un producto en editingProduct)
                <div className="form-section create-section">
                    <h3>Añadir Nuevo Vino</h3>
                    <form onSubmit={handleCreateProduct} className="product-form">
                        <div className="form-group">
                            <label>Nombre:</label>
                            <input type="text" name="name" value={newProduct.name} onChange={handleNewProductChange} required />
                        </div>
                        <div className="form-group">
                            <label>Descripción:</label>
                            <input type="text" name="description" value={newProduct.description} onChange={handleNewProductChange} />
                        </div>
                        <div className="form-group">
                            <label>Categoría:</label>
                            <input type="text" name="category" value={newProduct.category} onChange={handleNewProductChange} required />
                        </div>
                        <div className="form-group">
                            <label>Cosecha (Vintage):</label>
                            <input type="number" name="vintage" value={newProduct.vintage || ''} onChange={handleNewProductChange} required />
                        </div>
                        <div className="form-group">
                            <label>Precio:</label>
                            <input type="number" name="price" value={newProduct.price || ''} onChange={handleNewProductChange} step="0.01" required />
                        </div>
                        <div className="form-group">
                            <label>Cantidad:</label>
                            <input type="number" name="stock" value={newProduct.stock || ''} onChange={handleNewProductChange} required />
                        </div>
                        <button type="submit" className="submit-button">Crear Vino</button>
                    </form>
                </div>
            )}

            <hr />

            {/* Enlace para ir a la lista de productos */}
            <p className="link-to-list">
                Puedes ver la lista completa de vinos y editar/eliminar desde allí:
                <Link to="/products-list" className="nav-link">Ir a la Lista de Vinos</Link>
            </p>
        </div>
    );
}

export default ProductsPage;