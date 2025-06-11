package com.vinimompox.products.application.dto;

import lombok.Data; // Para getters, setters, etc. (si tienes Lombok configurado)
import lombok.NoArgsConstructor; // Para el constructor vacío
import lombok.AllArgsConstructor; // Para el constructor con todos los campos

import jakarta.validation.constraints.NotEmpty; // Para validación de campos no vacíos
import jakarta.validation.constraints.Size;   // Para validación de tamaño mínimo de la contraseña

@Data // Anotación de Lombok: Genera automáticamente getters, setters, equals, hashCode y toString
@NoArgsConstructor // Anotación de Lombok: Genera un constructor sin argumentos
@AllArgsConstructor // Anotación de Lombok: Genera un constructor con todos los campos
public class RegisterRequest {

    @NotEmpty(message = "El nombre de usuario no puede estar vacío")
    private String username;

    @NotEmpty(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") // Asegura una longitud mínima
    private String password;

    @NotEmpty(message = "La confirmación de contraseña no puede estar vacía")
    private String confirmPassword; // ¡Este es el campo clave para la confirmación!
}