package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.interactionapi.dto.*;
import ru.yandex.practicum.commerce.interactionapi.interfase.WarehouseOperations;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/warehouse")
public class WarehouseController implements WarehouseOperations {
    private final WarehouseService warehouseService;

    @Override
    public WarehouseDto newProductInWarehouse(NewProductInWarehouseRequest request) {
        log.info("--> PUT запрос {} для добавление продукта на склад", request);
        WarehouseDto newProduct = warehouseService.newProductInWarehouse(request);
        log.info("<-- PUT ответ {}", newProduct);
        return newProduct;

    }

    @Override
    public WarehouseDto addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("--> POST запрос {} для изменения количества продукта на складе", request);
        WarehouseDto updProduct = warehouseService.addProductToWarehouse(request);
        log.info("<-- POST ответ {}", updProduct);
        return updProduct;
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("--> GET запрос для получения адреса склада");
        AddressDto address = warehouseService.getWarehouseAddress();
        log.info("<-- GET ответ {}", address);
        return address;
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto request) {
        log.info("--> POST запрос проверки товара на складе: {}", request);
        BookedProductsDto result = warehouseService.checkProductQuantityEnoughForShoppingCart(request);
        log.info("<-- POST ответ {}", result);
        return result;
    }
}
