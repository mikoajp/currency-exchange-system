package pl.aeh.currencyexchange.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.aeh.currencyexchange.dto.ErrorResponseDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST controllers
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle UserAlreadyExistsException
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExists(
            UserAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        log.error("User already exists: {}", ex.getMessage());
        
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handle InvalidCredentialsException and BadCredentialsException
     */
    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponseDto> handleInvalidCredentials(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Invalid credentials: {}", ex.getMessage());
        
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Invalid email or password",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handle UnauthorizedException
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {
        log.error("Unauthorized access: {}", ex.getMessage());
        
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handle ResourceNotFoundException and UsernameNotFoundException
     */
    @ExceptionHandler({ResourceNotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Resource not found: {}", ex.getMessage());
        
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        log.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Validation failed")
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        log.error("Illegal argument: {}", ex.getMessage());
        
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error occurred: ", ex);
        
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
