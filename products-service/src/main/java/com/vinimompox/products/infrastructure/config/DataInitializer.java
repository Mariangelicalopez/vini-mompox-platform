package com.vinimompox.products.infrastructure.config;

import com.vinimompox.products.domain.model.Role;
import com.vinimompox.products.domain.model.User;
import com.vinimompox.products.infrastructure.adapter.out.persistence.RoleRepository;
import com.vinimompox.products.infrastructure.adapter.out.persistence.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    private static final String ROLE_ADMIN_NAME = "ADMIN";
    private static final String ROLE_USER_NAME = "USER";

    @Bean
    @Transactional
    public CommandLineRunner initData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // Aseguramos que los roles 'ADMIN' y 'USER' existan en la base de datos
            // antes de asignarlos a los usuarios.
            Role adminRole = roleRepository.findByName(ROLE_ADMIN_NAME)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role();
                        newAdminRole.setName(ROLE_ADMIN_NAME);
                        return roleRepository.save(newAdminRole);
                    });

            Role userRole = roleRepository.findByName(ROLE_USER_NAME)
                    .orElseGet(() -> {
                        Role newUserRole = new Role();
                        newUserRole.setName(ROLE_USER_NAME);
                        return roleRepository.save(newUserRole);
                    });

            // Creamos el usuario ADMIN si aún no existe
            if (userRepository.findByUsername("admin").isEmpty()) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("adminpass"));

                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(adminRole);
                adminRoles.add(userRole); // Un administrador a menudo también tiene privilegios de usuario estándar
                adminUser.setRoles(adminRoles);

                userRepository.save(adminUser);
                System.out.println("✅ Usuario ADMIN creado: admin/adminpass");
            }

            // Creamos el usuario USER si aún no existe
            if (userRepository.findByUsername("user").isEmpty()) {
                User normalUser = new User();
                normalUser.setUsername("user");
                normalUser.setPassword(passwordEncoder.encode("userpass"));

                Set<Role> normalUserRoles = new HashSet<>();
                normalUserRoles.add(userRole);
                normalUser.setRoles(normalUserRoles);

                userRepository.save(normalUser);
                System.out.println("✅ Usuario USER creado: user/userpass");
            }
        };
    }
}