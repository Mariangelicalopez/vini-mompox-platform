package com.vinimompox.products.application.service;

import com.vinimompox.products.domain.model.Role;
import com.vinimompox.products.domain.model.User;
import com.vinimompox.products.infrastructure.adapter.out.persistence.RoleRepository;
import com.vinimompox.products.infrastructure.adapter.out.persistence.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service // Marca esta clase como un componente de servicio de Spring
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository; // Inyectamos RoleRepository
    private final PasswordEncoder passwordEncoder;

    // Constructor para inyección de dependencias
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param username El nombre de usuario deseado.
     * @param password La contraseña del usuario (se encriptará).
     * @return El objeto User guardado en la base de datos.
     * @throws IllegalArgumentException Si el nombre de usuario ya existe o si el rol por defecto no se encuentra.
     */
    @Transactional // Asegura que toda la operación sea transaccional
    public User registerNewUser(String username, String password) {
        // 1. Validar la entrada (ej. que no sean nulos o vacíos si no lo haces con @Valid en el controller)
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }

        // 2. Verificar si el nombre de usuario ya existe
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario '" + username + "' ya está en uso.");
        }

        // 3. Buscar o crear el rol por defecto (ej. "USER")
        // Es crucial que este rol exista en tu base de datos (DataInitializer se encarga de esto)
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Error de configuración: El rol 'USER' no se encontró en la base de datos. Asegúrate de que DataInitializer lo cree."));

        // 4. Crear el nuevo usuario
        User newUser = new User();
        newUser.setUsername(username);
        // ¡Importante! Encriptar la contraseña antes de guardarla
        newUser.setPassword(passwordEncoder.encode(password));

        // Asignar el rol por defecto al nuevo usuario
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        newUser.setRoles(roles);

        // 5. Guardar el usuario en la base de datos
        return userRepository.save(newUser);
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username El nombre de usuario a buscar.
     * @return Un Optional que contiene el User si se encuentra, o vacío si no.
     */
    @Transactional(readOnly = true) // Método de solo lectura, mejora el rendimiento
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Puedes añadir más métodos aquí, como:
    // - findById(Long id)
    // - deleteUser(Long id)
    // - updateUserRoles(String username, Set<String> newRoleNames)
}