// products-service/src/main/java/com/vinimompox/products/infrastructure/adapter/out/SpringDataProductRepository.java
package com.vinimompox.products.infrastructure.adapter.out.persistence;

import com.vinimompox.products.domain.model.Product; // Importa tu entidad Product
import org.springframework.data.jpa.repository.JpaRepository; // La interfaz mágica de Spring Data JPA
import org.springframework.stereotype.Repository; // Anotación para indicar que es un repositorio

// Esta interfaz es una extensión de JpaRepository.
// Spring Data JPA se encarga de crear automáticamente la implementación
// de los métodos CRUD (Crear, Leer, Actualizar, Borrar) y muchos más,
// simplemente con definir esta interfaz.
// <Product, Long> significa que trabajará con la entidad Product y que su ID es de tipo Long.
@Repository // Le dice a Spring que esta interfaz es un componente de tipo repositorio
public interface SpringDataProductRepository extends JpaRepository<Product, Long> {
    // Aquí no necesitamos añadir ningún método por ahora, JpaRepository ya nos da lo básico.
    // Más adelante, podríamos añadir métodos personalizados aquí si necesitamos consultas específicas
    // como: List<Product> findByNameContaining(String name);
}