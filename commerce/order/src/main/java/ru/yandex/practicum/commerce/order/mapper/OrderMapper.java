package ru.yandex.practicum.commerce.order.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.interactionapi.dto.AddressDto;
import ru.yandex.practicum.commerce.interactionapi.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interactionapi.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.interactionapi.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interactionapi.dto.order.OrderState;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.model.OrderAddress;
import ru.yandex.practicum.commerce.order.model.OrderProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    public Order toOrder(CreateNewOrderRequest request) {
        UUID orderId = UUID.randomUUID();

        OrderAddress address = new OrderAddress();
        address.setAddressId(UUID.randomUUID());
        address.setCountry(request.getDeliveryAddress().getCountry());
        address.setCity(request.getDeliveryAddress().getCity());
        address.setStreet(request.getDeliveryAddress().getStreet());
        address.setHouse(request.getDeliveryAddress().getHouse());
        address.setFlat(request.getDeliveryAddress().getFlat());

        List<OrderProduct> products = new ArrayList<>();
        for (Map.Entry<UUID, Long> entry : request.getShoppingCart().getProducts().entrySet()) {
            OrderProduct product = new OrderProduct();
            product.setId(UUID.randomUUID());
            product.setProductId(entry.getKey());
            product.setQuantity(entry.getValue());
            products.add(product);
        }

        Order order = new Order();
        order.setOrderId(orderId);
        order.setState(OrderState.NEW);
        order.setUsername(request.getShoppingCart().getUsername());
        order.setShoppingCartId(request.getShoppingCart().getShoppingCartId());
        order.setOrderAddress(address);
        order.setProducts(products);

        return order;
    }

    public OrderDto toOrderDto(Order order) {
        Map<UUID, Long> products = order.getProducts().stream()
                .collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity));

        return new OrderDto(
                order.getOrderId(),
                order.getShoppingCartId(),
                products,
                order.getPaymentId(),
                order.getDeliveryId(),
                order.getState(),
                order.getDeliveryWeight(),
                order.getDeliveryVolume(),
                order.isFragile(),
                order.getTotalPrice(),
                order.getDeliverPrice(),
                order.getProductPrice()
        );
    }

    public DeliveryDto toDeliveryDto(Order order, AddressDto fromAddress) {
        DeliveryDto deliveryDto = new DeliveryDto();

        AddressDto toAddress = new AddressDto();
        toAddress.setCountry(order.getOrderAddress().getCountry());
        toAddress.setCity(order.getOrderAddress().getCity());
        toAddress.setStreet(order.getOrderAddress().getStreet());
        toAddress.setHouse(order.getOrderAddress().getHouse());
        toAddress.setFlat(order.getOrderAddress().getFlat());

        deliveryDto.setDeliveryId(UUID.randomUUID());
        deliveryDto.setDeliveryState(DeliveryState.CREATED);
        deliveryDto.setOrderId(order.getOrderId());
        deliveryDto.setToAddress(toAddress);
        deliveryDto.setFromAddress(fromAddress);

        return deliveryDto;
    }
}
