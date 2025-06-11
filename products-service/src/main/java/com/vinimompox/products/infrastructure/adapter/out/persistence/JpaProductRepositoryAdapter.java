package com.vinimompox.products.infrastructure.adapter.out.persistence;

import com.vinimompox.products.domain.model.Product; // Importa tu entidad Product
import com.vinimompox.products.domain.port.repository.ProductRepositoryPort;
// No necesitas importar JpaRepository directamente aquí, ya que SpringDataProductRepository la extiende.
import org.springframework.stereotype.Component; // Para que Spring lo gestione como un componente

import java.util.List;
import java.util.Optional;

// Este es el "Adaptador de Salida" de nuestra arquitectura hexagonal.
// Implementa el puerto ProductRepositoryPort usando la tecnología específica (Spring Data JPA).
// Spring gestiona esta clase como un "Componente" y la inyectará donde se necesite ProductRepositoryPort.
@Component
public class JpaProductRepositoryAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository springDataProductRepository; // Inyecta la interfaz de Spring Data JPA

    // Constructor donde Spring inyecta automáticamente SpringDataProductRepository
    public JpaProductRepositoryAdapter(SpringDataProductRepository springDataProductRepository) {
        this.springDataProductRepository = springDataProductRepository;
    }

    @Override
    public Product save(Product product) {
        // Delegamos la operación al repositorio de Spring Data JPA.
        // save() manejará tanto la creación como la actualización si el ID ya existe.
        return springDataProductRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return springDataProductRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return springDataProductRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        springDataProductRepository.deleteById(id);
    }

    @Override // <--- ¡AÑADE ESTA ANOTACIÓN!
    public boolean existsById(Long id) { // <--- ¡AÑADE ESTE MÉTODO!
        // Delega la llamada al método existsById de Spring Data JPA
        return springDataProductRepository.existsById(id);
    }

    // NOTA: El método 'update(Product product)' HA SIDO ELIMINADO de este adaptador.
    // Esto es porque:
    // 1. Lo eliminamos de la interfaz ProductRepositoryPort en una corrección anterior.
    // 2. El método 'save(Product product)' ya maneja la funcionalidad de actualización
    //    cuando el objeto Product ya tiene un ID asignado, siguiendo las convenciones de Spring Data JPA.
}