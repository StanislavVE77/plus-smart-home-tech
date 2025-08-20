package ru.yandex.practicum.commerce.shoppingcart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interactionapi.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interactionapi.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.interactionapi.interfase.ShoppingCartOperations;
import ru.yandex.practicum.commerce.shoppingcart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartOperations {
    private final ShoppingCartService shoppingCartService;

    @Override
    @GetMapping
    public ShoppingCartDto getShoppingCart(String username)
            throws NotAuthorizedUserException {
        log.info("--> GET запрос c username={}", username);
        ShoppingCartDto cart = shoppingCartService.getShoppingCart(username);
        log.info("<-- GET ответ cart={}", cart);
        return cart;
    }

    @Override
    @PutMapping
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products)
            throws NotAuthorizedUserException {
        log.info("--> PUT запрос c username={} и products={}", username, products);
        ShoppingCartDto cart = shoppingCartService.addProductToShoppingCart(username, products);
        log.info("<-- PUT ответ cart={}", cart);
        return cart;
    }

    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("--> POST запрос на изменение количества продукта в корзине: {}", request);
        ShoppingCartDto cart = shoppingCartService.changeProductQuantity(username, request);
        log.info("<-- POST ответ cart={}", cart);
        return cart;
    }

    @Override
    @PostMapping("/remove")
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        log.info("--> POST запрос на удаление продуктов из корзины: {}", productIds);
        ShoppingCartDto cart = shoppingCartService.removeFromShoppingCart(username, productIds);
        log.info("<-- POST ответ cart={}", cart);
        return cart;
    }

    @Override
    @DeleteMapping
    public boolean deactivateCurrentShoppingCart(String username) {
        log.info("--> DELETE запрос c username={}", username);
        boolean result = shoppingCartService.deactivateCurrentShoppingCart(username);
        log.info("<-- DELETE ответ {} : ", result);
        return result;
    }
}
