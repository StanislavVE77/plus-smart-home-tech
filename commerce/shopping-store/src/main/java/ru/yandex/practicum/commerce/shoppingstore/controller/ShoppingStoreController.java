package ru.yandex.practicum.commerce.shoppingstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.interactionapi.dto.*;
import ru.yandex.practicum.commerce.interactionapi.exception.CreateNewProductSericeException;
import ru.yandex.practicum.commerce.interactionapi.interfase.ShoppingStoreOperations;
import ru.yandex.practicum.commerce.shoppingstore.service.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-store")
public class ShoppingStoreController implements ShoppingStoreOperations {
    private final ShoppingStoreService shoppingStoreService;

    @Override
    public ProductDto createNewProduct(@Valid ProductDto product) throws CreateNewProductSericeException {
        log.info("--> PUT запрос {}", product);
        ProductDto newProduct = shoppingStoreService.createNewProduct(product);
        log.info("<-- PUT ответ {}", newProduct);
        return newProduct;
    }

    @Override
    public ProductDto updateProduct(@Valid ProductDto product) {
        log.info("--> POST запрос {}", product);
        ProductDto updProduct = shoppingStoreService.updateProduct(product);
        log.info("<-- POST ответ {}", updProduct);
        return updProduct;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("--> GET запрос c productId={}", productId);
        ProductDto product = shoppingStoreService.getProduct(productId);
        log.info("<-- GET ответ {}", product);
        return product;
    }

    @Override
    public PageDTO<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.info("--> GET запрос c category={}", category);
        Page<ProductDto> products = shoppingStoreService.getProducts(category, pageable);

        log.info("<-- GET ответ {}", products);
        return new PageDTO<>(products.getContent(), List.of(new SortDto(pageable.getSort().toList().get(0).getProperty(),
                pageable.getSort().toList().get(0).getDirection().toString())));
    }

    @Override
    public boolean setProductQuantityState(UUID productId, ProductQuantityState quantityState) {
        log.info("--> POST запрос c productId={} и QuantityState={}", productId, quantityState);
        boolean result = shoppingStoreService.setProductQuantityState(productId, quantityState);
        log.info("<-- POST ответ {} : ", result);
        return false;
    }

    @Override
    public boolean removeProductFromStore(@Valid UUID productId) {
        log.info("--> POST запрос на удаление продукта c productId={}", productId);
        boolean result = shoppingStoreService.removeProduct(productId);
        log.info("<-- POST ответ {} : ", result);
        return result;
    }
}
