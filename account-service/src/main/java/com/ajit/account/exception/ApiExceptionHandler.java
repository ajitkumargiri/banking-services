package com.ajit.account.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Api exception handler.
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                    HttpHeaders headers, HttpStatus status, WebRequest request) {
        // Collecting all validation error messages.
        List<String> errors = ex.getBindingResult().getFieldErrors()
                        .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        // Displaying all error messages separated by commas.
        Error error = new Error(HttpStatus.BAD_REQUEST.value(),
                        new Date(),
                        "Request is not correct", String.join(",", errors));
        return handleExceptionInternal(ex, error,
                        new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Resource not found exception error.
     *
     * @param ex the ex
     * @return the error
     */
    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Error resourceNotFoundException(ResourceNotFoundException ex) {
        return new Error(HttpStatus.NOT_FOUND.value(),
                        new Date(),
                        "Resource Not Found", ex.getMessage());
    }

    /**
     * Bad request exception error.
     *
     * @param ex the ex
     * @return the error
     */
    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Error badRequestException(BadRequestException ex) {
        return new Error(HttpStatus.BAD_REQUEST.value(),
                        new Date(),
                        "Request is not correct", ex.getMessage());
    }

    /**
     * Insufficient balance exception error.
     *
     * @param ex the ex
     * @return the error
     */
    @ExceptionHandler(value = InsufficientBalanceException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Error insufficientBalanceException(InsufficientBalanceException ex) {
        return new Error(HttpStatus.BAD_REQUEST.value(),
                        new Date(),
                        "Insufficient Balance", ex.getMessage());
    }

    /**
     * Invocation target exception error.
     *
     * @param ex the ex
     * @return the error
     */
    @ExceptionHandler(value = InvocationTargetException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Error invocationTargetException(InvocationTargetException ex) {
        return new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        new Date(),
                        "An unexpected error occurred", ex.getMessage());
    }

    /**
     * Illegal state exception error.
     *
     * @param ex the ex
     * @return the error
     */
    @ExceptionHandler(value = IllegalStateException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Error illegalStateException(IllegalStateException ex) {
        return new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        new Date(),
                        "An unexpected error occurred", ex.getMessage());
    }

    /**
     * Null pointer exception error.
     *
     * @param ex the ex
     * @return the error
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Error nullPointerException(NullPointerException ex) {
        return new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        new Date(),
                        "An unexpected error occurred", ex.getMessage());
    }
}