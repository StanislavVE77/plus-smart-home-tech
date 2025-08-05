package ru.yandex.practicum.commerce.interactionapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductsInShoppingCartNotFound(NoProductsInShoppingCartException ex) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.name(),
                "The required object was not found.",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWarehouseProductNotFound(NoSpecifiedProductInWarehouseException ex) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.name(),
                "The required object was not found.",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFound(ProductNotFoundException ex) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.name(),
                "The required object was not found.",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUserNotAuthorized(NotAuthorizedUserException ex) {
        return new ErrorResponse(
                HttpStatus.UNAUTHORIZED.name(),
                "User not authorized.",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(CreateNewProductSericeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleNewProductSericeException(CreateNewProductSericeException ex) {
        log.error("500 Internal Server Error: {}", ex.getMessage(), ex);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "SERVICE-ERROR.",
                ex.getMessage() + "\n" + stackTrace,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        log.error("500 Internal Server Error: {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Error occurred.",
                e.getMessage() + "\n" + stackTrace,
                LocalDateTime.now()
        );
    }

}
