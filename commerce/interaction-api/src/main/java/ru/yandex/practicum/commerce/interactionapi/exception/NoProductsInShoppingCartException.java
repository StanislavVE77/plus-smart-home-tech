package ru.yandex.practicum.commerce.interactionapi.exception;

public class NoProductsInShoppingCartException extends RuntimeException {
    public NoProductsInShoppingCartException(String username) {
        super("Корзина продуктов для пользователя " + username + " не найдена");
    }
}