package com.vinimompox.products.infrastructure.adapter.in.controller;

import com.vinimompox.products.domain.model.User; // Importa tu User de domain
import com.vinimompox.products.application.dto.RegisterRequest;
import com.vinimompox.products.application.service.UserService; // Importa tu UserService de application

import jakarta.validation.Valid; // Necesario para la validación del DTO
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Importa para UserDetails
import org.springframework.security.core.userdetails.UserDetails; // Importa para UserDetails
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Necesario para Collectors.toList()

@RestController
@RequestMapping("/api/auth") // Un prefijo común para endpoints de autenticación
@CrossOrigin(origins = "http://localhost:3000") // Asegúrate de que coincida con la URL de tu frontend
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register") // Endpoint para el registro de usuarios
    // Usa el DTO externo y activa la validación con @Valid
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // --- Lógica de validación de contraseñas duplicada del frontend ---
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return new ResponseEntity<>("Las contraseñas no coinciden.", HttpStatus.BAD_REQUEST);
        }

        try {
            User registeredUser = userService.registerNewUser(
                registerRequest.getUsername(),
                registerRequest.getPassword()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente: " + registeredUser.getUsername());
        } catch (RuntimeException e) {
            // Manejar caso donde el nombre de usuario ya existe (capturado por UserService)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Manejar otros posibles errores internos del servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al registrar usuario: " + e.getMessage());
        }
    }

    // --- ¡NUEVO ENDPOINT PARA OBTENER INFORMACIÓN DEL USUARIO Y SUS ROLES! ---
    @GetMapping("/userinfo")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> userInfo = new HashMap<>();
        if (userDetails != null) {
            userInfo.put("username", userDetails.getUsername());
            // Convertir las autoridades (roles) a una lista de cadenas
            // Spring Security prefija los roles con "ROLE_", el frontend esperará "ADMIN", "USER", etc.
            List<String> roles = userDetails.getAuthorities().stream()
                                            .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                                            .collect(Collectors.toList());
            userInfo.put("roles", roles);
        } else {
            // Esto no debería suceder en un endpoint @PreAuthorize, pero es una buena práctica
            userInfo.put("message", "No user authenticated");
        }
        return userInfo;
    }
}