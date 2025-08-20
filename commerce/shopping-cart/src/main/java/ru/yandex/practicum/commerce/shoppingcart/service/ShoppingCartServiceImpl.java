package ru.yandex.practicum.commerce.shoppingcart.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.interactionapi.client.WarehouseClient;
import ru.yandex.practicum.commerce.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.interactionapi.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interactionapi.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.interactionapi.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.shoppingcart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.commerce.shoppingcart.model.ShoppingCart;
import ru.yandex.practicum.commerce.shoppingcart.repository.ShoppingCartRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper mapper;

    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException(username));
        return mapper.toShoppingCartDto(shoppingCartList);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products) {
        UUID cartId;
        Optional<List<ShoppingCart>> shoppingCartList = shoppingCartRepository.findByUsername(username);
        if (shoppingCartList.get().isEmpty()) {
            cartId = UUID.randomUUID();
        } else {
            cartId = shoppingCartList.get().getFirst().getShoppingCartId();
        }

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            ShoppingCart cart = new ShoppingCart();
            cart.setShoppingCartId(cartId);
            cart.setUsername(username);
            cart.setProductId(entry.getKey());
            cart.setQuantity(entry.getValue());
            ShoppingCart updProduct = shoppingCartRepository.save(cart);
        }
        ShoppingCartDto newCart = new ShoppingCartDto(cartId, username, products);

        BookedProductsDto bookedProductsDto = warehouseClient.checkProductQuantityEnoughForShoppingCart(newCart);
        return newCart;
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCart cart = shoppingCartRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));
        cart.setQuantity(request.getNewQuantity());
        ShoppingCart updCart = shoppingCartRepository.save(cart);
        return getShoppingCart(username);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        UUID shoppingCartId = null;
        for (UUID productId : productIds) {
            ShoppingCart cart = shoppingCartRepository.findByUsernameAndProductId(username, productId)
                    .orElseThrow(() -> new ProductNotFoundException(productId));
            shoppingCartId = cart.getShoppingCartId();
            shoppingCartRepository.delete(cart);
        }
        Optional<List<ShoppingCart>> cartsList = shoppingCartRepository.findByShoppingCartId(shoppingCartId);
        if (cartsList.get().isEmpty()) {
            return new ShoppingCartDto(shoppingCartId, username, Map.of());
        }
        return mapper.toShoppingCartDto(cartsList.get());
    }

    @Override
    public boolean deactivateCurrentShoppingCart(String username) {
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException(username));
        for (ShoppingCart cart : shoppingCartList) {
            shoppingCartRepository.delete(cart);
        }
        return true;
    }

}
