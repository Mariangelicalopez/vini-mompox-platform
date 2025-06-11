// src/main/java/com/vinomompox/products/domain/repository/RoleRepository.java

package com.vinimompox.products.infrastructure.adapter.out.persistence;

import com.vinimompox.products.domain.model.Role; // Asegúrate de que la ruta a tu entidad Role sea correcta
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Este método nos permitirá buscar un Role por su nombre
    Optional<Role> findByName(String name);
}