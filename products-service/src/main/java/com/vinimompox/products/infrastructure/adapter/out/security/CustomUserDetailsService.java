package com.vinimompox.products.infrastructure.adapter.out.security;

import com.vinimompox.products.domain.model.User;
import com.vinimompox.products.infrastructure.adapter.out.persistence.UserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service // Marca esta clase como un componente de servicio de Spring
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Constructor para inyección de dependencias
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga los detalles de un usuario por su nombre de usuario para la autenticación.
     * Este método es la parte central de cómo Spring Security encuentra y autentica a tus usuarios.
     *
     * @param username El nombre de usuario que se intenta autenticar.
     * @return Un objeto UserDetails de Spring Security con la información del usuario y sus roles.
     * @throws UsernameNotFoundException Si el usuario con el nombre de usuario proporcionado no existe.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscar el usuario en tu base de datos
        // Si no se encuentra, lanzar una excepción específica de Spring Security
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username));

        // 2. Mapear los roles de tu entidad User a las autoridades de Spring Security
        // Cada rol de tu entidad Role (ej. "ADMIN", "USER") se convierte en una SimpleGrantedAuthority
        // Se le añade el prefijo "ROLE_" que es una convención común en Spring Security.
        Collection<? extends GrantedAuthority> authorities = user.getRoles().stream()
                // ¡CORRECCIÓN AQUÍ! Usamos role.getName() porque Role ahora es una entidad con un getter 'getName()'
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());

        // 3. Devolver un objeto UserDetails de Spring Security
        // Este objeto es utilizado por Spring Security para realizar la autenticación y autorización.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),      // Nombre de usuario
                user.getPassword(),      // Contraseña (Spring Security usará el PasswordEncoder para compararla)
                authorities              // Roles/Autoridades del usuario
        );
    }
}