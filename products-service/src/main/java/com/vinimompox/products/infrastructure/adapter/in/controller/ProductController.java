// products-service/src/main/java/com/vinimompox/products/infrastructure/adapter/in/ProductController.java
package com.vinimompox.products.infrastructure.adapter.in.controller;

import com.vinimompox.products.domain.model.Product; // Importa la entidad Product
import com.vinimompox.products.domain.port.repository.ProductServicePort;

import org.springframework.http.HttpStatus; // Para los códigos de estado HTTP (ej. 200 OK, 201 Created, 404 Not Found)
import org.springframework.http.ResponseEntity; // Para construir las respuestas HTTP
import org.springframework.web.bind.annotation.*; // Anotaciones generales para controladores REST
import jakarta.validation.Valid; // Para validar los objetos Product que recibimos en las solicitudes

import java.util.List; // Para manejar listas de productos

// Este es el "Adaptador de Entrada" de nuestra arquitectura hexagonal.
// Se encarga de exponer la funcionalidad de nuestro servicio de aplicación
// a través de una API REST que puede ser consumida por otras aplicaciones (frontend, otros microservicios).
@RestController // Le dice a Spring que esta clase es un controlador REST y manejará solicitudes HTTP.
@RequestMapping("/api/products") // Define la ruta base para todos los endpoints de este controlador.
                                 // Por ejemplo, para acceder a la API de productos, la URL comenzará con /api/products.
public class ProductController {

    private final ProductServicePort productServicePort; // Declaramos una dependencia al puerto de servicio

    // Constructor donde Spring inyecta automáticamente la implementación de ProductServicePort
    // (Spring encontrará ProductService porque está anotado con @Service)
    public ProductController(ProductServicePort productServicePort) {
        this.productServicePort = productServicePort;
    }

    // Endpoint para crear un nuevo producto
    // Mapea las solicitudes HTTP POST a la URL base /api/products
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        // @Valid: Activa las validaciones definidas en la entidad Product (si las tuviera, ej. @NotNull, @Size)
        // @RequestBody: Le indica a Spring que el cuerpo de la solicitud HTTP (en formato JSON) debe ser convertido a un objeto Product.
        Product createdProduct = productServicePort.createProduct(product); // Llama al servicio para crear el producto
        // Retorna el producto creado junto con un código de estado HTTP 201 (Created)
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // Endpoint para obtener un producto por su ID
    // Mapea las solicitudes HTTP GET a la URL /api/products/{id} (donde {id} es una variable de ruta)
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        // @PathVariable: Extrae el valor del {id} de la URL y lo asigna a la variable 'id'.
        return productServicePort.getProductById(id) // Llama al servicio para buscar el producto
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK)) // Si el producto se encuentra, retorna 200 OK con el producto
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Si no se encuentra, retorna 404 Not Found
    }

    // Endpoint para obtener todos los productos
    // Mapea las solicitudes HTTP GET a la URL base /api/products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productServicePort.getAllProducts(); // Llama al servicio para obtener todos los productos
        // Retorna la lista de productos junto con un código de estado HTTP 200 OK
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Endpoint para actualizar un producto existente por su ID
    // Mapea las solicitudes HTTP PUT a la URL /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        try {
            // Llama al servicio para actualizar el producto. Si el ID no existe, el servicio lanzará una excepción.
            Product updatedProduct = productServicePort.updateProduct(id, productDetails);
            // Si la actualización es exitosa, retorna el producto actualizado con un código de estado HTTP 200 OK
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Si el servicio lanza una excepción (ej. producto no encontrado), retorna 404 Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para eliminar un producto por su ID
    // Mapea las solicitudes HTTP DELETE a la URL /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productServicePort.deleteProduct(id); // Llama al servicio para eliminar el producto
        // Retorna un código de estado HTTP 204 (No Content) para indicar que la operación fue exitosa pero no hay contenido que devolver.
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}