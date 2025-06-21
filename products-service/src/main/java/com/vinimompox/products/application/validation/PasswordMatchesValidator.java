package com.vinimompox.products.application.validation;

import com.vinimompox.products.application.dto.RegisterRequest; // Importa tu DTO
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        // No se necesita inicialización para este validador
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (!(obj instanceof RegisterRequest)) {
            // Este validador está destinado a RegisterRequest. Si se aplica en otro lugar, no validará.
            return false;
        }
        RegisterRequest user = (RegisterRequest) obj;
        // La lógica central: verifica si la contraseña y la confirmación de contraseña no son nulas y coinciden.
        boolean isValid = user.getPassword() != null && user.getPassword().equals(user.getConfirmPassword());

        // Si la validación falla, personaliza el mensaje de error para adjuntarlo al campo confirmPassword.
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode("confirmPassword") // Adjunta el error al campo confirmPassword
                   .addConstraintViolation();
        }

        return isValid;
    }
}