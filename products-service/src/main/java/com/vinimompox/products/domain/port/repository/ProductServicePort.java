package com.vinimompox.products.domain.port.repository; // ¡PAQUETE CORREGIDO AQUÍ!

import com.vinimompox.products.domain.model.Product; // Importa la entidad Product
import java.util.List; // Para manejar listas de productos
import java.util.Optional; // Para manejar casos donde un producto podría no encontrarse

/**
 * Este es el "Puerto de Entrada" de nuestra arquitectura hexagonal.
 * Define las operaciones de negocio que la aplicación (el servicio de productos)
 * expone y ofrece a los "adaptadores de entrada" (como el controlador REST, UI, etc.).
 * La capa de infraestructura (controlador) llamará a estos métodos.
 */
public interface ProductServicePort {

    // Operación para crear un nuevo producto.
    // Recibe el objeto Product con los datos a guardar y retorna el producto creado.
    Product createProduct(Product product);

    // Operación para obtener un producto por su ID.
    // Retorna un Optional<Product> porque el producto podría no existir.
    Optional<Product> getProductById(Long id);

    // Operación para obtener una lista de todos los productos.
    List<Product> getAllProducts();

    // Operación para actualizar un producto existente por su ID.
    // Recibe el ID del producto a actualizar y un objeto Product con los detalles a cambiar.
    // Retorna el producto actualizado.
    Product updateProduct(Long id, Product productDetails);

    // Operación para eliminar un producto por su ID.
    void deleteProduct(Long id);
}