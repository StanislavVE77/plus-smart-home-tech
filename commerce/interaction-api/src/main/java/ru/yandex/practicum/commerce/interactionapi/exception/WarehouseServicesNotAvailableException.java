package ru.yandex.practicum.commerce.interactionapi.exception;

public class WarehouseServicesNotAvailableException extends RuntimeException{
    public WarehouseServicesNotAvailableException() {
        super("Сервис проверки количества продуктов на складе не доступен.");
    }
}