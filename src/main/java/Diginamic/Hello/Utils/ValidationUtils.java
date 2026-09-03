package Diginamic.Hello.Utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.stream.Collectors;

/**
 * Petite fabrique de messages d'erreur à partir d'un BindingResult en erreur,
 * utilisée par les contrôleurs pour lever une exception métier lisible.
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static String extraireMessages(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }
}
