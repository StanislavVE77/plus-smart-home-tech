package ru.yandex.practicum.commerce.interactionapi.exception;

import java.util.UUID;

public class ProductInShoppingCartNotInWarehouse extends RuntimeException{
    public ProductInShoppingCartNotInWarehouse(UUID productId) {
        super("Ошибка, товара " + productId + " из корзины нет на складе");
    }
}