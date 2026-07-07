package gr.lykost.pms.core.exceptions;

import gr.lykost.pms.dto.readonlydto.ApiErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorDTO handleNotFound(ResourceNotFoundException e) {
        return ApiErrorDTO.of(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorDTO handleDuplicate(DuplicateResourceException e) {
        return ApiErrorDTO.of(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(InvalidOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDTO handleInvalidOperation(InvalidOperationException e) {
        return ApiErrorDTO.of(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiErrorDTO handleUnauthorized(UnauthorizedException e) {
        return ApiErrorDTO.of(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorDTO handleDataIntegrity(DataIntegrityViolationException e) {
        return ApiErrorDTO.of("Conflict",
                "The operation violates a data constraint — the resource may still be referenced by other records");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDTO handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ApiErrorDTO("Validation Failed", "One or more fields are invalid", fieldErrors);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorDTO handleAuthentication(AuthenticationException e) {
        return ApiErrorDTO.of("Authentication Failed", "Invalid username or password");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiErrorDTO handleAccessDenied(AccessDeniedException e) {
        return ApiErrorDTO.of("Access Denied", "You do not have permission to perform this action");
    }

    @ExceptionHandler(AppServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorDTO handleAppServer(AppServerException e) {
        log.error("Server error: {}", e.getMessage(), e);
        return ApiErrorDTO.of(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorDTO handleUnexpected(Exception e) {
        log.error("Unexpected error", e);
        return ApiErrorDTO.of("Internal Server Error", "An unexpected error occurred");
    }
}
