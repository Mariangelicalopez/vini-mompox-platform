package com.vinimompox.products.application.service;

import com.vinimompox.products.domain.port.repository.ProductServicePort;
import com.vinimompox.products.domain.model.Product;
import com.vinimompox.products.domain.port.repository.ProductRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del puerto de servicio de productos.
 * Esta clase contiene la lógica de negocio para gestionar los productos.
 */
@Service // Indica que esta clase es un componente de servicio de Spring
public class ProductService implements ProductServicePort {

    private final ProductRepositoryPort productRepository;

    public ProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Actualiza la información de un producto existente.
     * @param id El ID del producto a actualizar.
     * @param productDetails El objeto Product con la información actualizada.
     * @return El producto actualizado.
     * @throws RuntimeException Si el producto con el ID especificado no se encuentra.
     */
    @Override
    public Product updateProduct(Long id, Product productDetails) {
        // Buscar el producto existente por ID
        Optional<Product> optionalProduct = productRepository.findById(id);

        // Verificar si el producto existe
        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();

            // Actualizar TODOS los campos relevantes del producto existente
            // con los nuevos detalles de productDetails.
            existingProduct.setName(productDetails.getName());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setCategory(productDetails.getCategory());
            existingProduct.setVintage(productDetails.getVintage());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setStock(productDetails.getStock());

            // Guardar y devolver el producto actualizado
            return productRepository.save(existingProduct);
        } else {
            // Si el producto no se encuentra, lanzar una excepción.
            // Esta excepción será capturada por el ProductController, que devolverá un 404.
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
    }
}