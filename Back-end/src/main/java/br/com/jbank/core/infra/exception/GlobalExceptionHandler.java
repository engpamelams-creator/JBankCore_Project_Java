package br.com.jbank.core.infra.exception;

import br.com.jbank.core.infra.response.JSendErrorResponse;
import br.com.jbank.core.infra.response.JSendFailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for standardizing API error responses using JSend format.
 * <p>
 * This handler intercepts exceptions thrown by controllers and converts them into
 * consistent JSend-formatted responses.
 * </p>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles business logic validation failures.
     * Returns HTTP 422 (Unprocessable Entity) with JSend fail response.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<JSendFailResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Business validation failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(JSendFailResponse.of(ex.getMessage()));
    }

    /**
     * Handles authentication failures.
     * Returns HTTP 401 (Unauthorized) with JSend fail response.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<JSendFailResponse> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(JSendFailResponse.of("Invalid credentials"));
    }

    /**
     * Handles ResponseStatusException (thrown by Spring Web).
     * Maps to JSend fail or error based on status code.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatus(ResponseStatusException ex) {
        log.warn("Response status exception: {} - {}", ex.getStatusCode(), ex.getReason());
        
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        
        // Client errors (4xx) are treated as "fail"
        if (status.is4xxClientError()) {
            return ResponseEntity
                    .status(status)
                    .body(JSendFailResponse.of(ex.getReason() != null ? ex.getReason() : "Request failed"));
        }
        
        // Server errors (5xx) are treated as "error"
        return ResponseEntity
                .status(status)
                .body(JSendErrorResponse.of(ex.getReason() != null ? ex.getReason() : "Internal server error"));
    }

    /**
     * Handles validation errors from @Valid annotations.
     * Returns HTTP 400 (Bad Request) with field-level error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JSendFailResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        log.warn("Validation failed: {}", errors);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(JSendFailResponse.of(errors));
    }

    /**
     * Catch-all handler for unexpected exceptions.
     * Returns HTTP 500 (Internal Server Error) with JSend error response.
     * <p>
     * IMPORTANT: Does not expose internal error details to the client for security.
     * </p>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<JSendErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(JSendErrorResponse.of("An unexpected error occurred. Please try again later."));
    }
}
