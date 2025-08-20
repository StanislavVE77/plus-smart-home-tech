package ru.yandex.practicum.commerce.order.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.interactionapi.client.DeliveryClient;
import ru.yandex.practicum.commerce.interactionapi.client.PaymentClient;
import ru.yandex.practicum.commerce.interactionapi.client.WarehouseClient;
import ru.yandex.practicum.commerce.interactionapi.dto.AddressDto;
import ru.yandex.practicum.commerce.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.interactionapi.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interactionapi.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interactionapi.dto.order.OrderState;
import ru.yandex.practicum.commerce.interactionapi.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.interactionapi.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.interactionapi.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.interactionapi.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.interactionapi.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.model.OrderProduct;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.*;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper mapper;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final WarehouseClient warehouseClient;

    @Override
    public List<OrderDto> getClientOrders(String username)  throws NotAuthorizedUserException {
        if (username != null && !username.isBlank()) {
            Optional<List<Order>> ordersList = orderRepository.findByUsername(username);
            return ordersList.get().stream().map(mapper::toOrderDto).toList();
        }
        throw new NotAuthorizedUserException(username);
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        Order order = mapper.toOrder(request);
        try {
            BookedProductsDto bookedProductsDto = warehouseClient.assemblyProductsForOrder(
                    new AssemblyProductsForOrderRequest(order.getOrderId(), request.getShoppingCart().getProducts())
            );
            order.setDeliveryVolume(bookedProductsDto.getDeliveryVolume());
            order.setDeliveryWeight(bookedProductsDto.getDeliveryWeight());
            order.setFragile(bookedProductsDto.isFragile());
            try {
                BigDecimal productsPrice = paymentClient.productCost(mapper.toOrderDto(order));
                order.setProductPrice(productsPrice);
                try {
                    AddressDto fromAddress = warehouseClient.getWarehouseAddress();
                    DeliveryDto deliveryDto = mapper.toDeliveryDto(order, fromAddress);
                    DeliveryDto updDeliveryDto = deliveryClient.planDelivery(deliveryDto);
                    order.setDeliveryId(updDeliveryDto.getDeliveryId());
                } catch (Exception e) {
                    order.setState(OrderState.DELIVERY_FAILED);
                }

            } catch (Exception e) {
                order.setState(OrderState.PAYMENT_FAILED);
            }
        } catch (Exception e) {
            order.setState(OrderState.ASSEMBLY_FAILED);
        }
        order = orderRepository.save(order);
        return mapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(() -> new NoOrderFoundException(request.getOrderId()));
        order.setState(OrderState.CANCELED);
        Order updOrder = orderRepository.save(order);
        List<OrderProduct> products = order.getProducts();
        List<OrderProduct> newProducts = new ArrayList<>();
        for (OrderProduct curProduct : products) {
            for (Map.Entry<UUID, Long> entry : request.getProducts().entrySet()) {
                if (curProduct.getProductId().equals(entry.getKey())) {
                    long newQuantity = curProduct.getQuantity() - entry.getValue();
                    if (newQuantity > 0) {
                        curProduct.setQuantity(newQuantity);
                        newProducts.add(curProduct);
                    }
                }
            }
        }
        order.setProducts(newProducts);
        order.setState(OrderState.PRODUCT_RETURNED);

        Order updOrder = orderRepository.save(order);
        return mapper.toOrderDto(updOrder);
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(UUID orderId, OrderState state) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NoOrderFoundException(orderId));
        if (state.equals(OrderState.ON_PAYMENT)) {
            PaymentDto paymentDto = paymentClient.payment(mapper.toOrderDto(order));
            order.setPaymentId(paymentDto.getPaymentId());
        }
        order.setState(state);

        Order updOrder = orderRepository.save(order);
        return mapper.toOrderDto(updOrder);

    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NoOrderFoundException(orderId));
        BigDecimal totalCost = paymentClient.getTotalCost(mapper.toOrderDto(order));
        order.setTotalPrice(totalCost);
        Order updOrder = orderRepository.save(order);
        return mapper.toOrderDto(updOrder);
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NoOrderFoundException(orderId));
        BigDecimal deliveryCost = deliveryClient.deliveryCost(mapper.toOrderDto(order));

        order.setDeliverPrice(deliveryCost);
        Order updOrder = orderRepository.save(order);
        return mapper.toOrderDto(updOrder);
    }
}
