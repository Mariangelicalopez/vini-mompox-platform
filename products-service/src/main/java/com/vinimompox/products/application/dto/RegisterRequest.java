// products-service/src/main/java/com/vinimompox/products/application/dto/RegisterRequest.java
package com.vinimompox.products.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import com.vinimompox.products.application.validation.PasswordMatches; // <-- ¡Esta es la clave!

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches // <-- Añadir esta anotación a nivel de clase
public class RegisterRequest {

    @NotEmpty(message = "El nombre de usuario no puede estar vacío.")
    private String username;

    @NotEmpty(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password;

    @NotEmpty(message = "La confirmación de contraseña no puede estar vacía.")
    private String confirmPassword; // <-- Añadir este campo
}