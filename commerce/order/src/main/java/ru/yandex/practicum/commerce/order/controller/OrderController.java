package ru.yandex.practicum.commerce.order.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.interactionapi.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interactionapi.dto.order.OrderState;
import ru.yandex.practicum.commerce.interactionapi.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.interactionapi.interfase.OrderOperations;
import ru.yandex.practicum.commerce.order.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/order")
public class OrderController implements OrderOperations {
    private final OrderService orderService;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        log.info("--> GET запрос c username={}", username);
        List<OrderDto> orders = orderService.getClientOrders(username);
        log.info("<-- GET ответ orders={}", orders);
        return orders;
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("--> PUT запрос c  CreateNewOrderRequest={}", request);
        OrderDto order = orderService.createNewOrder(request);
        log.info("<-- PUT ответ order={}", order);
        return order;
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("--> POST запрос на возврат заказа: {}", request);
        OrderDto order = orderService.productReturn(request);
        log.info("<-- POST ответ order={}", order);
        return order;
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("--> POST запрос на оплату заказа: {}", orderId);
        OrderDto order = orderService.updateOrderStatus(orderId, OrderState.PAID);
        log.info("<-- POST ответ order={}", order);
        return order;
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("--> POST запрос при ошибке оплаты заказа: {}", orderId);
        OrderDto order = orderService.updateOrderStatus(orderId, OrderState.PAYMENT_FAILED);
        log.info("<-- POST ответ order={}", order);
        return order;
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("--> POST запрос при доставки заказа: {}", orderId);
        OrderDto order = orderService.updateOrderStatus(orderId, OrderState.DELIVERED);
        log.info("<-- POST ответ order={}", order);
        return order;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("--> POST запрос при ошибке доставки заказа: {}", orderId);
        OrderDto order = orderService.updateOrderStatus(orderId, OrderState.DELIVERY_FAILED);
        log.info("<-- POST ответ order={}", order);
        return order;
    }

    @Override
    public OrderDto complete(UUID orderId) {
        log.info("--> POST запрос при завершении заказа: {}", orderId);
        OrderDto order = orderService.updateOrderStatus(orderId, OrderState.COMPLETED);
        log.info("<-- POST ответ order={}", order);
        return order;
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("--> POST запрос при подсчете общей стоимости заказа: {}", orderId);
        OrderDto order = orderService.calculateTotalCost(orderId);
        log.info("<-- POST ответ order={}", order);
        return order;
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("--> POST запрос при подсчете стоимости доставки заказа: {}", orderId);
        OrderDto order = orderService.calculateDeliveryCost(orderId);
        log.info("<-- POST ответ order={}", order);
        return order;
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("--> POST запрос при сборки заказа: {}", orderId);
        OrderDto order = orderService.updateOrderStatus(orderId, OrderState.ASSEMBLED);
        log.info("<-- POST ответ order={}", order);
        return order;
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("--> POST запрос при ошибки сборки заказа: {}", orderId);
        OrderDto order = orderService.updateOrderStatus(orderId, OrderState.ASSEMBLY_FAILED);
        log.info("<-- POST ответ order={}", order);
        return order;
    }
}
