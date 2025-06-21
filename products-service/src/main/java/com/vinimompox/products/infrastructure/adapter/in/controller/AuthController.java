package com.vinimompox.products.infrastructure.adapter.in.controller;

// Importaciones existentes
import com.vinimompox.products.domain.model.User; // Importa tu User de domain
import com.vinimompox.products.application.dto.RegisterRequest;
import com.vinimompox.products.application.service.UserService; // Importa tu UserService de application

import jakarta.validation.Valid; // Necesario para la validación del DTO
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// ¡NUEVAS IMPORTACIONES PARA EL MANEJO DE ERRORES DE VALIDACIÓN!
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        // --- IMPORTANTE: ELIMINAMOS LA VALIDACIÓN MANUAL DE CONTRASEÑAS AQUÍ ---
        // La anotación @PasswordMatches en RegisterRequest y el @ExceptionHandler
        // que añadiremos abajo se encargarán de esto automáticamente.
        // if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
        //     return new ResponseEntity<>("Las contraseñas no coinciden.", HttpStatus.BAD_REQUEST);
        // }

        try {
            User registeredUser = userService.registerNewUser(
                registerRequest.getUsername(),
                registerRequest.getPassword()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente: " + registeredUser.getUsername());
        } catch (RuntimeException e) {
            // Manejar caso donde el nombre de usuario ya existe o cualquier otro error de negocio del servicio
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Manejar otros posibles errores internos del servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al registrar usuario: " + e.getMessage());
        }
    }

    // --- ¡NUEVO MANEJADOR DE ERRORES DE VALIDACIÓN PARA EL DTO! ---
    // Este método se ejecutará automáticamente si @Valid detecta errores en el RegisterRequest
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Envía un 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField(); // Obtiene el nombre del campo (ej. "password", "confirmPassword")
            String errorMessage = error.getDefaultMessage(); // Obtiene el mensaje que definimos en las anotaciones (@Size, @NotEmpty, @PasswordMatches)
            errors.put(fieldName, errorMessage);
        });
        // Este mapa se devolverá como JSON al frontend, permitiéndole mostrar errores específicos por campo.
        // Ejemplo de respuesta: {"password": "La contraseña debe tener al menos 6 caracteres", "confirmPassword": "Las contraseñas no coinciden."}
        return errors;
    }

    // --- ENDPOINT PARA OBTENER INFORMACIÓN DEL USUARIO Y SUS ROLES ---
    @GetMapping("/userinfo")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> userInfo = new HashMap<>();
        if (userDetails != null) {
            userInfo.put("username", userDetails.getUsername());
            List<String> roles = userDetails.getAuthorities().stream()
                                            .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                                            .collect(Collectors.toList());
            userInfo.put("roles", roles);
        } else {
            userInfo.put("message", "No user authenticated"); // Esto no debería pasar con seguridad configurada
        }
        return userInfo;
    }
}