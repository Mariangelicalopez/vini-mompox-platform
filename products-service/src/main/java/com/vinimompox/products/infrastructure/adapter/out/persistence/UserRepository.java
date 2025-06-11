// product-backend/src/main/java/com.vinimompox.products.repository.UserRepository.java
package com.vinimompox.products.infrastructure.adapter.out.persistence;

import com.vinimompox.products.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Indica que esta interfaz es un componente de repositorio de Spring
public interface UserRepository extends JpaRepository<User, Long> {

    // Método para buscar un usuario por su nombre de usuario
    // Spring Data JPA lo implementa automáticamente si el nombre del método sigue la convención
    Optional<User> findByUsername(String username);

    // Opcional: Para verificar si un nombre de usuario ya existe
    boolean existsByUsername(String username);
}