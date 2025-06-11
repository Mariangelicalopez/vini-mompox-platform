package com.vinimompox.products.domain.port.repository;

import com.vinimompox.products.domain.model.Product; // Importa la clase Product
import java.util.List; // Para manejar listas de productos
import java.util.Optional; // Para manejar casos donde un producto podría no encontrarse

/**
 * Puerto de salida para el repositorio de productos (interfaz del lado secundario).
 * Define las operaciones CRUD que la capa de dominio espera de la persistencia de datos.
 */
public interface ProductRepositoryPort {

    /**
     * Guarda un producto nuevo o actualiza uno existente en la base de datos.
     * Retorna el producto guardado/actualizado (útil para obtener el ID si es nuevo).
     * El método `save()` de Spring Data JPA maneja tanto la creación como la actualización.
     * @param product El objeto Product a guardar o actualizar.
     * @return El producto persistido, con su ID generado si es nuevo.
     */
    Product save(Product product);

    /**
     * Busca un producto por su identificador único (ID).
     * @param id El ID del producto a buscar.
     * @return Un Optional que contiene el producto si se encuentra, o vacío si no.
     */
    Optional<Product> findById(Long id);

    /**
     * Recupera una lista de todos los productos disponibles en la base de datos.
     * @return Una lista de todos los productos.
     */
    List<Product> findAll();

    /**
     * Elimina un producto de la base de datos dado su identificador único (ID).
     * @param id El ID del producto a eliminar.
     */
    void deleteById(Long id);

    /**
     * Verifica si un producto con el ID dado existe en la base de datos.
     * @param id El ID del producto a verificar.
     * @return true si el producto existe, false en caso contrario.
     */
    boolean existsById(Long id); // <--- ¡ESTE MÉTODO ES NUEVO Y CRUCIAL!

    // NOTA: El método `Product update(Product product);` HA SIDO ELIMINADO de esta interfaz.
    // Esto es porque `ProductService` usa `save()` para las actualizaciones,
    // lo cual es la práctica estándar y más eficiente con Spring Data JPA.
    // Al eliminarlo aquí, evitamos conflictos y simplificamos la lógica.
}