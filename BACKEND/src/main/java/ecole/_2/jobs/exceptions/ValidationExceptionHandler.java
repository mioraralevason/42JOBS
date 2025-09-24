package ecole._2.jobs.exceptions;

import ecole._2.jobs.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException ex) {
        // Récupère le premier message d’erreur seulement pour simplifier
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return new ApiResponse<>(false, null, message);
    }
}
