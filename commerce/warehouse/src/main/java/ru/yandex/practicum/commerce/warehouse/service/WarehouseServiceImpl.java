package ru.yandex.practicum.commerce.warehouse.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.interactionapi.dto.AddressDto;
import ru.yandex.practicum.commerce.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interactionapi.dto.warehouse.*;
import ru.yandex.practicum.commerce.interactionapi.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.interactionapi.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.interactionapi.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.commerce.warehouse.model.OrderBooking;
import ru.yandex.practicum.commerce.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.commerce.warehouse.repository.OrderBookingRepository;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];
    private WarehouseRepository warehouseRepository;
    private OrderBookingRepository orderBookingRepository;
    private WarehouseMapper mapper;

    @Override
    public WarehouseDto newProductInWarehouse(NewProductInWarehouseRequest request) {
        WarehouseProduct product = mapper.toProduct(request);
        product = warehouseRepository.save(product);
        return mapper.toWarehouseDto(product);

    }

    @Override
    public WarehouseDto addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = warehouseRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(request.getProductId()));
        product.setQuantity(request.getQuantity());
        WarehouseProduct updProduct = warehouseRepository.save(product);
        return mapper.toWarehouseDto(updProduct);
    }

    @Override
    public AddressDto getWarehouseAddress() {

        return mapper.toAddressDto(CURRENT_ADDRESS);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto request) {
        Double weight = 0D;
        Double volume = 0D;
        boolean fragile = false;
        log.info("Проверка достаточного количества товаров для корзины {}", request.getShoppingCartId());
        for (UUID productId : request.getProducts().keySet()) {
            WarehouseProduct product = warehouseRepository.findById(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(productId));
            Long cartQuantity = request.getProducts().get(productId);
            Long warehouseQuantity = product.getQuantity();
            if (warehouseQuantity < cartQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(productId);
            }
            weight += product.getWeight() * cartQuantity;
            volume += product.getDepth() * product.getWidth() * product.getHeight() * cartQuantity;
            fragile = fragile || product.isFragile();
        }
        return new BookedProductsDto(weight, volume, fragile);

    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        OrderBooking orderBooking = orderBookingRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException(request.getOrderId()));
        orderBooking.setDeliveryId(request.getDeliveryId());
        OrderBooking updOrderBooking = orderBookingRepository.save(orderBooking);
    }

    @Override
    public void acceptReturn(Map<UUID, Long> products) {
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            WarehouseProduct product = warehouseRepository.findById(entry.getKey())
                    .orElseThrow(() -> new NoOrderFoundException(entry.getKey()));
            Long newQuantity = product.getQuantity() + entry.getValue();
            product.setQuantity(newQuantity);
            WarehouseProduct newProduct = warehouseRepository.save(product);
        }
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Double weight = 0D;
        Double volume = 0D;
        boolean fragile = false;
        for (UUID productId : request.getProducts().keySet()) {
            WarehouseProduct product = warehouseRepository.findById(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(productId));
            Long requestQuantity = request.getProducts().get(productId);
            Long warehouseQuantity = product.getQuantity();
            if (warehouseQuantity < requestQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(productId);
            }
            Long resultQuantity = warehouseQuantity - requestQuantity;
            product.setQuantity(resultQuantity);
            WarehouseProduct updProduct = warehouseRepository.save(product);
            weight += product.getWeight() * requestQuantity;
            volume += product.getDepth() * product.getWidth() * product.getHeight() * requestQuantity;
            fragile = fragile || product.isFragile();
        }
        OrderBooking orderBooking = orderBookingRepository.save(mapper.toOrderBooking(request));
        return new BookedProductsDto(weight, volume, fragile);
    }
}
