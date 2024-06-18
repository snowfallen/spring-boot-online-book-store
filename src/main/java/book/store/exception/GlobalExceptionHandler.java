package book.store.exception;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<List<String>> handleRegistrationException(
            RegistrationException ex) {
        return new ResponseEntity<>(List.of(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<List<String>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        return new ResponseEntity<>(List.of(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    private String getErrorMessage(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            String field = fieldError.getField();
            String message = error.getDefaultMessage();
            return field + " " + message;
        }
        return error.getDefaultMessage();
    }
}
