package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interactionapi.dto.AddressDto;
import ru.yandex.practicum.commerce.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interactionapi.dto.warehouse.*;
import ru.yandex.practicum.commerce.interactionapi.interfase.WarehouseOperations;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/warehouse")
public class WarehouseController implements WarehouseOperations {
    private final WarehouseService warehouseService;

    @Override
    @PutMapping
    public WarehouseDto newProductInWarehouse(NewProductInWarehouseRequest request) {
        log.info("--> PUT запрос {} для добавление продукта на склад", request);
        WarehouseDto newProduct = warehouseService.newProductInWarehouse(request);
        log.info("<-- PUT ответ {}", newProduct);
        return newProduct;

    }

    @Override
    @PostMapping("/add")
    public WarehouseDto addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("--> POST запрос {} для изменения количества продукта на складе", request);
        WarehouseDto updProduct = warehouseService.addProductToWarehouse(request);
        log.info("<-- POST ответ {}", updProduct);
        return updProduct;
    }

    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        log.info("--> GET запрос для получения адреса склада");
        AddressDto address = warehouseService.getWarehouseAddress();
        log.info("<-- GET ответ {}", address);
        return address;
    }

    @Override
    @PostMapping("/check")
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto request) {
        log.info("--> POST запрос проверки товара на складе: {}", request);
        BookedProductsDto result = warehouseService.checkProductQuantityEnoughForShoppingCart(request);
        log.info("<-- POST ответ {}", result);
        return result;
    }

    @Override
    @PostMapping("/shipped")
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info("--> POST запрос на передачу в доставку: {}", request);
        warehouseService.shippedToDelivery(request);
        log.info("<-- POST ответ ");
    }

    @Override
    @PostMapping("/return")
    public void acceptReturn(Map<UUID, Long> products) {
        log.info("--> POST запрос принять возврат товаров на склад: {}", products);
        warehouseService.acceptReturn(products);
        log.info("<-- POST ответ ");

    }

    @Override
    @PostMapping("/assembly")
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        log.info("--> POST запрос собрать товары к заказу для подготовки к отправке: {}", request);
        BookedProductsDto bookedProductsDto = warehouseService.assemblyProductsForOrder(request);
        log.info("<-- POST ответ {}", bookedProductsDto);
        return bookedProductsDto;
    }
}
