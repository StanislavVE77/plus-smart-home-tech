package ru.yandex.practicum.commerce.interactionapi.exception;

public class NoDeliveryFoundException extends RuntimeException{
    public NoDeliveryFoundException() {
        super("Доставка не найдена");
    }
}