package ru.yandex.practicum.commerce.interactionapi.exception;

public class NotAuthorizedUserException extends RuntimeException {
    public NotAuthorizedUserException(String username) {
        super("Пользователь " + username + " не авторизован");
    }
}